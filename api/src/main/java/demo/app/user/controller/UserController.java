package demo.app.user.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.apiResponse.ApiResponse;
import demo.app.user.roles.RoleGrantRequest;
import demo.app.user.dto.ResponseZitadelDTO;
import demo.app.user.dto.UpdateUserRequest;
import demo.app.user.dto.UserDTO;
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
    public ResponseEntity<ApiResponse<Object>> crearUsuario(@RequestBody UserDTO dto) {
            return userService.createUser(dto);
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<ResponseZitadelDTO>> getAllUsers() {
        return userService.getUser(null);
    }

    @PutMapping("/{id}")
    public String updateUser(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(payload);
            System.out.println("JSON bonito:\n" + json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Datos recibidos";
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResponseZitadelDTO>> getUser(@PathVariable String id) {
        return userService.getUser(id);
    }

    @PutMapping("/update-user")
    public String updateUser(@RequestBody UpdateUserRequest request) {
        return userService.updateUser(request);
    }

    @PutMapping("/update-passUser")
    public String updatePass(@RequestBody UpdateUserRequest request){
        return userService.updatePass(request);
    }


    @PostMapping("/Obtenertoken")
    public String obtenerToken() {
        return userService.obtenerToken();
    }

    @DeleteMapping("/")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@RequestParam Long userId) {
        return userService.deleteUser(userId);
    }

    @PutMapping("/desactivate")
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> desactivateUser(@RequestParam Long userId) {
        return userService.desactivate(userId);
    }

    @PutMapping("/reactivate")
    public ResponseEntity<ApiResponse<Object>> reactivateUser(@RequestParam Long userId) {
        return userService.reactivate(userId);
    }

    @PostMapping("/assign-roles")
    public ResponseEntity<ApiResponse<Object>> assignRolesToUser(@RequestBody RoleGrantRequest data) {
        return userService.assignRolesToUser(data);
    }

}
