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

    @RequestMapping(method = RequestMethod.POST, path = "/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> userSignup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {
        //throw exception if username exists
        if(signupBusinessService.getUserByUserName(signupUserRequest.getUserName()) != null){
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }
        //throw exception if email exists
        if(signupBusinessService.getUserByEmail(signupUserRequest.getEmailAddress()) != null){
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
        userEntity.setSalt("1234abc");
        userEntity.setRole("nonadmin");
        final UserEntity createdUserEntity = signupBusinessService.signup(userEntity);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        UserAuthTokenEntity userAuthToken = authenticationService.authenticate(decodedArray[0], decodedArray[1]);
        UserEntity user = userAuthToken.getUser();
        SigninResponse authorizedUserResponse = new SigninResponse().id(user.getUuid()).message("SIGNED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("access_token", userAuthToken.getAccessToken());
        return new ResponseEntity<SigninResponse>(authorizedUserResponse, headers, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> getUser(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException,AuthenticationFailedException {
        UserAuthTokenEntity uAuth = signoutBusinessService.getAuthTokenEntityByToken(authorization);
        if(uAuth==null){
            throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        }
        uAuth.setLogoutAt(now());
        signoutBusinessService.updateAuthTokenEntityByToken(uAuth);
        SignoutResponse signoutResponse = new SignoutResponse().id(uAuth.getUser().getUuid()).message("SIGNED OUT SUCCESSFULLY");

        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }



}
