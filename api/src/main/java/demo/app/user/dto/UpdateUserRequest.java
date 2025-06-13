package demo.app.user.dto;
// dto/UpdateUserRequest.java
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateUserRequest {
    public String userId;
    public String token;
    public EmailDTO email;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public EmailDTO getEmail() {
        return email;
    }

    public void setEmail(EmailDTO email) {
        this.email = email;
    }

    public PhoneDTO getPhone() {
        return phone;
    }

    public void setPhone(PhoneDTO phone) {
        this.phone = phone;
    }

    public ProfileDTO getProfile() {
        return profile;
    }

    public void setProfile(ProfileDTO profile) {
        this.profile = profile;
    }

    public PasswordDTO getPassword() {
        return password;
    }

    public void setPassword(PasswordDTO password) {
        this.password = password;
    }

    public PhoneDTO phone;
    public ProfileDTO profile;
    public PasswordDTO password;
}