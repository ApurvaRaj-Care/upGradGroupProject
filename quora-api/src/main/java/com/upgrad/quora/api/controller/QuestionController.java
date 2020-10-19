package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.*;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/")
public class QuestionController {
    @Autowired
    private CreateQuestionBusinessService createQuestionBusinessService;

    @Autowired
    private GetAllQuestionsBusinessService getAllQuestionsBusinessService;

    @Autowired
    private GetQuestionByUserIdBusinessService getQuestionByUserIdBusinessService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SignupBusinessService signupBusinessService;

    @Autowired
    private SignoutBusinessService signoutBusinessService;

    @RequestMapping(method= RequestMethod.POST,path="/question/create",consumes= MediaType.APPLICATION_JSON_UTF8_VALUE,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest,@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        UserAuthTokenEntity uAuth = signoutBusinessService.getAuthTokenEntityByToken(authorization);
        if (uAuth == null) {
            throw new AuthorizationFailedException("SGR-001", "User has not Signed in");
        }

        if (uAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }
        //Prepare Question
        final QuestionEntity questionEntity=new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());

        final QuestionEntity createdQuestionEntity=createQuestionBusinessService.createQuestion(questionEntity);
        QuestionResponse questionResponse=new QuestionResponse().id(createdQuestionEntity.getUuid()).status("CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }


    @RequestMapping(method = RequestMethod.GET, path = "/question/all")
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException, AnswerNotFoundException {

        UserAuthTokenEntity uAuth = signoutBusinessService.getAuthTokenEntityByToken(authorization);
        if (uAuth == null) {
            throw new AuthorizationFailedException("SGR-001", "User has not Signed in");
        }

        if (uAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }
        List<QuestionDetailsResponse> adr = new ArrayList<QuestionDetailsResponse>();
        List<QuestionEntity> questionEntityList = getAllQuestionsBusinessService.getAllQuestions();
        for(QuestionEntity qe :questionEntityList){
            adr.add(new QuestionDetailsResponse().id(qe.getUuid()).content(qe.getContent()));
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(adr, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}")
    public ResponseEntity<QuestionEditResponse> editQuestionContent(
            @PathVariable("questionId") final String questionId,
            @RequestHeader("authorization") final String authorization,
            final QuestionEditRequest questionEditRequest)
            throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity uAuth = signoutBusinessService.getAuthTokenEntityByToken(authorization);
        if (uAuth == null) {
            throw new AuthorizationFailedException("SGR-001", "User has not Signed in");
        }

        if (uAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }

        QuestionEntity questionEntity = getQuestionByUserIdBusinessService.getQuestionByQuestionUUID(questionId);

        if(questionEntity == null){
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
        }

        if(questionEntity.getUser().getUuid() != uAuth.getUser().getUuid()){
            throw new AuthorizationFailedException("ATHR-003","Only the question owner can edit the question");
        }

        questionEntity.setContent(questionEditRequest.getContent());
        final QuestionEntity editedQuestionEntity=createQuestionBusinessService.editQuestion(questionEntity);

        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(editedQuestionEntity.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/question/delete/{questionId}")
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
            @PathVariable("questionId") final String questionId,
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthTokenEntity uAuth = signoutBusinessService.getAuthTokenEntityByToken(authorization);
        if (uAuth == null) {
            throw new AuthorizationFailedException("SGR-001", "User has not Signed in");
        }

        if (uAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }

        QuestionEntity questionEntity = getQuestionByUserIdBusinessService.getQuestionByQuestionUUID(questionId);

        if(questionEntity == null){
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
        }

        if(questionEntity.getUser().getUuid() != uAuth.getUser().getUuid() && uAuth.getUser().getRole().equalsIgnoreCase("nonadmin")){
            throw new AuthorizationFailedException("ATHR-003","Only the question owner or admin can delete the question");
        }

        final QuestionEntity editedQuestionEntity=createQuestionBusinessService.deleteQuestion(questionId);

        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(editedQuestionEntity.getUuid()).status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "question/all/{userId}")
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUserId(@PathVariable("userId") final String userId,@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException, UserNotFoundException {

        UserAuthTokenEntity uAuth = signoutBusinessService.getAuthTokenEntityByToken(authorization);
        if (uAuth == null) {
            throw new AuthorizationFailedException("SGR-001", "User has not Signed in");
        }

        if (uAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }

        UserEntity userEntity = signupBusinessService.getUserByUUID(userId);

        if(userEntity == null){
            throw new UserNotFoundException("USR-001","User with entered uuid whose question details are to be seen does not exist");
        }

        List<QuestionDetailsResponse> adr = new ArrayList<QuestionDetailsResponse>();
        List<QuestionEntity> questionEntityList = getAllQuestionsBusinessService.getAllQuestionsByUserId(userId);
        for(QuestionEntity qe :questionEntityList){
            adr.add(new QuestionDetailsResponse().id(qe.getUuid()).content(qe.getContent()));
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(adr, HttpStatus.CREATED);
    }



}
