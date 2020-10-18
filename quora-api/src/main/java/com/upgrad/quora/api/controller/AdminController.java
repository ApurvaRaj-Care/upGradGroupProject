package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
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
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private SignupBusinessService signupBusinessService;

    @Autowired
    private SignoutBusinessService signoutBusinessService;

    @Autowired
    private AdminBusinessService adminBusinessService;

    @RequestMapping(method = RequestMethod.DELETE, path = "/userprofile/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> getUser(@PathVariable("userId") final String userUuid,
                                                       @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity uAuth = signoutBusinessService.getAuthTokenEntityByToken(authorization);
        if(uAuth==null){
            throw new AuthorizationFailedException("SGR-001","User has not Signed in");
        }

        if(uAuth.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out");
        }

        if(uAuth.getUser().getRole().equalsIgnoreCase("nonadmin")){
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
        }

        UserEntity userEntity = signupBusinessService.getUserByUUID(userUuid);

        if(userEntity ==null){
            throw new UserNotFoundException("USR-001","User with entered uuid to be deleted does not exist");
        }

        adminBusinessService.deleteUser(userEntity.getUuid());

        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(userEntity.getUuid()).status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);
    }
}
