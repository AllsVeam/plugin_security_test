package demo.app.user.service;


import demo.app.apiResponse.ApiResponse;
import demo.app.user.dto.UpdateUserRequest;
import demo.app.user.dto.UserDTO;
import org.springframework.http.ResponseEntity;

public interface ZitadelService{
    public void createUser(UserDTO userDTO);
    public ResponseEntity<ApiResponse<Object>> getUser();
    public String obtenerToken();
    public String updateUser(UpdateUserRequest request);
    public ResponseEntity<ApiResponse<Object>> deleteUser(Long userId);
    ResponseEntity<ApiResponse<Object>> desactivate(Long userId);
    ResponseEntity<ApiResponse<Object>> reactivate(Long userId);
}
