package demo.app.user.service;

import demo.app.apiResponse.ApiResponse;
import demo.app.user.dto.ResponseZitadelDTO;
import demo.app.user.dto.UserDTO;
import demo.app.user.dto.UserZitadelDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ZitadelServiceImp implements ZitadelService {

    private static final String API_URL = "https://plugin-auth-ofrdfj.us1.zitadel.cloud/management/v1/users/_search";
    private static final String ZITADEL_TOKEN = "bGH1RVY7gwgFydzrRTgyWfDhcoxYs8oiG-aEWapojTUa83Qw_6TEoux346VcdoVzO3VprpA";

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
    public ResponseEntity<ApiResponse<ResponseZitadelDTO>> getUser(String id) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(ZITADEL_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResponseZitadelDTO> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    entity,
                    ResponseZitadelDTO.class
            );

            ResponseZitadelDTO responseBody = response.getBody();

            if (responseBody != null && responseBody.getResult() != null) {
                List<UserZitadelDto> allUsers = responseBody.getResult();

                // Si no se solicitó ID específico, devolver todos los usuarios
                if (id == null || id.isEmpty()) {
                    return ResponseEntity.ok(new ApiResponse<>(200, "Usuarios obtenidos", responseBody));
                }

                // Si hay un ID específico, filtrar
                List<UserZitadelDto> filteredUsers = allUsers.stream()
                        .filter(user -> id.equals(user.getId()))
                        .collect(Collectors.toList());

                ResponseZitadelDTO filteredResponse = new ResponseZitadelDTO();
                filteredResponse.setDetails(responseBody.getDetails());
                filteredResponse.setResult(filteredUsers);

                return ResponseEntity.ok(new ApiResponse<>(200, "Usuarios obtenidos", filteredResponse));
            }

            return ResponseEntity.ok(new ApiResponse<>(404, "Usuario no encontrado", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(400, "Error al obtener el usuario", null));
        }
    }

}
