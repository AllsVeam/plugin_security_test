package demo.app.user.controller;
import demo.app.apiResponse.ApiResponse;
import demo.app.user.dto.UpdateUserRequest;
import demo.app.user.dto.UserDTO;
import demo.app.user.service.ZitadelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> crearUsuario(@RequestBody UserDTO dto) {
        try {
            zitadelService.createUser(dto);
            return ResponseEntity.ok("Usuario creado correctamente en Zitadel");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear usuario: " + e.getMessage());
        }    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Object>> getUser() {
        return zitadelService.getUser();
    }

    @PutMapping("/update-user")
    public String updateUser(@RequestBody UpdateUserRequest request) {
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


}
