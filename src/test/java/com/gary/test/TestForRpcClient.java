package com.gary.test;

import com.gary.action.UserAction;
import com.gary.action.impl.UserActionImpl;
import com.gary.core.RpcClient;
import com.gary.model.UserModel;

/**
 * describe:
 *
 * @author gary
 * @date 2019/01/12
 */
public class TestForRpcClient {

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient("127.0.0.1", 54189);

//        UserAction proxy = rpcClient.getProxy(UserActionImpl.class);
//        UserModel user1 = proxy.getUserById("123456");
//        UserModel user2 = new UserModel();
//        user2.setName("哈哈哈");
//        UserModel model = proxy.getUser(user2);
//        System.out.println(model);
//        System.out.println(user1);

        UserAction proxy = rpcClient.getProxy(UserAction.class);
        //UserModel user1 = proxy.getUserById("123456");
        UserModel user2 = new UserModel();
        user2.setName("哈哈哈");
        UserModel user = new UserModel();
        user.setName("哈哈哈");
        user2.setNext(user);
        UserModel model = proxy.getUser(user2, user2);
        System.out.println(model);
        //System.out.println(user1);
    }

}
