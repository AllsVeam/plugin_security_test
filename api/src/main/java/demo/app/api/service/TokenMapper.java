package demo.app.api.service;
import java.util.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.api.NoRolesAssignedException;
import demo.app.api.dto.RoleDTO;
import demo.app.api.dto.UserDetailsDTO;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class TokenMapper {


    public static UserDetailsDTO mapTokenToUserDetails(Map<String, Object> tokenMap) {
        UserDetailsDTO userDetails = new UserDetailsDTO();

        String accessToken = (String) tokenMap.getOrDefault("access_token", null);
        userDetails.setAccessToken(accessToken);
        userDetails.setAuthenticated(true);
        userDetails.setOfficeId(1);
        userDetails.setOfficeName("Head Office");

        HttpClient client = HttpClient.newHttpClient();

        if (accessToken != null) {
            try {
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

                    if (value instanceof Map<?, ?>) {
                        Map<?, ?> rolesMap = (Map<?, ?>) value;

                        for (Map.Entry<?, ?> roleEntry : rolesMap.entrySet()) {
                            String roleName = roleEntry.getKey().toString();
                            Object roleDetailsObj = roleEntry.getValue();

                            if (roleDetailsObj instanceof Map<?, ?>) {
                                Map<?, ?> roleDetailsMap = (Map<?, ?>) roleDetailsObj;

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
                        throw new NoRolesAssignedException("sin rol asignado");
                    }

                    userDetails.setRoles(rolesList);

                    String scope = (String) tokenMap.get("scope");
                    if (scope != null && !scope.isBlank()) {
                        List<String> permissions = new ArrayList<>(Arrays.asList(scope.split(" ")));
                        permissions.add("TWOFACTOR_AUTHENTICATED");
                        userDetails.setPermissions(permissions);
                    } else {
                        userDetails.setPermissions(Collections.emptyList());
                    }

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
