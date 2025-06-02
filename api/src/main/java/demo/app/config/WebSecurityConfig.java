package demo.app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;

import demo.app.support.zitadel.CustomAuthorityOpaqueTokenIntrospector;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
class WebSecurityConfig {

    @Autowired
    private OpaqueTokenIntrospector introspector;
    /*
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(c -> c.disable())
                .sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(arc -> arc
                        .requestMatchers("/api/tasks").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(rs -> rs.opaqueToken(a -> a.introspector(this.introspector())));

        return http.build();
    }*/
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/get-token-direct", "/callback").permitAll()  // ← abierto
                        .requestMatchers("/api/tasks", "/api/consulta").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.opaqueToken(a -> a.introspector(this.introspector())));

        return http.build();
    }


    private OpaqueTokenIntrospector introspector() {
        return new CustomAuthorityOpaqueTokenIntrospector(introspector);
    }
}
