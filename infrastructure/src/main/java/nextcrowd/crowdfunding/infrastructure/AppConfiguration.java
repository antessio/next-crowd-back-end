package nextcrowd.crowdfunding.infrastructure;

import java.nio.file.Path;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import nextcrowd.crowdfunding.infrastructure.storage.FileStorageService;
import nextcrowd.crowdfunding.infrastructure.storage.FileSystemStorageService;
import nextcrowd.crowdfunding.infrastructure.storage.StorageUtils;

@Configuration
public class AppConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }


    public FileSystemStorageService fileSystemStorageService(
            @Value("${storage.directory}") Path storageDirectory,
            @Value("${storage.base-url}") String baseUrl) {
        return new FileSystemStorageService(storageDirectory, baseUrl);
    }

    @Bean
    public Map<StorageUtils.StorageLocation, FileStorageService> getStorageServices(@Value("${storage.directory}") Path storageDirectory,
                                                                                    @Value("${storage.base-url}") String baseUrl) {
        return Map.of(StorageUtils.StorageLocation.FS, fileSystemStorageService(storageDirectory, baseUrl));
    }

}
