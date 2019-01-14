package com.gary.action.impl;

import com.gary.action.UserAction;
import com.gary.model.UserModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * describe:
 *
 * @author gary
 * @date 2019/01/12
 */
public class UserActionImpl implements UserAction, Serializable {

    @Override
    public UserModel getUserById(String id) {
        UserModel user = new UserModel();
        user.setId(id);
        user.setName("张三");
        user.setSex(true);

        return user;
    }

    @Override
    public List<UserModel> getUserList() {
        List<UserModel> userList = new ArrayList<>();

        UserModel user = new UserModel();
        user.setId("12345678");
        user.setName("张三");
        user.setSex(true);
        userList.add(user);

        user = new UserModel();
        user.setId("87654321");
        user.setName("李四");
        user.setSex(false);
        userList.add(user);

        return userList;
    }

    @Override
    public UserModel getUser(UserModel user) {
        return user;
    }
}
