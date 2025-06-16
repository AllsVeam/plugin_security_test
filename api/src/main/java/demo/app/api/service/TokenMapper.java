package demo.app.api.service;
import java.util.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.app.api.dto.RoleDTO;
import demo.app.api.dto.UserDetailsDTO;


public class TokenMapper {


    public static UserDetailsDTO mapTokenToUserDetails(Map<String, Object> tokenMap) {
        UserDetailsDTO userDetails = new UserDetailsDTO();

        String accessToken = (String) tokenMap.getOrDefault("access_token", null);
        userDetails.setAccessToken(accessToken);
        userDetails.setAuthenticated(true);
        userDetails.setOfficeId(1);
        userDetails.setOfficeName("Head Office");

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        String userId = null;
        String apiToken = "YOUR_API_TOKEN_HERE";


        if (accessToken != null) {
            try {

                HttpRequest userInfoRequest = HttpRequest.newBuilder()
                        .uri(URI.create("https://plugin-auth-ofrdfj.us1.zitadel.cloud/oidc/v1/userinfo"))
                        .header("Authorization", "Bearer " + accessToken)
                        .GET()
                        .build();

                HttpResponse<String> userInfoResponse = client.send(userInfoRequest, HttpResponse.BodyHandlers.ofString());

                if (userInfoResponse.statusCode() == 200) {
                    Map<String, Object> userInfo = mapper.readValue(userInfoResponse.body(), Map.class);
                    userDetails.setUsername((String) userInfo.getOrDefault("preferred_username", null));
                    userId = (String) userInfo.getOrDefault("sub", null);
                    userDetails.setUserId(Long.parseLong(userId));

                } else {
                    return null;
                }


                    // === Leer roles desde scope ===
                    String scope = (String) tokenMap.get("scope");

                    if (scope != null && !scope.isBlank()) {
                        List<String> scopes = new ArrayList<>(Arrays.asList(scope.split(" ")));
                        List<RoleDTO> rolesList = new ArrayList<>();

                        for (String s : scopes) {
                            if (!s.equals("openid") && !s.equals("profile") && !s.equals("email")) {
                                RoleDTO role = new RoleDTO();
                                if(s.equals("ALL_FUNCTIONS")){
                                    role.setId(1L);
                                    role.setName("Super user");
                                    role.setDescription("This role provides all application permissions.");
                                    rolesList.add(role);
                                }else{
                                    role.setId(null);
                                    role.setName(null);
                                    role.setDescription(null);
                                    rolesList.add(role);
                                }

                            }
                        }

                        userDetails.setRoles(rolesList);
                } else {
                    userDetails.setRoles(Collections.emptyList());
                }

            } catch (Exception e) {
                e.printStackTrace();
                userDetails.setRoles(Collections.emptyList());
            }
        }


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



}
