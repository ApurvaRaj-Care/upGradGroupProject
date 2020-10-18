package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminBusinessService {
    @Autowired
    private UserDao userDao;

    public UserEntity deleteUser(final String uuid) throws AuthorizationFailedException, UserNotFoundException {
        return userDao.deleteUserByUUID(uuid);
    }
}
