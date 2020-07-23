package com.att.gmall.test.juc;

public class TestThread {


    public static void main(String[] args) {
        new  Thread(()->{
            System.out.println(0);

        },"t1").start();
        new  Thread(()->{
            System.out.println(0);

        },"t2").start();
    }
}
