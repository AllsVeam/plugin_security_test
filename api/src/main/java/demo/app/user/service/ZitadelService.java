package demo.app.user.service;


import demo.app.apiResponse.ApiResponse;
import demo.app.user.dto.UpdateUserRequest;
import demo.app.user.dto.UserDTO;
import org.springframework.http.ResponseEntity;

public interface ZitadelService{
    void createUser(UserDTO userDTO);
    ResponseEntity<ApiResponse<Object>> getUser();
    String obtenerToken();
    String updateUser(UpdateUserRequest request);
    ResponseEntity<ApiResponse<Object>> deleteUser(Long userId);
    ResponseEntity<ApiResponse<Object>> desactivate(Long userId);
    ResponseEntity<ApiResponse<Object>> reactivate(Long userId);
    ResponseEntity<ApiResponse<Object>> getUserById(Long userId);
}
