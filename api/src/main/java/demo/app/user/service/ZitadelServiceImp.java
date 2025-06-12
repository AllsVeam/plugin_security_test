package demo.app.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.apiResponse.ApiResponse;
import demo.app.user.dto.ResponseZitadelDTO;
import demo.app.user.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class ZitadelServiceImp implements ZitadelService {


    @Value("${zitadel.urltoken}")
    private String tokenUrl;


    @Value("${zitadel.urluser}")
    private String urlUser;


    @Value("${zitadel.scope}")
    private String scopetoken;


    @Value("${zitadel.client_id}")
    private String clientId;

    @Value("${zitadel.client_secret}")
    private String client_secret;






    @Override
    public String obtenerToken() {
        try {

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");
            body.add("client_id", clientId);
            body.add("client_secret", client_secret);
            body.add("scope", scopetoken);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                if (responseBody != null && responseBody.containsKey("access_token")) {
                    return responseBody.get("access_token").toString();
                } else {
                    throw new RuntimeException("Token no encontrado en la respuesta");
                }
            } else {
                throw new RuntimeException("Error al obtener el token: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            System.err.println("Respuesta de error de Zitadel: " + e.getResponseBodyAsString());
            throw new RuntimeException("Error al obtener el token de Zitadel", e);
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al obtener el token", e);
        }
    }


    @Override
    public void createUser(UserDTO dto) {
        try {
            String token = obtenerToken();

            OkHttpClient client = new OkHttpClient();

            Map<String, Object> profile = Map.of(
                    "firstName", dto.getGivenName(),
                    "lastName", dto.getFamilyName(),
                    "displayName", dto.getDisplayName(),
                    "nickName", dto.getNickName(),
                    "preferredLanguage", dto.getPreferredLanguage(),
                    "gender", dto.getGender()
            );

            Map<String, Object> email = Map.of(
                    "email", dto.getEmail(),
                    "isEmailVerified", false
            );

            Map<String, Object> phone = Map.of(
                    "phone", dto.getPhone(),
                    "isPhoneVerified", true
            );

            Map<String, Object> password = Map.of(
                    "password", dto.getPassword(),
                    "changeRequired", true
            );

            Map<String, Object> initialLogin = Map.of(
                    "returnToUrl", "https://example.com/email/verify"
            );

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("userName", dto.getUsername());
            payload.put("organizationId", dto.getOrganizationId());
            payload.put("profile", profile);
            payload.put("email", email);
            payload.put("phone", phone);
            payload.put("password", password);
            payload.put("initialLogin", initialLogin);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(payload);

            System.out.println("Payload generado:\n" + json);

            RequestBody body = RequestBody.create(
                    json,
                    okhttp3.MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(urlUser)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                String responseBody = response.body() != null ? response.body().string() : "Sin cuerpo";
                System.err.println("Error al crear usuario: " + responseBody);
                throw new RuntimeException("Error al crear usuario en Zitadel: " + responseBody);
            }

            System.out.println("Usuario creado correctamente.");

        } catch (Exception e) {
            throw new RuntimeException("Error al crear usuario en Zitadel", e);
        }
    }






    @Override
    public ResponseEntity<ApiResponse<Object>> getUser() {
        String url = "https://plugin-auth-ofrdfj.us1.zitadel.cloud/management/v1/users/_search";
        String token=obtenerToken();
        // Token obtenido previamente vía client_credentials
        String bearerToken = "Bearer "+token;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token); // sin "Bearer ", Spring lo agrega
        HttpEntity<String> request = new HttpEntity<>("{}", headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Object.class
            );

            ApiResponse<Object> apiResponse = new ApiResponse<>();
            apiResponse.setMessage("Usuarios obtenidos correctamente");
            apiResponse.setSuccess(true);
            apiResponse.setData(response.getBody());
            System.out.println(response);
            return ResponseEntity.ok(apiResponse);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            ApiResponse<Object> errorResponse = new ApiResponse<>();
            errorResponse.setMessage("Error al obtener usuarios: " + e.getMessage());
            errorResponse.setSuccess(false);
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);
        }
    }


}
