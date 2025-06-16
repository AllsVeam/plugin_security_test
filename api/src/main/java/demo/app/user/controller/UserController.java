package demo.app.user.controller;
import demo.app.apiResponse.ApiResponse;
import demo.app.user.dto.ResponseZitadelDTO;
import demo.app.user.dto.UserDTO;
import demo.app.user.service.ZitadelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    public ZitadelService zitadelService;

    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello World get";
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserDTO userDTO) {
        return zitadelService.createUser(userDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResponseZitadelDTO>> getUser(@PathVariable String id) {
        return zitadelService.getUser(id);
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<ResponseZitadelDTO>> getAllUsers() {
        return zitadelService.getUser(null);
    }

}
