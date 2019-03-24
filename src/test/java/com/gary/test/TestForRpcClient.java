package com.gary.test;

import com.gary.action.UserAction;
import com.gary.core.EServiceCommand;
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
        RpcClient rpcClient = new RpcClient(EServiceCommand.VIDEO_SERVICE);

        UserAction proxy = rpcClient.getProxy(UserAction.class);
        //UserModel user1 = proxy.getUserById("123456");
        UserModel user2 = new UserModel();
        user2.setName("哈哈哈");
        UserModel user = new UserModel();
        user.setName("哈哈哈");
        user2.setNext(user);
        UserModel model = proxy.getUser(user2, user2);
        System.out.println(model);
        UserModel mode2 = proxy.getUser(user2, user2);
        System.out.println(mode2);
        //System.out.println(user1);
    }

}
