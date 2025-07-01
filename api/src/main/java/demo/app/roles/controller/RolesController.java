package demo.app.roles.controller;

import demo.app.apiResponse.ApiResponse;
import demo.app.roles.dto.RoleRequest;
import demo.app.roles.service.RolesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
public class RolesController {

    @Autowired
    private RolesService rolesService;

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> listRoles() {
        return rolesService.getRoles();
    }

    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> createRol(@RequestBody RoleRequest data) {
        return rolesService.createRol(data);
    }

    @DeleteMapping("/{roleKey}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> deleteRol(@PathVariable String roleKey) {
        return rolesService.deleteRol(roleKey);
    }

    @PutMapping("/{roleKey}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> updateRol(@PathVariable String roleKey, @RequestBody RoleRequest data) {
        return rolesService.updateRol(roleKey, data);
    }

}
