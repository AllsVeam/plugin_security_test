package demo.app.user.service;

import demo.app.apiResponse.ApiResponse;
import demo.app.user.dto.ResponseZitadelDTO;
import demo.app.user.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ZitadelServiceImp implements ZitadelService {

    @Value("${zitadel.client-id}")
    private String clientId;

    @Value("${zitadel.client-secret}")
    private String clientSecret;

    @Value("${zitadel.scope}")
    private String scope;

    @Value("${zitadel.token-url}")
    private String tokenUrl;

    @Value("${zitadel.api-url}")
    private String apiUrl;

    private static final String API_URL = "https://plugin-auth-ofrdfj.us1.zitadel.cloud/management/v1/users/_search";
    private static final String ZITADEL_TOKEN = "bGH1RVY7gwgFydzrRTgyWfDhcoxYs8oiG-aEWapojTUa83Qw_6TEoux346VcdoVzO3VprpA";

    private String obtenerToken() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // —————————————————————————————
        // En lugar de enviar client_id/secret en el body:
        headers.setBasicAuth(clientId, clientSecret);
        // —————————————————————————————

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("scope", scope);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(tokenUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody()!=null) {
                return (String) response.getBody().get("access_token");
            } else {
                log.error("Token request fallo: HTTP {} – body={}",
                        response.getStatusCode(), response.getBody());
                throw new RuntimeException("No se pudo obtener token de Zitadel");
            }
        } catch (HttpClientErrorException e) {
            log.error("Error pidiendo token: HTTP {} – {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }
    @Override
    public ResponseEntity<ApiResponse<UserDTO>> createUser(UserDTO userDTO) {
        try {
            // Preparar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(obtenerToken());

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
            log.error("Error creando usuario en Zitadel", e);
            return ResponseEntity.status(400).body(new ApiResponse<>(400, "Error al crear el usuario", null));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getUser() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(obtenerToken());
        log.info("Usando client_id: {}", clientId);
        log.info("Scope: {}", scope);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {/*
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            return ResponseEntity.ok(new ApiResponse<>(200, "Usuarios obtenidos", response.getBody()));
            */

            ResponseEntity<ResponseZitadelDTO> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
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

}
