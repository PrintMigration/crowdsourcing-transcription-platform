package mainApplication.fusionAuth;

import com.inversoft.error.Errors;
import com.inversoft.rest.ClientResponse;
import io.fusionauth.client.FusionAuthClient;
import io.fusionauth.domain.ApplicationRole;
import io.fusionauth.domain.User;
import io.fusionauth.domain.UserRegistration;
import io.fusionauth.domain.api.*;
import io.fusionauth.domain.api.jwt.ValidateResponse;
import io.fusionauth.domain.api.user.RegistrationRequest;
import io.fusionauth.domain.api.user.RegistrationResponse;
import io.fusionauth.domain.api.user.SearchRequest;
import io.fusionauth.domain.api.user.SearchResponse;
import io.fusionauth.domain.search.UserSearchCriteria;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.util.*;

@RestController
@RequestMapping("/fusionauth")
public class FusionAuthController {

    @Value("${fusionAuth.applicationId}")
    private String applicationId;

    private final FusionAuthClient fusionAuthClient;

    public FusionAuthController(FusionAuthClient fusionAuthClient) {
        this.fusionAuthClient = fusionAuthClient;
    }

    @PostMapping("/application/role")
    public ResponseEntity addApplicationRole(@RequestBody String role) {
        ApplicationRequest applicationRequest = new ApplicationRequest(new ApplicationRole(role));
        fusionAuthClient.createApplicationRole(UUID.fromString(applicationId), null, applicationRequest);

        ClientResponse<ApplicationResponse, Void> clientResponse = fusionAuthClient.retrieveApplication(UUID.fromString(applicationId));
        return new ResponseEntity(clientResponse.successResponse.application, HttpStatus.OK);
    }

    @GetMapping("/application/role")
    public ResponseEntity getApplicationRoles() {
        ClientResponse<ApplicationResponse, Void> clientResponse = fusionAuthClient.retrieveApplication(UUID.fromString(applicationId));
        return new ResponseEntity(clientResponse.successResponse.application, HttpStatus.OK);
    }

    @GetMapping(path="/get-admins")
    public List<User> getAllAdmins() {
        SearchRequest searchRequest = new SearchRequest(new UserSearchCriteria());
        searchRequest.search.queryString = "registrations.applicationId:" + applicationId + " AND registrations.roles:Administrator";
        ClientResponse<SearchResponse, Errors> clientResponse = fusionAuthClient.searchUsersByQueryString(searchRequest);
        if (clientResponse.wasSuccessful()) {
            return clientResponse.successResponse.users;
        } else {
            return null;
        }
    }

    @PostMapping(path="/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        if (loginRequest == null || loginRequest.loginId == null || loginRequest.password == null) {
            return new ResponseEntity<>("You did not provide the correct information", HttpStatus.BAD_REQUEST);
        }
        loginRequest.applicationId = UUID.fromString(applicationId);
        ClientResponse<LoginResponse, Errors> clientResponse = fusionAuthClient.login(loginRequest);
        if (clientResponse.wasSuccessful()) {
            ResponseEntity<User> user = new ResponseEntity<>(clientResponse.successResponse.user, HttpStatus.OK);
            Cookie cookie = new Cookie("access_token", clientResponse.successResponse.token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
            return user;
        } else if (clientResponse.status == 404){
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("Request failed", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path="/logout")
    public ResponseEntity logout(HttpServletResponse response) {
            Cookie removeCookie = new Cookie("access_token", null);
            removeCookie.setMaxAge(0);
            removeCookie.setHttpOnly(true);
            removeCookie.setPath("/");
            response.addCookie(removeCookie);
            return new ResponseEntity<>("Logout success", HttpStatus.OK);
    }

    @PostMapping(path="/user/register")
    public ResponseEntity createUser(@RequestBody RegistrationRequest registrationRequest, HttpServletResponse response) {
        if (registrationRequest == null || registrationRequest.user == null) {
            return new ResponseEntity<>("You did not provide the correct information", HttpStatus.BAD_REQUEST);
        }
        registrationRequest.registration.applicationId = UUID.fromString(applicationId);
        ClientResponse<RegistrationResponse, Errors> clientResponse = fusionAuthClient.register(null, registrationRequest);
        if (clientResponse.wasSuccessful()) {
            return new ResponseEntity<>(clientResponse.successResponse, HttpStatus.OK);
        } else if (clientResponse.errorResponse != null) {
                if (clientResponse.errorResponse.fieldErrors != null)
                    return new ResponseEntity<>(clientResponse.errorResponse.fieldErrors, HttpStatus.BAD_REQUEST);
                else if (clientResponse.errorResponse.generalErrors != null)
                    return new ResponseEntity<>(clientResponse.errorResponse.generalErrors, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(path="/user")
    public ResponseEntity getUser(@CookieValue(value = "access_token") Cookie cookie) {
        if (cookie == null) {
            return new ResponseEntity<>("You are not logged in" , HttpStatus.UNAUTHORIZED);
        }
        ClientResponse<ValidateResponse, Void> clientResponse = fusionAuthClient.validateJWT(cookie.getValue());
        if (clientResponse.status == 401) {
            return new ResponseEntity<>("Your access token is invalid (try logging in again)" , HttpStatus.UNAUTHORIZED);
        }
        if (clientResponse.wasSuccessful()) {
            SearchRequest searchRequest = new SearchRequest(new UserSearchCriteria());
            searchRequest.search.queryString = "id:" + clientResponse.successResponse.jwt.subject;
            ClientResponse<SearchResponse, Errors> searchResponse = fusionAuthClient.searchUsersByQueryString(searchRequest);
            if (searchResponse.wasSuccessful()) {
                if (searchResponse.successResponse.users != null && searchResponse.successResponse.users.size() > 0) {
                    return new ResponseEntity<>(searchResponse.successResponse.users.get(0), HttpStatus.OK);
                }
            } else if (searchResponse.status == 404) {
                return new ResponseEntity<>("User does not exist", HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>("Request failed", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("Failed Request", HttpStatus.BAD_REQUEST);
    }

    public UUID getUserUUIDByEmail(String email) {
        SearchRequest searchRequest = new SearchRequest(new UserSearchCriteria());
        searchRequest.search.queryString = "email:" + email;
        ClientResponse<SearchResponse, Errors> searchResponse = fusionAuthClient.searchUsersByQueryString(searchRequest);
        if (searchResponse.wasSuccessful()) {
            if (searchResponse.successResponse.users != null && searchResponse.successResponse.users.size() > 0) {
                return searchResponse.successResponse.users.get(0).id;
            }
        }
        return null;
    }

    public ResponseEntity reactivateUser(String email) {
        UUID uuid = getUserUUIDByEmail(email);
        if (uuid == null) return new ResponseEntity<>("Request failed", HttpStatus.BAD_REQUEST);
        ClientResponse<UserResponse, Errors> clientResponse = fusionAuthClient.reactivateUser(uuid);
        if (clientResponse.wasSuccessful()) {
            return new ResponseEntity<>(clientResponse.successResponse.user, HttpStatus.OK);
        } else if (clientResponse.status == 404) {
            return new ResponseEntity<>("User with UUID " + uuid + " does not exist", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("Request failed", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity deactivateUser(String email) {
        UUID uuid = getUserUUIDByEmail(email);
        if (uuid == null) return new ResponseEntity<>("Request failed", HttpStatus.BAD_REQUEST);
        ClientResponse<Void, Errors> clientResponse = fusionAuthClient.deactivateUser(uuid);
        if (clientResponse.wasSuccessful()) {
            return new ResponseEntity<>("Successfully deactivated user with UUID: " + uuid , HttpStatus.OK);
        } else if (clientResponse.status == 404) {
            return new ResponseEntity<>("User with UUID: " + uuid + " does not exist", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("Request failed", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path="/user")
    public ResponseEntity updateUser(@RequestBody RegistrationRequest updateRequest, @CookieValue(value = "access_token") Cookie cookie) {
        if (updateRequest == null || updateRequest.user == null || updateRequest.registration == null) {
            return new ResponseEntity<>("You did not provide the correct information", HttpStatus.BAD_REQUEST);
        }
        if (cookie == null) {
            return new ResponseEntity<>("You are not logged in" , HttpStatus.UNAUTHORIZED);
        }
        ClientResponse<ValidateResponse, Void> clientResponse = fusionAuthClient.validateJWT(cookie.getValue());
        if (clientResponse.status == 401) {
            return new ResponseEntity<>("Your access token is invalid (try logging in again)" , HttpStatus.UNAUTHORIZED);
        }
        if (clientResponse.wasSuccessful()) {
            SearchRequest searchRequest = new SearchRequest(new UserSearchCriteria());
            searchRequest.search.queryString = "id:" + clientResponse.successResponse.jwt.subject;
            ClientResponse<SearchResponse, Errors> searchResponse = fusionAuthClient.searchUsersByQueryString(searchRequest);
            if (searchResponse.wasSuccessful()) {
                if (searchResponse.successResponse.users != null && searchResponse.successResponse.users.size() > 0) {
                    User user = searchResponse.successResponse.users.get(0);
                    UserRegistration userRegistration = user.getRegistrationForApplication(UUID.fromString(applicationId));
                    if (updateRequest.registration.data != null) {
                        SortedSet<String> temp = userRegistration.roles;
                        userRegistration = new UserRegistration(userRegistration.id, userRegistration.applicationId, userRegistration.userId, null, userRegistration.username, null, null, updateRequest.registration.data, new ArrayList<>(), "");
                        userRegistration.roles = temp;
                    }
                    if (updateRequest.registration.username != null) {
                        userRegistration.username = updateRequest.registration.username;
                    }
                    ClientResponse<RegistrationResponse, Errors> updateRegistrationResponse = fusionAuthClient.updateRegistration(user.id, new RegistrationRequest(user, userRegistration));
                    if (updateRegistrationResponse.wasSuccessful()) {
                        if (updateRequest.user.firstName != null) {
                            user.firstName = updateRequest.user.firstName;
                        }
                        if (updateRequest.user.lastName != null) {
                            user.lastName = updateRequest.user.lastName;
                        }
                        ClientResponse<UserResponse, Errors> updateUserResponse = fusionAuthClient.updateUser(user.id, new UserRequest(user));
                        if (updateUserResponse.wasSuccessful()) {
                            return new ResponseEntity<>(updateUserResponse.successResponse.user, HttpStatus.OK);
                        }
                    }
                }
            }
        }
        return new ResponseEntity<>("Request failed", HttpStatus.BAD_REQUEST);
    }

    @PostMapping(path="/user/{email}/add-roles")
    public ResponseEntity addUserRoles(@PathVariable String email, @RequestBody List<String> roles, @CookieValue(value = "access_token") Cookie cookie) {
        if (roles == null || roles.size() < 1) {
            return new ResponseEntity<>("You did not provide the correct information", HttpStatus.BAD_REQUEST);
        }
        if (cookie == null) {
            return new ResponseEntity<>("You are not an Administrator (you are not logged in)" , HttpStatus.UNAUTHORIZED);
        }
        ClientResponse<ValidateResponse, Void> clientResponse = fusionAuthClient.validateJWT(cookie.getValue());
        if (clientResponse.status == 401) {
            return new ResponseEntity<>("Your access token is invalid (try logging in again)" , HttpStatus.UNAUTHORIZED);
        }
        if (clientResponse.wasSuccessful()) {
            if (clientResponse.successResponse.jwt.otherClaims.get("roles").toString().contains("Administrator")) {
                SearchRequest searchRequest = new SearchRequest(new UserSearchCriteria());
                searchRequest.search.queryString = "email:" + email;
                ClientResponse<SearchResponse, Errors> searchResponse = fusionAuthClient.searchUsersByQueryString(searchRequest);
                if (searchResponse.wasSuccessful()) {
                    User user;
                    if (searchResponse.successResponse.users != null && searchResponse.successResponse.users.size() > 0) {
                        user = searchResponse.successResponse.users.get(0);
                        UserRegistration userRegistration = user.getRegistrationForApplication(UUID.fromString(applicationId));
                        userRegistration.roles.addAll(roles);
                        ClientResponse<RegistrationResponse, Errors> updateResponse = fusionAuthClient.updateRegistration(user.id, new RegistrationRequest(user, userRegistration));
                        if (updateResponse.wasSuccessful()) {
                            return new ResponseEntity<>(updateResponse.successResponse, HttpStatus.OK);
                        } else {
                            return new ResponseEntity<>("Request failed", HttpStatus.BAD_REQUEST);
                        }
                    }

                } else if (searchResponse.status == 404) {
                    return new ResponseEntity<>("User with email " + email + " does not exist", HttpStatus.NOT_FOUND);
                } else {
                    return new ResponseEntity<>("Request failed", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("You are not an Administrator" , HttpStatus.UNAUTHORIZED);
            }
        }
        return new ResponseEntity<>("Failed Request", HttpStatus.BAD_REQUEST);
    }


    public ResponseEntity addUserRoles(String email, List<String> roles) {
        if (roles == null || roles.size() < 1) {
            return new ResponseEntity<>("You did not provide the correct information", HttpStatus.BAD_REQUEST);
        }
        SearchRequest searchRequest = new SearchRequest(new UserSearchCriteria());
        searchRequest.search.queryString = "email:" + email;
        ClientResponse<SearchResponse, Errors> searchResponse = fusionAuthClient.searchUsersByQueryString(searchRequest);
        if (searchResponse.wasSuccessful()) {
            User user;
            if (searchResponse.successResponse.users != null && searchResponse.successResponse.users.size() > 0) {
                user = searchResponse.successResponse.users.get(0);
                UserRegistration userRegistration = user.getRegistrationForApplication(UUID.fromString(applicationId));
                userRegistration.roles.addAll(roles);
                ClientResponse<RegistrationResponse, Errors> updateResponse = fusionAuthClient.updateRegistration(user.id, new RegistrationRequest(user, userRegistration));
                if (updateResponse.wasSuccessful()) {
                    return new ResponseEntity<>(updateResponse.successResponse, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Request failed", HttpStatus.BAD_REQUEST);
                }
            }
        } else if (searchResponse.status == 404) {
            return new ResponseEntity<>("User with email " + email + " does not exist", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("Request failed", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Failed Request", HttpStatus.BAD_REQUEST);
    }

    @PostMapping(path="/user/{email}/delete-roles")
    public ResponseEntity deleteUserRoles(@PathVariable String email, @RequestBody List<String> roles, @CookieValue(value = "access_token") Cookie cookie) {
        if (roles == null || roles.size() < 1) {
            return new ResponseEntity<>("You did not provide the correct information", HttpStatus.BAD_REQUEST);
        }
        ResponseEntity responseEntity = isAdministrator(cookie);
        if (responseEntity != null) return  responseEntity;

        SearchRequest searchRequest = new SearchRequest(new UserSearchCriteria());
        searchRequest.search.queryString = "email:" + email;
        ClientResponse<SearchResponse, Errors> searchResponse = fusionAuthClient.searchUsersByQueryString(searchRequest);
        if (searchResponse.wasSuccessful()) {
            User user;
            if (searchResponse.successResponse.users != null && searchResponse.successResponse.users.size() > 0) {
                user = searchResponse.successResponse.users.get(0);
                UserRegistration userRegistration = user.getRegistrationForApplication(UUID.fromString(applicationId));
                for (String role : roles) {
                    userRegistration.roles.remove(role);
                }
                ClientResponse<RegistrationResponse, Errors> updateResponse = fusionAuthClient.updateRegistration(user.id, new RegistrationRequest(user, userRegistration));
                if (updateResponse.wasSuccessful()) {
                    return new ResponseEntity<>(updateResponse.successResponse, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Request failed", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("User with email " + email + " does not exist", HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>("Request failed", HttpStatus.BAD_REQUEST);

    }

    public ResponseEntity deleteUserRoles(String email, List<String> roles) {
        if (roles == null || roles.size() < 1) {
            return new ResponseEntity<>("You did not provide the correct information", HttpStatus.BAD_REQUEST);
        }
        SearchRequest searchRequest = new SearchRequest(new UserSearchCriteria());
        searchRequest.search.queryString = "email:" + email;
        ClientResponse<SearchResponse, Errors> searchResponse = fusionAuthClient.searchUsersByQueryString(searchRequest);
        if (searchResponse.wasSuccessful()) {
            User user;
            if (searchResponse.successResponse.users != null && searchResponse.successResponse.users.size() > 0) {
                user = searchResponse.successResponse.users.get(0);
                UserRegistration userRegistration = user.getRegistrationForApplication(UUID.fromString(applicationId));
                for (String role : roles) {
                    userRegistration.roles.remove(role);
                }
                ClientResponse<RegistrationResponse, Errors> updateResponse = fusionAuthClient.updateRegistration(user.id, new RegistrationRequest(user, userRegistration));
                if (updateResponse.wasSuccessful()) {
                    return new ResponseEntity<>(updateResponse.successResponse, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Request failed", HttpStatus.BAD_REQUEST);
                }
            }
        } else if (searchResponse.status == 404) {
            return new ResponseEntity<>("User with email " + email + " does not exist", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>("Request failed", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(path="/users")
    public ResponseEntity getUsersByRoleAndEmailSubstring(@RequestParam(required = false) String email, @RequestParam(required = false) String role, @RequestParam int page , @CookieValue(value = "access_token") Cookie cookie) {
        ResponseEntity responseEntity = isAdministrator(cookie);
        if (responseEntity != null) return  responseEntity;

        SearchRequest searchRequest = new SearchRequest(new UserSearchCriteria());
        // Prevent int overflow
        if (page > 1 && page < 85000000) searchRequest.search.startRow = (page-1) * 25;
        searchRequest.search.queryString = "registrations.applicationId:" + applicationId;
        if (email != null) {
            searchRequest.search.queryString += " AND " + "email:*" + email + "*";
        }
        if (role != null) {
            searchRequest.search.queryString +=  " AND " + "registrations.roles:" + role;
        }
        ClientResponse<SearchResponse, Errors> searchResponse = fusionAuthClient.searchUsersByQueryString(searchRequest);
        if (searchResponse.wasSuccessful()) {
            if (searchResponse.successResponse.users != null && searchResponse.successResponse.users.size() > 0)
                return new ResponseEntity<>(searchResponse.successResponse.users, HttpStatus.OK);
            else
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Request failed", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity isTranscriber(Cookie cookie) {
        if (cookie == null) {
            return new ResponseEntity("You are not a transcriber or higher (you are not logged in)" , HttpStatus.UNAUTHORIZED);
        }
        ClientResponse<ValidateResponse, Void> clientResponse = fusionAuthClient.validateJWT(cookie.getValue());
        if (clientResponse.wasSuccessful()) {
            if (!clientResponse.successResponse.jwt.otherClaims.get("roles").toString().contains("Transcriber")) {
                return new ResponseEntity<>("You are not a transcriber or higher", HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>("Your login session has ended (or you sent a garbage cookie)", HttpStatus.UNAUTHORIZED);
        }
        return null;
    }

    public ResponseEntity isEditor(Cookie cookie) {
        if (cookie == null) {
            return new ResponseEntity("You are not an editor or higher (you are not logged in)" , HttpStatus.UNAUTHORIZED);
        }
        ClientResponse<ValidateResponse, Void> clientResponse = fusionAuthClient.validateJWT(cookie.getValue());
        if (clientResponse.wasSuccessful()) {
            if (!clientResponse.successResponse.jwt.otherClaims.get("roles").toString().contains("Editor")) {
                return new ResponseEntity<>("You are not an editor or higher", HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>("Your login session has ended (or you sent a garbage cookie)", HttpStatus.UNAUTHORIZED);
        }
        return null;
    }

    public ResponseEntity isEncoder(Cookie cookie) {
        if (cookie == null) {
            return new ResponseEntity("You are not an encoder or higher (you are not logged in)" , HttpStatus.UNAUTHORIZED);
        }
        ClientResponse<ValidateResponse, Void> clientResponse = fusionAuthClient.validateJWT(cookie.getValue());
        if (clientResponse.wasSuccessful()) {
            if (!clientResponse.successResponse.jwt.otherClaims.get("roles").toString().contains("Encoder")) {
                return new ResponseEntity<>("You are not an encoder or higher", HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>("Your login session has ended (or you sent a garbage cookie)", HttpStatus.UNAUTHORIZED);
        }
        return null;
    }

    public ResponseEntity isMetadataExpert(Cookie cookie) {
        if (cookie == null) {
            return new ResponseEntity("You are not a metadata expert or higher (you are not logged in)" , HttpStatus.UNAUTHORIZED);
        }
        ClientResponse<ValidateResponse, Void> clientResponse = fusionAuthClient.validateJWT(cookie.getValue());
        if (clientResponse.wasSuccessful()) {
            if (!clientResponse.successResponse.jwt.otherClaims.get("roles").toString().contains("Metadata Expert")) {
                return new ResponseEntity<>("You are not a metadata expert or higher", HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>("Your login session has ended (or you sent a garbage cookie)", HttpStatus.UNAUTHORIZED);
        }
        return null;
    }

    public ResponseEntity isAdministrator(Cookie cookie) {
        if (cookie == null) {
            return new ResponseEntity("You are not an administrator (you are not logged in)" , HttpStatus.UNAUTHORIZED);
        }
        ClientResponse<ValidateResponse, Void> clientResponse = fusionAuthClient.validateJWT(cookie.getValue());
        if (clientResponse.wasSuccessful()) {
            if (!clientResponse.successResponse.jwt.otherClaims.get("roles").toString().contains("Administrator")) {
                return new ResponseEntity<>("You are not an administrator", HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>("Your login session has ended (or you sent a garbage cookie)", HttpStatus.UNAUTHORIZED);
        }
        return null;
    }

}
