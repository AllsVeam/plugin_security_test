package demo.app.roles.service;

import demo.app.apiResponse.ApiResponse;

import demo.app.roles.dto.RoleRequest;
import org.springframework.http.ResponseEntity;

public interface RolesService {
    ResponseEntity<ApiResponse<Object>> getRoles();
    ResponseEntity<ApiResponse<Object>> createRol(RoleRequest data);
    ResponseEntity<ApiResponse<Object>> deleteRol(String roleKey);
    ResponseEntity<ApiResponse<Object>> updateRol(String roleKey, RoleRequest data);
}
