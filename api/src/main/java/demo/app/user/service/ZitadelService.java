package demo.app.user.service;


import demo.app.apiResponse.ApiResponse;
import demo.app.user.roles.RoleGrantRequest;
import demo.app.user.roles.RoleRequest;
import demo.app.user.dto.ResponseZitadelDTO;
import demo.app.user.dto.UpdateUserRequest;
import demo.app.user.dto.UserDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ZitadelService{
    ResponseEntity<ApiResponse<Object>> createUser(UserDTO userDTO);
    ResponseEntity<ApiResponse<ResponseZitadelDTO>> getUser(String id);
    String obtenerToken();
    String updateUser(UpdateUserRequest request);
    ResponseEntity<ApiResponse<Object>> deleteUser(Long userId);
    ResponseEntity<ApiResponse<Object>> desactivate(Long userId);
    ResponseEntity<ApiResponse<Object>> reactivate(Long userId);
    ResponseEntity<ApiResponse<Object>> getUserById(Long userId);
    ResponseEntity<ApiResponse<Object>> createRol(RoleRequest data);
    ResponseEntity<ApiResponse<Object>> deleteRol(String roleKey);
    ResponseEntity<ApiResponse<Object>> updateRol(String roleKey, RoleRequest data);
    ResponseEntity<ApiResponse<Object>> assignRolesToUser(RoleGrantRequest data);
    List<Map<String, Object>> getRoles();
}
