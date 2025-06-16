package demo.app.user.service;


import demo.app.apiResponse.ApiResponse;
import demo.app.user.dto.UserDTO;
import org.springframework.http.ResponseEntity;

public interface ZitadelService {
    public ResponseEntity<ApiResponse<UserDTO>> createUser(UserDTO userDTO);
    public ResponseEntity<ApiResponse<Object>> getUser();
    public ResponseEntity<ApiResponse<Object>> deleteUser(Long userId);
    ResponseEntity<ApiResponse<Object>> desactivate(Long userId);
    ResponseEntity<ApiResponse<Object>> reactivate(Long userId);
}
