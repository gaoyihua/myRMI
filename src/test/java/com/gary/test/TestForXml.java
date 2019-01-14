package com.gary.test;

import java.io.InputStream;

/**
 * describe:
 *
 * @author gary
 * @date 2019/01/13
 */
public class TestForXml {
    public static void main(String[] args) {
        InputStream is = TestForXml.class.getResourceAsStream("/test.xml");
        System.out.println(is);
    }
}
