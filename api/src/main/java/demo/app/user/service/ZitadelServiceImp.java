package demo.app.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.apiResponse.ApiResponse;
import demo.app.user.dto.ResponseZitadelDTO;
import demo.app.user.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ZitadelServiceImp implements ZitadelService {

    // https://plugin-auth-ofrdfj.us1.zitadel.cloud/v2/
    private static final String API_URL = "https://plugin-auth-ofrdfj.us1.zitadel.cloud/management/v1/users/_search";
    private static final String ZITADEL_TOKEN = "bGH1RVY7gwgFydzrRTgyWfDhcoxYs8oiG-aEWapojTUa83Qw_6TEoux346VcdoVzO3VprpA";
    private static final String ZITADEL_USER = "https://plugin-auth-ofrdfj.us1.zitadel.cloud/v2/users/";

    @Override
    public ResponseEntity<ApiResponse<UserDTO>> createUser(UserDTO userDTO) {
        try {
            // Preparar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth("WqBTjb1zFWa_KDptOVoHneXpbnEe6VO8vsKashypwtjiNiS-wV2HzDe6pehXd4d5T7YmuD0");

            // Construir body para ZITADEL
            Map<String, Object> payload = new HashMap<>();
            Map<String, Object> user = new HashMap<>();
            Map<String, Object> human = new HashMap<>();

            Map<String, Object> email = Map.of(
                    "email", userDTO.getEmail(),
                    "is_email_verified", false
            );

            Map<String, Object> name = Map.of(
                    "given_name", userDTO.getGivenName(),
                    "family_name", userDTO.getFamilyName()
            );

            Map<String, Object> password = Map.of(
                    "password", userDTO.getPassword()
            );

            human.put("email", email);
            human.put("name", name);
            human.put("password", password);

            user.put("username", userDTO.getUsername());
            user.put("human", human);

            payload.put("user", user);

            // Crear request
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            // Enviar POST a ZITADEL
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://plugin-auth-ofrdfj.us1.zitadel.cloud/management/v1/users",
                    request,
                    String.class
            );

            System.out.println(restTemplate);

            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(new ApiResponse<>(200, "Usuario creado correctamente en ZITADEL", userDTO));
            } else {
                throw new RuntimeException("Respuesta de ZITADEL no exitosa: " + response.getStatusCode());
            }

        } catch (Exception e) {
            return ResponseEntity.status(400).body(new ApiResponse<>(400, "Error al crear el usuario", null));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getUser() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(ZITADEL_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            /*
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            return ResponseEntity.ok(new ApiResponse<>(200, "Usuarios obtenidos", response.getBody()));
            */

            ResponseEntity<ResponseZitadelDTO> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    entity,
                    ResponseZitadelDTO.class
            );

            System.out.println(response.getBody());

            ResponseZitadelDTO responseBody = response.getBody();
            return ResponseEntity.ok(new ApiResponse<>(200, "Usuarios obtenidos", responseBody));

        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(400, "Error al obtener el usuario", null));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> deleteUser(Long userId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(ZITADEL_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResponseZitadelDTO> response = restTemplate.exchange(
                    ZITADEL_USER + userId,
                    HttpMethod.DELETE,
                    entity,
                    ResponseZitadelDTO.class
            );

            return ResponseEntity.ok(new ApiResponse<>(200, "Usuario eliminado", response));
        } catch (HttpClientErrorException e){
            return handleZitadelError(e);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Error inesperado: " + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> desactivate(Long userId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(ZITADEL_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResponseZitadelDTO> response = restTemplate.exchange(
                    ZITADEL_USER + userId + "/deactivate",
                    HttpMethod.POST,
                    entity,
                    ResponseZitadelDTO.class
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(200, "Usuario desactivado correctamente", response.getBody())
            );

        } catch (HttpClientErrorException e) {
            return handleZitadelError(e);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Error inesperado: " + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> reactivate(Long userId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(ZITADEL_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResponseZitadelDTO> response = restTemplate.exchange(
                    ZITADEL_USER + userId + "/reactivate",
                    HttpMethod.POST,
                    entity,
                    ResponseZitadelDTO.class
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(200, "Usuario activado correctamente", response.getBody())
            );

        } catch (HttpClientErrorException e) {
            return handleZitadelError(e);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Error inesperado: " + e.getMessage(), null));
        }
    }

    // TODO: Bad_Request, maneja el error del consumo y se queda interno dependiendo del codigo recivido ya sea 5 o 9, si fuera diferente es directo un BAD_REQUEST
    private ResponseEntity<ApiResponse<Object>> handleZitadelError(HttpClientErrorException e) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> errorJson = mapper.readValue(e.getResponseBodyAsString(), Map.class);

            int code = (int) errorJson.getOrDefault("code", 400);
            String message = (String) errorJson.getOrDefault("message", "Error desconocido");
            Object details = errorJson.get("details");

            HttpStatus status = (code == 5) ? HttpStatus.NOT_FOUND : (code == 9) ? HttpStatus.ACCEPTED : HttpStatus.BAD_REQUEST;

            return ResponseEntity.status(status)
                    .body(new ApiResponse<>(code, message, details));

        } catch (Exception parseException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(400, "Error al parsear el mensaje de error", null));
        }
    }

}
