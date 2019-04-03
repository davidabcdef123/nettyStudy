package com.david.nio.channel;

/**
 * Created by sc on 2019-04-03.
 */
public class Test {

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                System.out.println("当前状态："+Thread.currentThread().isInterrupted());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("状态："+Thread.currentThread().isInterrupted());
            }
        }).start();
    }
}
