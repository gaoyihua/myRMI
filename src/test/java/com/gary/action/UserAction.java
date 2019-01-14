package com.gary.action;

import com.gary.model.UserModel;

import java.util.List;

/**
 * describe:
 *
 * @author gary
 * @date 2019/01/12
 */
public interface UserAction {
    UserModel getUserById(String id);
    UserModel getUser(UserModel user);
    List<UserModel> getUserList();
}
