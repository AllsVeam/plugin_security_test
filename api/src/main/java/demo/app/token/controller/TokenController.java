package demo.app.token.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.token.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Map;

@RestController
public class TokenController {

    @Autowired
    TokenService tokenService;

    @PostMapping("/token")
    public ResponseEntity<?> getToken(@RequestBody Map<String, String> payload) {
        return tokenService.getToken(payload);
    }

    @PostMapping("/notifications")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getNotifications() {
        return ResponseEntity.ok("Token válido, notificación enviada");
    }


}
