package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.*;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {
    @Autowired
    private AnswerBusinessService answerBusinessService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SignupBusinessService signupBusinessService;

    @Autowired
    private SignoutBusinessService signoutBusinessService;

    @Autowired
    GetQuestionByUserIdBusinessService getQuestionByUserIdBusinessService;

    /**
     * This method is used for the corresponding question which
     * is to be answered in the database
     *
     * @param questionId    To get respective question using unique key call questionId
     * @param authorization holds the Bearer access token for authenticating the user.
     * @return the response for the answer which is created along with httpStatus
     * @throws AuthorizationFailedException If the access token provided by the user does not exist
     *                                      in the database, If the user has signed out
     * @throws InvalidQuestionException     If the question uuid entered by the user whose answer
     *                                      is to be posted does not exist in the database
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create")
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest,
                                                       @PathVariable("questionId") final String questionId,
                                                       @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity uAuth = signoutBusinessService.getAuthTokenEntityByToken(authorization);
        if (uAuth == null) {
            throw new AuthorizationFailedException("SGR-001", "User has not Signed in");
        }

        if (uAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
        }

        QuestionEntity questionEntity = getQuestionByUserIdBusinessService.getQuestionByQuestionUUID(questionId);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }
        //Prepare answer
        final AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAns(answerRequest.getAnswer());
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setQuestion(questionEntity);
        answerEntity.setUser(uAuth.getUser());

        final AnswerEntity updatedAnswerEntity = answerBusinessService.createAnswer(answerEntity);
        AnswerResponse answerResponse = new AnswerResponse().id(updatedAnswerEntity.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    /**
     * This method is used to edit the content of a specfic answer in a database
     * Note,only the owner of the answer can edit the answer
     *
     * @param answerId          Is the uuid of the answer that needed to be edited
     * @param authorization     holds the Bearer access token for authenticating the user.
     * @param answerEditRequest Is uuid of the edited answer and message 'ANSWER EDITED' in the JSON response with the corresponding HTTP status.
     * @return answer uuid with the message 'ANSWER EDITED'
     * @throws AnswerNotFoundException      If answer with uuid which is to be edited does not exist in the database
     * @throws AuthorizationFailedException If access token does not exit : if user has signed out : if non-owner tries to edit
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}")
    public ResponseEntity<AnswerEditResponse> editAnswerContent(
            @PathVariable("answerId") final String answerId,
            @RequestHeader("authorization") final String authorization,
            final AnswerEditRequest answerEditRequest)
            throws AnswerNotFoundException, AuthorizationFailedException {

        UserAuthTokenEntity uAuth = signoutBusinessService.getAuthTokenEntityByToken(authorization);
        if (uAuth == null) {
            throw new AuthorizationFailedException("SGR-001", "User has not Signed in");
        }

        if (uAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        }
        final AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAns(answerEditRequest.getContent());
        final AnswerEntity editAnswerEntity = answerBusinessService.editAnswerContent(answerEditRequest.getContent(), answerId, uAuth);
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(editAnswerEntity.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}")
    public ResponseEntity<AnswerDeleteResponse> deleteAnswerContent(
            @PathVariable("answerId") final String answerId,
            @RequestHeader("authorization") final String authorization)
            throws AnswerNotFoundException, AuthorizationFailedException {

        UserAuthTokenEntity uAuth = signoutBusinessService.getAuthTokenEntityByToken(authorization);
        if (uAuth == null) {
            throw new AuthorizationFailedException("SGR-001", "User has not Signed in");
        }
        if (uAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        }

        final AnswerEntity deleteAnswerEntity = answerBusinessService.deleteAnswerContent(answerId, uAuth);
        AnswerDeleteResponse answerResponse = new AnswerDeleteResponse().id(deleteAnswerEntity.getUuid()).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerResponse, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}")
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswer(@PathVariable("questionId") final String questionId,
                                                       @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException,AnswerNotFoundException {

        UserAuthTokenEntity uAuth = signoutBusinessService.getAuthTokenEntityByToken(authorization);
        if (uAuth == null) {
            throw new AuthorizationFailedException("SGR-001", "User has not Signed in");
        }

        if (uAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
        }

        QuestionEntity questionEntity = getQuestionByUserIdBusinessService.getQuestionByQuestionUUID(questionId);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }

        List<AnswerDetailsResponse> adr = new ArrayList<AnswerDetailsResponse>();
        final List<AnswerEntity> ansList = answerBusinessService.getAllAnswer(questionId);
        for(AnswerEntity ae : ansList){
            adr.add(new AnswerDetailsResponse().id(ae.getUuid()).answerContent(ae.getAns()).questionContent(questionEntity.getContent()));
        }

        return new ResponseEntity<List<AnswerDetailsResponse>>(adr, HttpStatus.CREATED);
    }
}

