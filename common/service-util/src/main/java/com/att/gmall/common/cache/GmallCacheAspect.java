package com.att.gmall.common.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.att.gmall.common.constant.RedisConst;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class GmallCacheAspect {
    @Autowired
    RedisTemplate redisTemplate;

    @Around("@annotation(com.att.gmall.common.cache.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point) {
        // 声明一个对象Object
        Object proceed = null;

        //拼接的缓存key
        Object[] args = point.getArgs();
        String id = new String( args[0]+"");
        //获得执行方法的注解,通过注解获得缓存前缀
        MethodSignature signature = (MethodSignature) point.getSignature();//通过反射获得当前要执行的方法信息
        Class returnType = signature.getReturnType();
        GmallCache annotation = signature.getMethod().getAnnotation(GmallCache.class);//获取方法的注解
        String prefix = annotation.prefix();//通过注解获取属性
        //拼接缓存key
        String cacheKey = prefix + id;

        //缓存查库
        proceed = cacheHit(returnType, cacheKey);
        //如果缓存有值
        if (proceed != null) {
            return proceed;

        } else {//缓存没值,查数据库
            //查数据库前要用分布式锁防止高并发,保证数据库操作的安全性
            String uid = UUID.randomUUID().toString();
            Boolean stockLock = redisTemplate.opsForValue().setIfAbsent(cacheKey + ":lock", uid, 1, TimeUnit.SECONDS);
            //判断是否有锁
            if (stockLock) {//有锁执行查询数据库
                try {
                    proceed = point.proceed(); //查询db数据库的方法,是被代理方法getSkuInfoNx(skuId)的执行
                    //cglib代理   直接变成字节码文件  没有编译过程所以很快
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                if (null==proceed) {//如果查询到数据库没有值,则创建一个空值的缓存对象,防止缓存穿透
                    try {
                        redisTemplate.opsForValue().set(cacheKey, returnType.newInstance());
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                //如果查询到数据库有值则把信息存到缓存  同步缓存
                redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(proceed));
                //删除锁
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                // 设置lua脚本返回的数据类型
                DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
                // 设置lua脚本返回类型为Long
                redisScript.setResultType(Long.class);
                redisScript.setScriptText(script);
                redisTemplate.execute(redisScript, Arrays.asList(cacheKey + ":lock"), uid);
            } else {//没有锁则自旋
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return cacheHit(returnType, cacheKey); //JSON.parseObject(cacheInfo = (String) redisTemplate.opsForValue().get(cacheKey)) ;
            }

        }
        return proceed;
    }

    private Object cacheHit(Class returnType, String cacheKey) {
        Object o = null;
        String cacheInfo = (String) redisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isNotEmpty(cacheInfo)) {
            o = JSON.parseObject(cacheInfo, returnType);
        }

        return o;
    }
}