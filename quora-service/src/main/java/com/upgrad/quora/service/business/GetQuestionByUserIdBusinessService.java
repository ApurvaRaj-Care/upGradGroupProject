package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDAO;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetQuestionByUserIdBusinessService {

    @Autowired
    QuestionDAO questionDAO;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity getQuestionByQuestionUUID(final String questionUUID)  {
        return questionDAO.getQuestionsById(questionUUID);
    }
}
