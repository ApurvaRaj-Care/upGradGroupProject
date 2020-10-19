package com.upgrad.quora.service.business;


import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDAO;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerBusinessService {
    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private QuestionDAO questionDao;


    /**
     * This method is used to create answer for questions asked by users
     *
     * @param answerEntity        for the particular question
     * @return creates the answer for particular question by Id
     * @throws AuthorizationFailedException If the access token provided by the user does not exist
     *                                      in the database, If the user has signed out
     * @throws InvalidQuestionException     If the question uuid entered by the user whose answer
     *                                      is to be posted does not exist in the database
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final AnswerEntity answerEntity) {
        return answerDao.createAnswer(answerEntity);
    }

    /**
     * This method is used to edit answer content
     * checks for all the conditions and provides necessary response messages
     *
     * @param content        entity that needed to be updated
     * @param answerId      Is the uuid of the answer that needed to be edited
     * @param userAuthTokenEntity user auth token entity
     * @return the answer after updating the content
     * @throws AuthorizationFailedException if access token does not exit, if user has signed out, if non-owner tries to edit
     * @throws AnswerNotFoundException      if answer with uuid which is to be edited does not exist in the database
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswerContent(final String content, final String answerId, final UserAuthTokenEntity userAuthTokenEntity)
            throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity answerEntity = answerDao.getAnswerByUUID(answerId);
        // If the answer with uuid which is to be edited does not exist in the database, throw 'AnswerNotFoundException'
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        } else {
            // if the user who is not the owner of the answer tries to edit the answer throw "AuthorizationFailedException"
            if (answerEntity.getUser().getId() != userAuthTokenEntity.getUser().getId()) {
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
            }
        }
        answerEntity.setAns(content);
        return answerDao.updateAnswerContent(answerEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity deleteAnswerContent(final String answerId, final UserAuthTokenEntity userAuthTokenEntity)
            throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity answerEntity = answerDao.getAnswerByUUID(answerId);
        // If the answer with uuid which is to be edited does not exist in the database, throw 'AnswerNotFoundException'
        if (answerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        } else {
            // if the user who is not the owner of the answer tries to edit the answer throw "AuthorizationFailedException"
            if (answerEntity.getUser().getId() != userAuthTokenEntity.getUser().getId() && userAuthTokenEntity.getUser().getRole().equalsIgnoreCase("nonadmin")) {
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
            }
        }
        return answerDao.deleteAnswerByAnswerUUID(answerId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<AnswerEntity> getAllAnswer(final String questionId)
            throws AuthorizationFailedException, AnswerNotFoundException {
        List<AnswerEntity> answerEntityList = answerDao.getAllAnswersByQuestionId(questionId);
        return answerEntityList;
    }

}
