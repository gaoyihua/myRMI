package com.gary.test;

import com.gary.core.ServerManageCenter;

/**
 * describe:
 *
 * @author gary
 * @date 2019/01/23
 */
public class TestForManageCenter {
    public static void main(String[] args) {
        try {
            ServerManageCenter server = new ServerManageCenter(54100);
            server.startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
