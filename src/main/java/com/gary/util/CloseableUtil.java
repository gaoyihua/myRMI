package com.gary.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * describe:Closeable工具类
 *
 * @author gary
 * @date 2019/01/12
 */
public class CloseableUtil {

    public static void close(Closeable...closeables) {
        if(closeables != null) {
            for (Closeable closeable : closeables) {
                try {
                    if (closeable != null) {
                        closeable.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    closeable = null;
                }
            }
        }
    }
}
