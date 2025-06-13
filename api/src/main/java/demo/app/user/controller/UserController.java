package demo.app.user.controller;
import demo.app.apiResponse.ApiResponse;
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

    @Autowired
    public ZitadelService zitadelService;


    @GetMapping("/hello")
    @PreAuthorize("isAuthenticated()")
    public String helloWorld() {
        return "Hello World get";
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserDTO userDTO) {
        return zitadelService.createUser(userDTO);
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<Object>> getUser() {
        return zitadelService.getUser();
    }

    @DeleteMapping("/")
    public ResponseEntity<ApiResponse<Object>> deleteUser(@RequestParam Long userId) {
        return zitadelService.deleteUser(userId);
    }

    @PostMapping("/desactivate")
    public ResponseEntity<ApiResponse<Object>> desactivateUser(@RequestParam Long userId) {
        return zitadelService.desactivate(userId);
    }

    @PostMapping("/reactivate")
    public ResponseEntity<ApiResponse<Object>> reactivateUser(@RequestParam Long userId) {
        return zitadelService.reactivate(userId);
    }


}
