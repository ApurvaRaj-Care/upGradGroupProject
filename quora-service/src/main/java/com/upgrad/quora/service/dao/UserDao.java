package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Create new user
     * @param userEntity : to be created
     * @return UserEntity : created user entity
     */
    public UserEntity createUser(UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    /**
     * Create UserAuthTokenEntity
     * @param userAuthTokenEntity : to be created
     * @return UserAuthTokenEntity : created entity
     */
    public UserAuthTokenEntity createUserAuthTokenEntity(final UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    /**
     * get user by email
     * @param email : email id
     * @return UserEntity : fetched user entity
     */
    public UserEntity getUserByEmail(final String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * get user by uuid
     * @param uuid : uuid of user
     * @return UserEntity : fetched user entity
     */
    public UserEntity getUserByUUID(final String uuid) {
        try {
            return entityManager.createNamedQuery("userByUUID", UserEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * get user by userName
     * @param username : username of user
     * @return UserEntity : fetched user entity
     */
    public UserEntity getUserByUserName(final String username) {
        try {
            return entityManager.createNamedQuery("userByUserName", UserEntity.class).setParameter("username", username).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Delete user
     * @param uuid : uuid of user to be deleted
     * @return UserEntity : deleted user entity
     */
    public UserEntity deleteUserByUUID(final String uuid) {
        try {
            return entityManager.createNamedQuery("deleteUserByUUID", UserEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * get UserAuthTokenEntity by accessToken
     * @param accessToken : user access token
     * @return UserAuthTokenEntity : Fetched UserAuthTokenEntity
     */
    public UserAuthTokenEntity getUserAuthTokenEntityByAccessToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * update UserAuthTokenEntity
     * @param updatedUserAuthTokenEntity to be updated
     */
    public void updateUserAuthTokenEntity(final UserAuthTokenEntity updatedUserAuthTokenEntity) {
        entityManager.merge(updatedUserAuthTokenEntity);
    }

    /**
     * update user
     * @param updatedUserEntity to be updated
     */
    public void updateUserEntity(final UserEntity updatedUserEntity) {
        entityManager.merge(updatedUserEntity);
    }
}
