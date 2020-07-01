package com.tangs.myapplication.ui.main;

import com.tangs.myapplication.ui.main.data.User;


import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public interface UserDataSource {

    Flowable<List<User>> getAll();

    Flowable<List<User>> loadAllByIds(int[] userIds);

    User findByName(String first, String last);

    Completable insertAll(User... users);

    void delete(User user);
}
