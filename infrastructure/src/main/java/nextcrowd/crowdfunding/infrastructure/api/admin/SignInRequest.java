package nextcrowd.crowdfunding.infrastructure.api.admin;

public record SignInRequest(String username, String password, String fullName, boolean isProjectOwner, boolean isBaker) {

}
