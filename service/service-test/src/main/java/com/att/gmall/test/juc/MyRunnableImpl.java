package com.att.gmall.test.juc;

public class MyRunnableImpl implements  Runnable {
    MyTicker myTicker;
    public  MyRunnableImpl(MyTicker myTicker){
        this.myTicker=myTicker;
    }
    @Override
    public void run() {
        myTicker.sale();
    }
}
