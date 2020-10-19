package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignoutBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    /**
     * Get UserAuthTokenEntity By access_token
     * @param accessToken
     * @return UserAuthTokenEntity
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity getAuthTokenEntityByToken(final String accessToken) {
        UserAuthTokenEntity userAuthTokenEntity =  userDao.getUserAuthTokenEntityByAccessToken(accessToken);
        return userAuthTokenEntity;
    }

    /**
     * update UserAuthTokenEntity
     * @param userAuthTokenEntity
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserAuthTokenEntity(final UserAuthTokenEntity userAuthTokenEntity) {
        userDao.updateUserAuthTokenEntity(userAuthTokenEntity);
    }
}
