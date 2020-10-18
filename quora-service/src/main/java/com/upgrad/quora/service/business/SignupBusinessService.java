package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    public UserEntity getUserByUserName(final String username) {
        UserEntity userEntity =  userDao.getUserByUserName(username);
        return userEntity;
    }

    public UserEntity getUserByEmail(final String email){
        UserEntity userEntity =  userDao.getUserByEmail(email);
        return userEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) {
        String[] encryptedText = passwordCryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        return userDao.createUser(userEntity);
    }

    public UserEntity getUserByUUID(String userUuid) {
        UserEntity userEntity =  userDao.getUserByUUID(userUuid);
        return userEntity;
    }
}
