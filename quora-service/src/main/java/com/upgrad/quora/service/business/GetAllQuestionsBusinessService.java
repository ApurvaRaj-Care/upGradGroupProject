package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDAO;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllQuestionsBusinessService {
    @Autowired
    private QuestionDAO questionDAO;

    public List<QuestionEntity> getAllQuestions(){
        return questionDAO.getAllQuestions();
    }

    public List<QuestionEntity> getAllQuestionsByUserId(String userUUID){
        return questionDAO.getAllQuestionsByUserUUID(userUUID);
    }


}
