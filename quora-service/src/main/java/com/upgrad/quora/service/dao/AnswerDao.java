package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Saves the answer for the question
     *
     * @param answerEntity answer for the question
     * @return answer for the question
     */
    public AnswerEntity createAnswer(AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    /**
     * This method is to get a answer by uuid from db
     *
     * @param answerUUID is the uuid of answer to get from db
     * @return the answer present in db
     */
    public AnswerEntity getAnswerByUUID(String answerUUID) {
        try {
            return entityManager.createNamedQuery("answerByUUID", AnswerEntity.class).setParameter("uuid", answerUUID).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method is used to update the answer content to db
     *
     * @param answerEntity Is the answer that needed to be merged to db
     * @return answer that is merged in db
     */
    public AnswerEntity updateAnswerContent(AnswerEntity answerEntity) {
        entityManager.merge(answerEntity);
        return answerEntity;
    }

    public List<AnswerEntity> getAllAnswersByQuestionId(String questionId){
        List allAnswers=new ArrayList<AnswerEntity>();
        allAnswers= entityManager.createNamedQuery("allAnswers",AnswerEntity.class).getResultList();
        return allAnswers;
    }

    public AnswerEntity deleteAnswerByAnswerUUID(String  answerUUID) {
        try {
            return entityManager.createNamedQuery("deleteAnswerByUUID", AnswerEntity.class).setParameter("uuid", answerUUID).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
