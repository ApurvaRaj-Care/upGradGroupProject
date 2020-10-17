package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDAO;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateQuestionBusinessService {

    @Autowired
    private QuestionDAO questionDAO;

    public QuestionEntity createQuestion(QuestionEntity questionEntity){
       return questionDAO.createQuestion(questionEntity);
    }


}
