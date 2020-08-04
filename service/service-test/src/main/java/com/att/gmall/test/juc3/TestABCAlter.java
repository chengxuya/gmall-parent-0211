package com.att.gmall.test.juc3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestABCAlter {

}
class  AlterDemo{
    private  int num=1;
    private Lock lock=new ReentrantLock();
    private Condition  condition=lock.newCondition();
    private Condition  condition1=lock.newCondition();
    private Condition  condition2=lock.newCondition();
    public  void  loopA(){
        lock.lock();;
        try {
            //1.判断
            if (num!=1){
                condition1.await();
            }
            //2.打印
            for (int i = 1; i <=5 ; i++) {
                System.out.println(Thread.currentThread().getName()+i);

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}
