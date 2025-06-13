package demo.app.user.controller;
import demo.app.apiResponse.ApiResponse;
import demo.app.user.dto.UpdateUserRequest;
import demo.app.user.dto.UserDTO;
import demo.app.user.service.ZitadelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    private ZitadelService zitadelService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearUsuario(@RequestBody UserDTO dto) {
        try {
            zitadelService.createUser(dto);
            return ResponseEntity.ok("Usuario creado correctamente en Zitadel");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear usuario: " + e.getMessage());
        }
    }


    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello World get";
    }


    @GetMapping("/getuser")
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


}
