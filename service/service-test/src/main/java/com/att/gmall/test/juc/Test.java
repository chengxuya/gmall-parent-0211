package com.att.gmall.test.juc;

public class Test {
    public static void main(String[] args) {
        MyTicker myTicker=new MyTicker();
        for (int i = 0; i < 100; i++) {
            MyRunnableImpl myRunnable=new MyRunnableImpl(myTicker);
            new Thread(myRunnable).start();
        }
        new Thread(()->{
            myTicker.sale();

        }).start();
    }
}
