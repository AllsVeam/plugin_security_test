package demo.app.user.service;


import demo.app.apiResponse.ApiResponse;
import demo.app.user.roles.RoleGrantRequest;
import demo.app.user.dto.ResponseZitadelDTO;
import demo.app.user.dto.UpdateUserRequest;
import demo.app.user.dto.UserDTO;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<ApiResponse<Object>> createUser(UserDTO userDTO);
    ResponseEntity<ApiResponse<ResponseZitadelDTO>> getUser(String id);
    String obtenerToken();
    String updateUser(UpdateUserRequest request);
    String updatePass(UpdateUserRequest request);
    ResponseEntity<ApiResponse<Object>> deleteUser(Long userId);
    ResponseEntity<ApiResponse<Object>> desactivate(Long userId);
    ResponseEntity<ApiResponse<Object>> reactivate(Long userId);
    ResponseEntity<ApiResponse<Object>> getUserById(Long userId);
    ResponseEntity<ApiResponse<Object>> assignRolesToUser(RoleGrantRequest data);
}
