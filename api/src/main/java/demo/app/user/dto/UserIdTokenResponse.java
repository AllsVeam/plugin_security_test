package demo.app.user.dto;

public class UserIdTokenResponse {
    private String userId;
    private String token;

    // Constructor
    public UserIdTokenResponse(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    // Getters y setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}

