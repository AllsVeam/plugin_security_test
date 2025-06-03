package demo.app.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

@RestController
class ExampleController {

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

    @GetMapping("/api/project-roles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> getProjectRoles() {
        String token = getAccessTokenFromSecurityContext();
        String roles = getRolesFromZitadel(token, "320912215601386953");
        return ResponseEntity.ok(roles);
    }

    public String getAccessTokenFromSecurityContext() {
        BearerTokenAuthentication auth = (BearerTokenAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return auth.getToken().getTokenValue(); // El token JWT original
    }

    public String getRolesFromZitadel(String accessToken, String projectId) {
        String url = "https://plugin-auth-ofrdfj.us1.zitadel.cloud/management/v1/projects/" + projectId + "/roles";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }

    @PostMapping("/api/DTO-token")
    public ResponseEntity<ResponseDTO<UserDetailsDTO>> mapToken(@RequestBody Map<String, Object> tokenPayload) {
        ResponseDTO<UserDetailsDTO> response = new ResponseDTO<>();

        try {
            if (!tokenPayload.containsKey("access_token")) {
                response.setStatus(400);
                response.setMsg("The 'access_token' field is missing from the payload.");
                response.setObject(null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            UserDetailsDTO userDetails = TokenMapper.mapTokenToUserDetails(tokenPayload);

            if (userDetails == null || userDetails.getUserId() == null) {
                response.setStatus(401);
                response.setMsg("Invalid or unrecognized token");
                response.setObject(null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            response.setStatus(200);
            response.setMsg("Full user");
            response.setObject(userDetails);
            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            response.setStatus(500);
            response.setMsg("Unexpected error: " + ex.getMessage());
            response.setObject(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
