package demo.app.user.service;


import demo.app.apiResponse.ApiResponse;
import demo.app.apiResponse.ApiResponsePass;
import demo.app.user.dto.*;
import demo.app.user.roles.RoleGrantRequest;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface UserService {
    ResponseEntity<ApiResponse<Object>> createUser(UserDTO userDTO);
    ResponseEntity<ApiResponse<ResponseZitadelDTO>> getUser(String id);
    String obtenerToken();
    String updateUser(UpdateUserRequest request);
    ResponseEntity<ApiResponsePass> updatePass(Map<String, Object> jsonBody);
    ResponseEntity<ApiResponse<Object>> deleteUser(Long userId);
    ResponseEntity<ApiResponse<Object>> desactivate(Long userId);
    ResponseEntity<ApiResponse<Object>> reactivate(Long userId);
    ResponseEntity<ApiResponse<Object>> getUserById(Long userId);
    ResponseEntity<ApiResponse<Object>> assignRolesToUser(RoleGrantRequest data);
    ResponseEntity<ApiResponse<Object>> updateRolesToUser(RoleGrantRequest data);
    ResponseEntity<ApiResponse<Object>> createUserBD(AppUserRequest request);
    ResponseEntity<ApiResponse<Object>> updateOfficeAndStaffToUser(OfficeUpdateRequest data);
    ResponseEntity<ApiResponse<Object>> getDatosExtraUsuario(String userId);
}
