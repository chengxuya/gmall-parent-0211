package com.att.gmall.test.juc2;

public class Printing {
    private long num = 0;

    public synchronized void print0(){
        if(num!=1){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        num--;
        System.out.println(Thread.currentThread().getName()+"执行方法0，打印："+num);

        notifyAll();
    }

    public synchronized void print1(){
        if(num!=0){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        num++;
        System.out.println(Thread.currentThread().getName()+"执行方法1，打印："+num);

        notifyAll();
    }

}
