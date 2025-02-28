package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.SignoutBusinessService;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.UUID;

import static java.time.ZonedDateTime.now;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private SignupBusinessService signupBusinessService;

    @Autowired
    private SignoutBusinessService signoutBusinessService;

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * POST request signup
     * This endpoint requests for all the attributes in 'SignupUserRequest' about the user.
     *
     * @param signupUserRequest
     * @return SignupUserResponse
     * 'uuid' of the registered user and message 'USER SUCCESSFULLY REGISTERED'
     * @throws SignUpRestrictedException If the username provided already exists in the current database,
     *                                   throw ‘SignUpRestrictedException’
     *                                   with the message code -'SGR-001' and message - 'Try any other Username, this Username has already been taken'.
     *                                   If the email Id provided by the user already exists in the current database,
     *                                   throw ‘SignUpRestrictedException’
     *                                   with the message code -'SGR-002' and message -'This user has already been registered, try with any other emailId'.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> userSignup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        //throw exception if username exists
        if (signupBusinessService.getUserByUserName(signupUserRequest.getUserName()) != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }
        //throw exception if email exists
        if (signupBusinessService.getUserByEmail(signupUserRequest.getEmailAddress()) != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        //Create new User
        final UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setContactNumber(signupUserRequest.getContactNumber());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setRole("nonadmin");

        final UserEntity createdUserEntity = signupBusinessService.signup(userEntity);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }

    /**
     * POST request signin
     * This endpoint is used for user authentication.
     * After successful authentication, JWT token is given to a user.     *
     *
     * @param authorization User credentials to be passed in the authorization field of header as part of Basic authentication.
     *                      "Basic username:password" (where username:password of the String is encoded to Base64 format) in the authorization header.
     * @return SigninResponse()
     * 'uuid' of the authenticated user
     * message 'SIGNED IN SUCCESSFULLY' in the JSON response with the corresponding HTTP status.
     * access_token field of the Response Header
     * @throws AuthenticationFailedException If the username provided by the user does not exist,
     *                                       with the message code -'ATH-001' and message-'This username does not exist'.
     *                                       If the password provided by the user does not match the password in the existing database,
     *                                       with the message code -'ATH-002' and message -'Password failed'.
     */
    @RequestMapping(method = RequestMethod.POST, path = "/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        //Decode the authorization header
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        //Authenticate the user
        UserAuthTokenEntity userAuthToken = authenticationService.authenticate(decodedArray[0], decodedArray[1]);
        UserEntity user = userAuthToken.getUser();
        //Prepare response
        SigninResponse authorizedUserResponse = new SigninResponse().id(user.getUuid()).message("SIGNED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("access_token", userAuthToken.getAccessToken());
        return new ResponseEntity<SigninResponse>(authorizedUserResponse, headers, HttpStatus.OK);
    }


    /**
     * POST request signout
     * Sign out from the Quora Application.
     * The user cannot access any other endpoint once he is signed out of the application.
     *
     * @param authorization access token of the signed in user in the authorization field of the Request Header
     * @return SignoutResponse
     * 'uuid' of the signed out user and message 'SIGNED OUT SUCCESSFULLY'
     * @throws SignOutRestrictedException If the access token provided by the user does not exist in the database
     *                                    'SGR-001' and message - 'User is not Signed in'
     */

    @RequestMapping(method = RequestMethod.GET, path = "/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> getUser(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {
        UserAuthTokenEntity uAuth = signoutBusinessService.getAuthTokenEntityByToken(authorization);
        if (uAuth == null || uAuth.getLogoutAt() != null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
        uAuth.setLogoutAt(now());
        signoutBusinessService.updateUserAuthTokenEntity(uAuth);

        SignoutResponse signoutResponse = new SignoutResponse().id(uAuth.getUser().getUuid()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }
}
