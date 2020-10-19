package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDAO;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CreateQuestionBusinessService {

    @Autowired
    private QuestionDAO questionDAO;

    @Transactional
    public QuestionEntity createQuestion(QuestionEntity questionEntity)  {
       return questionDAO.createQuestion(questionEntity);
    }

    @Transactional
    public QuestionEntity editQuestion(QuestionEntity questionEntity) {
        return questionDAO.updateQuestion(questionEntity);
    }

    @Transactional
    public QuestionEntity deleteQuestion(String questionUUID) {
        return questionDAO.deleteQuestionById(questionUUID);
    }
}
