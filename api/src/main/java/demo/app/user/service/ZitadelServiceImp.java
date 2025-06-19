package demo.app.user.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.apiResponse.ApiResponse;
import demo.app.user.roles.RoleGrantRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import demo.app.user.roles.RoleRequest;
import demo.app.user.dto.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ZitadelServiceImp implements ZitadelService {
    // https://plugin-auth-ofrdfj.us1.zitadel.cloud/v2/
    private static final String API_URL = "https://plugin-auth-ofrdfj.us1.zitadel.cloud/management/v1/users/_search";
    private static final String ZITADEL_TOKEN = "bGH1RVY7gwgFydzrRTgyWfDhcoxYs8oiG-aEWapojTUa83Qw_6TEoux346VcdoVzO3VprpA";
    private static final String ZITADEL_USER = "https://plugin-auth-ofrdfj.us1.zitadel.cloud/v2/users/";

    @Value("${zitadel.proyect_id}")
    private String proyectId;

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

    @Value("${zitadel.proyect_grand_id}")
    private String projectGrantId;

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

    public UserIdTokenResponse buscarUserIdYToken(String criterio, String valor) {
        String token = obtenerToken();
        String url = "https://plugin-auth-ofrdfj.us1.zitadel.cloud/management/v1/users/_search";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        HttpEntity<String> request = new HttpEntity<>("{}", headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    Object.class
            );

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> root = mapper.convertValue(response.getBody(), new TypeReference<>() {});
            List<Map<String, Object>> usuarios = (List<Map<String, Object>>) root.get("result");

            for (Map<String, Object> usuario : usuarios) {
                String userName = (String) usuario.get("userName");
                Map<String, Object> human = (Map<String, Object>) usuario.get("human");

                if (criterio.equalsIgnoreCase("userName") && userName != null && userName.equalsIgnoreCase(valor)) {
                    return new UserIdTokenResponse((String) usuario.get("id"), token);
                }

                if (criterio.equalsIgnoreCase("email") && human != null) {
                    Map<String, Object> email = (Map<String, Object>) human.get("email");
                    if (email != null && valor.equalsIgnoreCase((String) email.get("email"))) {
                        return new UserIdTokenResponse((String) usuario.get("id"), token);
                    }
                }
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("Error HTTP: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.out.println("Error al procesar la respuesta: " + e.getMessage());
        }

        return null;
    }


    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "https://plugin-auth-ofrdfj.us1.zitadel.cloud/v2/users/";


    @Override
    public String updateUser(UpdateUserRequest req) {
        StringBuilder result = new StringBuilder();

        if (req.email != null) {
            result.append(postRequest(baseUrl + req.userId + "/email", req.token, req.email));
        }
        if (req.phone != null) {
            result.append(postRequest(baseUrl + req.userId + "/phone", req.token, req.phone));
        }
        if (req.profile != null) {
            // Construir cuerpo de actualización de perfil
            String url = baseUrl + "human/" + req.userId;
            String body = """
                {
                  "username": "%s",
                  "profile": {
                    "givenName": "%s",
                    "familyName": "%s",
                    "displayName": "%s",
                    "nickName": "%s",
                    "preferredLanguage": "%s",
                    "gender": "%s"
                  }
                }
                """.formatted(
                    req.profile.username,
                    req.profile.givenName,
                    req.profile.familyName,
                    req.profile.displayName,
                    req.profile.nickName,
                    req.profile.preferredLanguage,
                    req.profile.gender
            );
            result.append(putRequest(url, req.token, body));
        }
        if (req.password != null) {
            result.append(postRequest(baseUrl + req.userId + "/password", req.token, req.password));
        }

        return result.toString();
    }
    private String postRequest(String url, String token, Object body) {
        return sendRequest(url, token, body, HttpMethod.POST);
    }

    private String putRequest(String url, String token, Object body) {
        return sendRequest(url, token, body, HttpMethod.PUT);
    }

    private String sendRequest(String url, String token, Object body, HttpMethod method) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<Object> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class);

            return "\n[" + method + "] " + url + ": " + response.getStatusCode();
        } catch (Exception e) {
            return "\n[" + method + "] " + url + ": ERROR - " + e.getMessage();
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

    @Override
    public ResponseEntity<ApiResponse<Object>> getUserById(Long userId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(ZITADEL_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    ZITADEL_USER + userId, HttpMethod.GET, entity, Object.class
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(200, "Usuario obtenido correctamente", response.getBody())
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

    @Override
    public ResponseEntity<ApiResponse<Object>> createRol(RoleRequest data) {
        try {
            String roleKey = data.getRoleKey();

            String url = "https://plugin-auth-ofrdfj.us1.zitadel.cloud/management/v1/projects/" + proyectId + "/roles";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(ZITADEL_TOKEN);
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject json = new JSONObject();
            json.put("roleKey", data.getRoleKey());
            json.put("displayName", data.getDisplayName());
            json.put("group", data.getGroup() != null ? data.getGroup() : "");

            HttpEntity<String> entity = new HttpEntity<>(json.toString(), headers);

            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);

            return ResponseEntity.ok(new ApiResponse<>(200, "Rol creado correctamente", response.getBody()));

        } catch (HttpClientErrorException e) {
            return handleZitadelError(e);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Error inesperado: " + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> deleteRol(String roleKey) {
        try {
            String url = "https://plugin-auth-ofrdfj.us1.zitadel.cloud/management/v1/projects/" + proyectId + "/roles/" + roleKey;

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(ZITADEL_TOKEN);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    entity,
                    Object.class
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(200, "Rol eliminado correctamente", response.getBody())
            );

        } catch (HttpClientErrorException e) {
            return handleZitadelError(e);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Error inesperado: " + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> updateRol(String roleKey, RoleRequest data) {
        try {
            String url = "https://plugin-auth-ofrdfj.us1.zitadel.cloud/management/v1/projects/" + proyectId + "/roles/" + roleKey;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(ZITADEL_TOKEN);
            headers.setContentType(MediaType.APPLICATION_JSON);

            JSONObject payload = new JSONObject();
            payload.put("displayName", data.getDisplayName());
            payload.put("group", data.getGroup() != null ? data.getGroup() : "");

            HttpEntity<String> entity = new HttpEntity<>(payload.toString(), headers);

            ResponseEntity<Object> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    entity,
                    Object.class
            );

            return ResponseEntity.ok(new ApiResponse<>(200, "Rol actualizado correctamente", response.getBody()));

        } catch (HttpClientErrorException e) {
            return handleZitadelError(e);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Error inesperado: " + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> assignRolesToUser(RoleGrantRequest data) {
        System.out.println("🚀 Iniciando asignación de roles al usuario...");
        System.out.println("📥 Datos recibidos:");
        System.out.println("  - userId: " + data.getUserId());
        System.out.println("  - roleKeys: " + data.getRoleKeys());

        try {
            String userId = data.getUserId();
            String urlGetGrants = "https://plugin-auth-ofrdfj.us1.zitadel.cloud/management/v1/users/" + userId + "/grants";

            // Configurar headers
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(ZITADEL_TOKEN);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            System.out.println("🔎 Consultando grants actuales del usuario...");
            ResponseEntity<String> response = restTemplate.exchange(urlGetGrants, HttpMethod.GET, entity, String.class);
            System.out.println("✅ Respuesta de grants: " + response.getBody());

            JSONObject body = new JSONObject(response.getBody());
            JSONArray grants = body.optJSONArray("result");
            String userGrantIdToDelete = null;

            // Verificar si ya existe un grant con los mismos roles
            if (grants != null) {
                for (int i = 0; i < grants.length(); i++) {
                    JSONObject grant = grants.getJSONObject(i);
                    String projectId = grant.optString("projectId");

                    if (projectId.equals(proyectId)) {
                        JSONArray existingRoles = grant.optJSONArray("roles");
                        List<String> incomingRoles = data.getRoleKeys();

                        if (existingRoles != null && existingRoles.toList().containsAll(incomingRoles)) {
                            System.out.println("⚠️ El usuario ya tiene estos roles asignados.");
                            return ResponseEntity.ok(new ApiResponse<>(200, "El usuario ya tiene estos roles asignados", null));
                        }

                        userGrantIdToDelete = grant.optString("userGrantId");
                        break;
                    }
                }
            } else {
                System.out.println("⚠️ No se encontraron grants actuales para el usuario.");
            }

            // Si hay que eliminar el grant anterior
            if (userGrantIdToDelete != null && !userGrantIdToDelete.isEmpty()) {
                String deleteUrl = "https://plugin-auth-ofrdfj.us1.zitadel.cloud/management/v1/users/" + userId + "/grants/" + userGrantIdToDelete;
                System.out.println("🗑️ Eliminando grant anterior con ID: " + userGrantIdToDelete);

                ResponseEntity<String> deleteResp = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, entity, String.class);
                System.out.println("✅ Grant eliminado. Código: " + deleteResp.getStatusCode());
            }

            // Crear nuevo grant
            JSONObject payload = new JSONObject();
            payload.put("projectId", proyectId);
            payload.put("roleKeys", new JSONArray(data.getRoleKeys()));

            String urlAssign = "https://plugin-auth-ofrdfj.us1.zitadel.cloud/management/v1/users/" + userId + "/grants";
            HttpEntity<String> assignEntity = new HttpEntity<>(payload.toString(), headers);

            System.out.println("📤 Enviando nuevo grant al usuario...");
            System.out.println("Payload: " + payload.toString());

            ResponseEntity<Object> assignResp = restTemplate.exchange(urlAssign, HttpMethod.POST, assignEntity, Object.class);
            System.out.println("✅ Roles asignados correctamente");

            return ResponseEntity.ok(new ApiResponse<>(200, "Rol(es) asignado(s) correctamente", assignResp.getBody()));

        } catch (HttpClientErrorException e) {
            System.out.println("❌ Error HTTP: " + e.getStatusCode());
            System.out.println("Mensaje: " + e.getResponseBodyAsString());
            return handleZitadelError(e);

        } catch (Exception e) {
            System.out.println("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Error inesperado: " + e.getMessage(), null));
        }
    }




}
