package demo.app.user.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.apiResponse.ApiResponse;
import demo.app.apiResponse.ApiResponsePass;
import demo.app.user.dto.*;
import demo.app.user.roles.RoleGrantRequest;
import demo.app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    @PreAuthorize("isAuthenticated()")
    public String helloWorld() {
        return "Hello World get";
    }

    @PostMapping("/crear")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> crearUsuario(@RequestBody UserDTO dto) {
            return userService.createUser(dto);
    }

    @GetMapping("/")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResponseZitadelDTO>> getAllUsers() {
        return userService.getUser(null);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updateUser(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Datos recibidos";
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ResponseZitadelDTO>> getUser(@PathVariable String id) {
        return userService.getUser(id);
    }

    @PutMapping("/update-user")
    @PreAuthorize("isAuthenticated()")
    public String updateUser(@RequestBody UpdateUserRequest request) {
        return userService.updateUser(request);
    }

    @PutMapping("/update-passUser")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponsePass> updatePass(@RequestBody Map<String, Object> request) {
        return userService.updatePass(request);
    }

    @PostMapping("/Obtenertoken")
    @PreAuthorize("isAuthenticated()")
    public String obtenerToken() {
        return userService.obtenerToken();
    }

    @DeleteMapping("/")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@RequestParam Long userId) {
        return userService.deleteUser(userId);
    }

    @PutMapping("/desactivate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> desactivateUser(@RequestParam Long userId) {
        return userService.desactivate(userId);
    }

    @PutMapping("/reactivate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> reactivateUser(@RequestParam Long userId) {
        return userService.reactivate(userId);
    }

    @PostMapping("/assign-roles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> assignRolesToUser(@RequestBody RoleGrantRequest data) {
        return userService.assignRolesToUser(data);
    }

    @PutMapping("/update-roles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> updateRolesToUser(@RequestBody RoleGrantRequest data) {
        return userService.updateRolesToUser(data);
    }

    @PutMapping("/update-office")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> updateOfficeAndStaffToUser(@RequestBody OfficeUpdateRequest data) {
        return userService.updateOfficeAndStaffToUser(data);
    }


    @PostMapping("/CrearBD")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> createUserBD(@RequestBody AppUserRequest request) {
        return userService.createUserBD(request);
    }

    @GetMapping("/dataUserBD/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> getDatosExtraUsuario(@PathVariable String userId) {
        return userService.getDatosExtraUsuario(userId);
    }
}
