package com.att.gmall.test.juc;

public class  MyTicker {
    private  Long num=100L;
            public synchronized long sale(){
                        num--;
                System.out.println(Thread.currentThread().getName()+"买走了一张票剩余:"+num);
                return num;
            }

    }

