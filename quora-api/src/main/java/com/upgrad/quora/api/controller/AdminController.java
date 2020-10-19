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

/**
 * AdminController : Controller class for admin users
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private SignupBusinessService signupBusinessService;

    @Autowired
    private SignoutBusinessService signoutBusinessService;

    @Autowired
    private AdminBusinessService adminBusinessService;

    /**
     * DELETE Request  /admin/user/{userId}
     * This endpoint is used to delete a user from the Quora Application.
     * Only an admin is authorized to access this endpoint.
     *
     * @param userUuid      path variable 'userId' as a string for the corresponding user which is to be deleted
     * @param authorization access token of the signed in user as a string in authorization Request Header
     * @return UserDeleteResponse
     * user profile
     * @throws AuthorizationFailedException If the access token provided by the user does not exist,
     *                                      'ATHR-001' and message -'User has not signed in'
     *                                      If the user has signed out,
     *                                      'ATHR-002' and message -'User is signed out'
     *                                      If the role of the user is 'nonadmin',
     *                                      'ATHR-003' and message -'Unauthorized Access, Entered user is not an admin'     *
     * @throws UserNotFoundException        If the user with uuid whose profile is to be deleted does not exist
     *                                      'USR-001' and message -'User with entered uuid to be deleted does not exist'
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> getUser(@PathVariable("userId") final String userUuid,
                                                      @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthTokenEntity uAuth = signoutBusinessService.getAuthTokenEntityByToken(authorization);
        if (uAuth == null) {
            throw new AuthorizationFailedException("SGR-001", "User has not Signed in");
        }

        if (uAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }

        if (uAuth.getUser().getRole().equalsIgnoreCase("nonadmin")) {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }

        //fetch user to be deleted by uuid
        UserEntity userEntity = signupBusinessService.getUserByUUID(userUuid);

        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }
        adminBusinessService.deleteUser(userEntity.getUuid());
        UserDeleteResponse userDeleteResponse = new UserDeleteResponse()
                .id(userEntity.getUuid())
                .status("USER SUCCESSFULLY DELETED");
        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);
    }
}
