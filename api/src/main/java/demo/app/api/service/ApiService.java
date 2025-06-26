package demo.app.api.service;

import demo.app.api.dto.UserDetailsDTO;
import demo.app.apiResponse.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ApiService {
    ResponseEntity<ApiResponse<UserDetailsDTO>> userDetails(Map<String, String> tokenMap);
    ResponseEntity<ApiResponse<UserDetailsDTO>> mapToken(Map<String, Object> tokenPayload);
    ResponseEntity<?> getToken(Map<String, String> payload);
    ResponseEntity<String> getProjectRoles();


}
