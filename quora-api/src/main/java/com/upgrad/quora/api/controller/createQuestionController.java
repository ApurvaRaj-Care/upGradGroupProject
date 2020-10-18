package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.CreateQuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class createQuestionController {

    @Autowired
    private CreateQuestionBusinessService createQuestionBusinessService;

    @RequestMapping(method= RequestMethod.POST,path="/question/create",consumes= MediaType.APPLICATION_JSON_UTF8_VALUE,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest) throws AuthorizationFailedException {

        final QuestionEntity questionEntity=new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());


        final QuestionEntity createdquestionEntity=createQuestionBusinessService.createQuestion(questionEntity);
        QuestionResponse questionResponse=new QuestionResponse().id(createdquestionEntity.getUuid()).status("CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse,HttpStatus.CREATED);
    }
}
