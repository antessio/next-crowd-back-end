package nextcrowd.crowdfunding.infrastructure;

import static org.springframework.security.config.Customizer.withDefaults;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import nextcrowd.crowdfunding.infrastructure.security.http.JwtAuthenticationFilter;
import nextcrowd.crowdfunding.infrastructure.security.persistence.Role;
import nextcrowd.crowdfunding.infrastructure.security.persistence.UserRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${security.allowedOrigins}") String allowedOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();

        // Set allowed origins
        configuration.setAllowedOrigins(Optional.ofNullable(allowedOrigins)
                                                .filter(Predicate.not(String::isBlank))
                                                .map(s -> s.split(","))
                                                .map(List::of)
                                                .orElseGet(List::of));

        // Allow credentials if needed
        configuration.setAllowCredentials(true);

        // Set allowed methods (e.g., GET, POST, etc.)
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Set allowed headers
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/dashboard/login").permitAll()
                        .requestMatchers("/dashboard/sign_in").permitAll()
                        .requestMatchers("/dashboard/user").hasAnyAuthority(Role.ADMIN, Role.PROJECT_OWNER, Role.BAKER)
                        .requestMatchers("/admin/upload").hasAnyAuthority(Role.ADMIN, Role.PROJECT_OWNER, Role.BAKER)
                        .requestMatchers("/admin/**").hasAuthority(Role.ADMIN)
                        .requestMatchers("/projectOwner/**").hasAuthority(Role.PROJECT_OWNER)
                        .requestMatchers("/bakers/**").hasAuthority(Role.BAKER)
                        .requestMatchers("/public/**").permitAll()
                        .anyRequest().denyAll())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByEmail(username)
                                         .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider(UserRepository userRepository) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService(userRepository));
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

}
