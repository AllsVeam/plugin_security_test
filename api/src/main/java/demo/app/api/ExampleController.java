package demo.app.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.Arrays;
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
}
