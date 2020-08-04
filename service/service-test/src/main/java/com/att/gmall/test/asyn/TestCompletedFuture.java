package com.att.gmall.test.asyn;

import com.att.gmall.model.product.BaseCategoryView;
import com.att.gmall.model.product.SkuInfo;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TestCompletedFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
   //     a();// 不是级联不打印异常信息 ,  a方法 会用堆方法的自由变量,即是自己设置的值
   //     b();//b是级联写法,不用自由变量
//一般在容易出现异常，并且对线程使用了exceptionally线程异常处理的方法后，用级联写法，不会出现栈的变量的异常，
// 否则就会影响主线程执行，并且返回空的线程执行结果
        //级联写法
       // CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(null).exceptionally(null).whenComplete(null).get();
        //自由变量写法
            //如a 全部拆开写

    //查询商品详情的代码
        CompletableFuture<SkuInfo> completableFutureInfo = CompletableFuture.supplyAsync(new Supplier<SkuInfo>(){
            @Override
            public SkuInfo get() {

                SkuInfo skuInfo=new SkuInfo();
                System.out.println("查询skuInfo的线程执行");
                        skuInfo.setSkuName("niubi商品");
                        skuInfo.setCategory3Id(61l);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return skuInfo;
            }
    });
        CompletableFuture<BigDecimal> completableFuturePrice = CompletableFuture.supplyAsync(new Supplier<BigDecimal>(){
            @Override
            public BigDecimal get() {
                System.out.println("查询商品价格线程");
                return new BigDecimal(100);
            }
        });
        //查询商品分类的线程
        CompletableFuture<BaseCategoryView> baseCategoryViewCompletableFuture = completableFutureInfo.thenApply(new Function<SkuInfo, BaseCategoryView>() {
            @Override
            public BaseCategoryView apply(SkuInfo skuInfo) {
                BaseCategoryView baseCategoryView = new BaseCategoryView();
                baseCategoryView.setCategory3Id(skuInfo.getCategory3Id());
                return baseCategoryView;
            }
        });
        CompletableFuture.allOf(completableFutureInfo,completableFuturePrice).join();
        SkuInfo skuInfo =completableFutureInfo.get();
        BaseCategoryView baseCategoryView=baseCategoryViewCompletableFuture.get();
        BigDecimal price =completableFuturePrice.get();
        Thread.sleep(3000);

        System.out.println(skuInfo.getSkuName());

        System.out.println(price);

        System.out.println(baseCategoryView.getCategory3Id());
}
    private static void b() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(new Supplier<Long>(){
            @Override
            public Long get() {
                System.out.println("completableFuture线程开始执行");
                int i = 1/0;
                return 1024l;
            }
        }).exceptionally(new Function<Throwable, Long>() {
            @Override
            public Long apply(Throwable throwable) {
                System.out.println("出异常");
                return 1L;
            }
        }).whenComplete(new BiConsumer<Long, Throwable>() {
            @Override
            public void accept(Long aLong, Throwable throwable) {
                System.out.println("whencomple"+aLong);
            }
        });//级联打印里面内容
        Long aLong = completableFuture.get();
        //主线程抛异常走不下去
        System.out.println("主线程"+aLong);
    }


    private static void a() throws InterruptedException, ExecutionException {
        CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(new Supplier<Long>(){
                                              @Override
                                              public Long get() {
                                                  System.out.println("completableFuture线程开始执行");
                                                  int i = 1/0;
                                                  return 1024l;
                                              }
                                                                                 });
        completableFuture.exceptionally(new Function<Throwable, Long>() {
            @Override
            public Long apply(Throwable throwable) {
                System.out.println("出异常");
                return 1L;
            }
        });
        completableFuture.whenComplete(new BiConsumer<Long, Throwable>() {
            @Override
            public void accept(Long aLong, Throwable throwable) {
                System.out.println("whencomple"+aLong);
            }
        });
        Long aLong = completableFuture.get();
        //主线程抛异常走不下去
        System.out.println("主线程"+aLong);
    }

}
