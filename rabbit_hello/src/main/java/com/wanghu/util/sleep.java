package com.wanghu.util;

/**
 * @author wanghu
 * @date 2022/1/31 20:55
 */
public class sleep {
    public static void sleep(int second) {
        try {
            Thread.sleep(1000*second);
        } catch (InterruptedException _ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
