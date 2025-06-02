package demo.app.api;
import java.util.HashMap;
import java.util.*;

import ch.qos.logback.classic.encoder.JsonEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.Arrays;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
class ExampleController {

    @Value("${zitadel.client-id}")
    private String clientId;

    @Value("${zitadel.client-secret}")
    private String clientSecret;

    @Value("${zitadel.redirect-uri}")
    private String redirectUri;

    private static final Logger logger = LoggerFactory.getLogger(ExampleController.class);

    private ArrayList<String> tasks = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(ExampleController.class);

    @GetMapping("/api/healthz")
    Object healthz() {
        return "OK";
    }

    @GetMapping(value = "/api/tasks", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    Object tasks(SecurityContextHolderAwareRequestWrapper requestWrapper) {
        if (this.tasks.size() > 0 || !requestWrapper.isUserInRole("ROLE_admin")) {
            return this.tasks;
        }
        log.debug("Entrando a la llamada");
        return Arrays.asList("add the first task");
    }

    @GetMapping(value = "/api/consulta", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    Object tasks2() {
        return "Estamos autorizados";
    }

    @PostMapping(value = "/api/tasks", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('admin')")
    Object addTask(@RequestBody String task, HttpServletResponse response) {
        if (task.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "task must not be empty");
        }
        this.tasks.add(task);
        return "task added";
    }

    @PostMapping("/callback")
    public ResponseEntity<Map<String, String>> callback(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        logger.info("Received code: {}", code);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", redirectUri);
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
                "https://prueba-fnkj2p.us1.zitadel.cloud/oauth/v2/token",
                request,
                Map.class
        );

        String accessToken = (String) tokenResponse.getBody().get("access_token");
        logger.info("Access token obtained: {}", accessToken);

        Map<String, String> response = new HashMap<>();
        response.put("access_token", accessToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/callback")
    public ResponseEntity<Map<String, String>> callback(@RequestParam String code) {
        logger.info("Received code: {}", code);
        System.out.println(code);
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", redirectUri);
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
                "https://prueba-fnkj2p.us1.zitadel.cloud/oauth/v2/token",
                request,
                Map.class
        );

        if (tokenResponse.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> body = tokenResponse.getBody();

            String accessToken = (String) body.get("access_token");
            String idToken = (String) body.get("id_token");
            String refreshToken = (String) body.get("refresh_token");

            logger.info("Access token obtained: {}", accessToken);
            logger.info("ID token (JWT) obtained: {}", idToken);

            Map<String, String> response = new HashMap<>();
            response.put("access_token", accessToken);
            response.put("id_token", idToken);
            if (refreshToken != null) {
                response.put("refresh_token", refreshToken);
            }
            System.out.println("Token: "+response);
            return ResponseEntity.ok(response);
        } else {
            logger.error("Failed to get token: {}", tokenResponse);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Failed to get token"));
        }
    }

    @PostMapping("/token")
    public ResponseEntity<?> getToken(@RequestBody Map<String, String> payload) {
        try {
            String code = payload.get("code");
            String codeVerifier = payload.get("code_verifier");

            HttpClient client = HttpClient.newHttpClient();
            String requestBody = "grant_type=authorization_code"
                    + "&code=" + code
                    + "&redirect_uri=http://localhost:4200/callback"
                    + "&client_id=321191693166683125"
                    + "&code_verifier=" + codeVerifier;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://plugin-auth-ofrdfj.us1.zitadel.cloud/oauth/v2/token"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> tokenData = mapper.readValue(response.body(), new TypeReference<>() {});

            return ResponseEntity.ok(tokenData); // Retorna el JSON con access_token, id_token, refresh_token
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el token");
        }
    }

    @PostMapping("/userdetails")
    public ResponseEntity<?> userDetails(@RequestBody Map<String, String> tokenMap) {
        String token = tokenMap.get("token");
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body("No token provided");
        }

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://plugin-auth-ofrdfj.us1.zitadel.cloud/oidc/v1/userinfo"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return ResponseEntity.status(response.statusCode()).body("Error al obtener datos del usuario");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> userInfo = objectMapper.readValue(response.body(), new TypeReference<>() {});

            return ResponseEntity.ok(userInfo);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
