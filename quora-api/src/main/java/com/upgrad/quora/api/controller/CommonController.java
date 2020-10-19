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

    /**
     * GET request userprofile/{userId}
     * This endpoint is used to get the details of any user in the Quora Application.
     * This endpoint can be accessed by any user in the application.
     *
     * @param userUuid      'userId' as a string for the corresponding user profile to be retrieved
     * @param authorization access token of the signed in user as a string in authorization Request Header
     * @return UserDetailsResponse
     * all the details of the user
     * @throws AuthorizationFailedException If the access token provided by the user does not exist
     *                                      'ATHR-001' and message - 'User has not signed in'
     *                                      If the user has signed out,
     *                                      'ATHR-002' and message -'User is signed out.Sign in first to get user details'
     * @throws UserNotFoundException        If the user with uuid whose profile is to be retrieved does not exist
     *                                      'USR-001' and message -'User with entered uuid does not exist'
     */
    @RequestMapping(method = RequestMethod.GET, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getUser(@PathVariable("userId") final String userUuid,
                                                       @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        //String [] bearerToken = authorization.split("Bearer ");
        UserAuthTokenEntity uAuth = signoutBusinessService.getAuthTokenEntityByToken(authorization);
        if (uAuth == null) {
            throw new AuthorizationFailedException("SGR-001", "User has not Signed in");
        }

        if (uAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }

        UserEntity userEntity = signupBusinessService.getUserByUUID(userUuid);

        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse()
                .userName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .userName(userEntity.getUserName())
                .emailAddress(userEntity.getEmail())
                .country(userEntity.getCountry())
                .aboutMe(userEntity.getAboutMe())
                .dob(userEntity.getDob())
                .contactNumber(userEntity.getContactNumber());
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }
}
