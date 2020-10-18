package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.SignoutBusinessService;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired
    private SignoutBusinessService signoutBusinessService;

    @Autowired
    private SignupBusinessService signupBusinessService;

    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUser(@PathVariable("userId") final String userUuid,
                                                       @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        //String [] bearerToken = authorization.split("Bearer ");
        UserAuthTokenEntity uAuth = signoutBusinessService.getAuthTokenEntityByToken(authorization);
        if(uAuth==null){
            throw new AuthorizationFailedException("SGR-001","User has not Signed in");
        }

        if(uAuth.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        }

        UserEntity userEntity = signupBusinessService.getUserByUUID(userUuid);

        if(userEntity ==null){
            throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
        }

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().userName(userEntity.getFirstName()).lastName(userEntity.getLastName()).userName(userEntity.getUserName()).emailAddress(userEntity.getEmail()).country(userEntity.getCountry()).aboutMe(userEntity.getAboutMe()).dob(userEntity.getDob()).contactNumber(userEntity.getContactNumber());
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }
}
