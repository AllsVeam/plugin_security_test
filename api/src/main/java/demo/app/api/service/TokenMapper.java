package demo.app.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.api.NoRolesAssignedException;
import demo.app.api.dto.RoleDTO;
import demo.app.api.dto.UserDetailsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TokenMapper {


    @Autowired
    private PermissionService permissionService;

    public UserDetailsDTO mapTokenToUserDetails(Map<String, Object> tokenMap) {
        UserDetailsDTO userDetails = new UserDetailsDTO();

        String accessToken = (String) tokenMap.getOrDefault("access_token", null);
        userDetails.setAccessToken(accessToken);
        userDetails.setAuthenticated(true);
        userDetails.setOfficeId(1);
        userDetails.setOfficeName("Head Office");

        if (accessToken != null) {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest userInfoRequest = HttpRequest.newBuilder()
                        .uri(URI.create("https://plugin-auth-ofrdfj.us1.zitadel.cloud/oidc/v1/userinfo"))
                        .header("Authorization", "Bearer " + accessToken)
                        .GET()
                        .build();

                HttpResponse<String> userInfoResponse = client.send(userInfoRequest, HttpResponse.BodyHandlers.ofString());

                if (userInfoResponse.statusCode() == 200) {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> userInfo = mapper.readValue(userInfoResponse.body(), Map.class);

                    userDetails.setUsername((String) userInfo.getOrDefault("preferred_username", null));
                    String userId = (String) userInfo.getOrDefault("sub", null);
                    userDetails.setUserId(Long.parseLong(userId));

                    List<RoleDTO> rolesList = new ArrayList<>();
                    Object value = userInfo.get("urn:zitadel:iam:org:project:roles");

                    if (value instanceof Map<?, ?> rolesMap) {
                        for (Map.Entry<?, ?> roleEntry : rolesMap.entrySet()) {
                            String roleName = roleEntry.getKey().toString();
                            Object roleDetailsObj = roleEntry.getValue();

                            if (roleDetailsObj instanceof Map<?, ?> roleDetailsMap) {
                                for (Map.Entry<?, ?> detailEntry : roleDetailsMap.entrySet()) {
                                    try {
                                        Long roleId = Long.parseLong(detailEntry.getKey().toString());
                                        String source = detailEntry.getValue().toString();

                                        RoleDTO role = new RoleDTO();
                                        role.setId(roleId);
                                        role.setName(roleName);
                                        role.setDescription(roleName + " " + source);
                                        role.setDisabled(false);

                                        rolesList.add(role);
                                    } catch (NumberFormatException e) {
                                        System.out.println("ID inválido para rol: " + detailEntry.getKey());
                                    }
                                }
                            }
                        }
                    }

                    if (rolesList.isEmpty()) {
                        throw new NoRolesAssignedException("Sin rol asignado");
                    }

                    userDetails.setRoles(rolesList);
                    String scope = (String) tokenMap.get("scope");
                    List<String> permisos = new ArrayList<>();

//                     if (scope != null && !scope.isBlank()) {
//                      permisos.addAll(
//                      Arrays.stream(scope.split(" "))
//                      .filter(s -> !s.equalsIgnoreCase("ALL_FUNCTIONS")) // 👈 evita agregarlo
//                      .collect(Collectors.toList())
//                  );
//                  }


                    List<String> roleNames = userDetails.getRoles().stream()
                            .map(RoleDTO::getName)
                            .collect(Collectors.toList());

                    List<String> permisosDesdeBD = permissionService.obtenerPermisosDesdeRoles(roleNames);
                    permisos.addAll(permisosDesdeBD);

                    //permisos.add("TWOFACTOR_AUTHENTICATED");

                    Set<String> permisosUnicos = new HashSet<>(permisos);
                    userDetails.setPermissions(new ArrayList<>(permisosUnicos));



                    userDetails.setShouldRenewPassword(false);
                    userDetails.setTwoFactorAuthenticationRequired(false);
                    return userDetails;
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error al procesar el token: " + e.getMessage());
            }
        }

        throw new RuntimeException("Token nulo o inválido");
    }



}
