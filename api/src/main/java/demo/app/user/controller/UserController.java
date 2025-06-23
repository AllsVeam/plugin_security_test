package demo.app.user.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.apiResponse.ApiResponse;
import demo.app.user.roles.RoleGrantRequest;
import demo.app.user.dto.ResponseZitadelDTO;
import demo.app.user.dto.UpdateUserRequest;
import demo.app.user.dto.UserDTO;
import demo.app.user.service.ZitadelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final ZitadelService userService;

    @Autowired
    public UserController(ZitadelService userService) {
        this.userService = userService;
    }

    @Autowired
    public ZitadelService zitadelService;

    @GetMapping("/hello")
    @PreAuthorize("isAuthenticated()")
    public String helloWorld() {
        return "Hello World get";
    }

    @PostMapping("/crear")
    public ResponseEntity<ApiResponse<Object>> crearUsuario(@RequestBody UserDTO dto) {
            return zitadelService.createUser(dto);
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<ResponseZitadelDTO>> getAllUsers() {
        return zitadelService.getUser(null);
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
        return zitadelService.getUser(id);
    }

    @PutMapping("/update-user")
    public String updateUser(@RequestBody UpdateUserRequest request) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
            System.out.println("JSON bonito:\n" + json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zitadelService.updateUser(request);
    }


    @PostMapping("/Obtenertoken")
    public String obtenerToken() {
        return zitadelService.obtenerToken();
    }

    @DeleteMapping("/")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@RequestParam Long userId) {
        return zitadelService.deleteUser(userId);
    }

    @PutMapping("/desactivate")
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> desactivateUser(@RequestParam Long userId) {
        return zitadelService.desactivate(userId);
    }

    @PutMapping("/reactivate")
    public ResponseEntity<ApiResponse<Object>> reactivateUser(@RequestParam Long userId) {
        return zitadelService.reactivate(userId);
    }

    @PostMapping("/assign-roles")
    public ResponseEntity<ApiResponse<Object>> assignRolesToUser(@RequestBody RoleGrantRequest data) {
        return zitadelService.assignRolesToUser(data);
    }

}
