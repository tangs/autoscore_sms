package com.tangs.myapplication.ui.main.data;

import com.tangs.myapplication.ui.main.UserDataSource;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class LocalUserDataSource implements UserDataSource {

    private final UserDao userDao;

    public LocalUserDataSource(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Flowable<List<User>> getAll() {
        return userDao.getAll();
    }

    @Override
    public Flowable<List<User>> loadAllByIds(int[] userIds) {
        return userDao.loadAllByIds(userIds);
    }

    @Override
    public User findByName(String first, String last) {
        return userDao.findByName(first, last);
    }

    @Override
    public Completable insertAll(User... users) {
        return userDao.insertAll(users);
    }

    @Override
    public void delete(User user) {
        userDao.delete(user);
    }
}
