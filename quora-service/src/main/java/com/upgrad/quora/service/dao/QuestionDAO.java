package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class QuestionDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity){
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestions(){
        List allQuestions=new ArrayList<>();
        allQuestions= entityManager.createNamedQuery("allQuestions",QuestionEntity.class).getResultList();
        return allQuestions;
    }

    public List<QuestionEntity> getAllQuestionsByUserUUID(String userUUID){
        List allQuestions=new ArrayList<>();
        allQuestions= entityManager.createNamedQuery("questionByUserid",QuestionEntity.class).setParameter("uuid",userUUID).getResultList();
        return allQuestions;
    }

    public QuestionEntity getQuestionsById(String questionUUID){
        return  entityManager.createNamedQuery("questionById",QuestionEntity.class).setParameter("uuid",questionUUID).getSingleResult();
    }

    public QuestionEntity deleteQuestionById(String questionUUID){
        return  entityManager.createNamedQuery("deleteById",QuestionEntity.class).setParameter("uuid",questionUUID).getSingleResult();
    }

    public QuestionEntity updateQuestion(QuestionEntity questionEntity){
        entityManager.merge(questionEntity);
        return questionEntity;
    }
}
