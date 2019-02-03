package com.revolut.dao;

import java.util.List;

import com.revolut.model.User;

public interface UserDao {

    List<User> getAllUsers();

    User getUserByName(String userName);

}
