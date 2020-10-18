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

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity getAuthTokenEntityByToken(final String authToken) {
        UserAuthTokenEntity userAuthTokenEntity =  userDao.getAuthToken(authToken);
        return userAuthTokenEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateAuthTokenEntityByToken(final UserAuthTokenEntity entity) {
        userDao.updateAuthToken(entity);
    }
}
