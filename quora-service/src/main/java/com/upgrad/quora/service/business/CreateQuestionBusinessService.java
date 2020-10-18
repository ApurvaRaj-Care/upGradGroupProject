package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDAO;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CreateQuestionBusinessService {

    @Autowired
    private QuestionDAO questionDAO;

    @Transactional
    public QuestionEntity createQuestion(QuestionEntity questionEntity) throws AuthorizationFailedException {
       return questionDAO.createQuestion(questionEntity);
    }


}
