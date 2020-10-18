package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.service.business.GetAllQuestionsBusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class GetAllQuestionsController {

    @Autowired
    private GetAllQuestionsBusinessService getAllQuestionsBusinessService;

    @RequestMapping(method= RequestMethod.GET,path="/question/all",produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
        public ResponseEntity<QuestionDetailsResponse> getAllQuestions(){
        getAllQuestionsBusinessService.getAllQuestions();


    }
}
