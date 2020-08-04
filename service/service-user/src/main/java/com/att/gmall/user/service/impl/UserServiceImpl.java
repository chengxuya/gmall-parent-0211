package com.att.gmall.user.service.impl;

import com.att.gmall.model.user.UserInfo;
import com.att.gmall.user.mapper.UserInfoMapper;
import com.att.gmall.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{


    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public String getUserId(String token) {

        // 从缓存中根据token去除userId
        String userId = String.valueOf(redisTemplate.opsForValue().get("user:token:" + token));
        System.out.println(userId);
        System.out.println("service:");
        System.out.println(StringUtils.isEmpty(userId));
        System.out.println( "isNotEmpty:"+ org.apache.commons.lang3.StringUtils.isNotEmpty(userId));
        return userId;
    }

    @Override
    public String login(UserInfo userInfo) {

        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("login_name",userInfo.getLoginName());
        queryWrapper.eq("passwd",  DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes()));

        UserInfo userInfoReturn = userInfoMapper.selectOne(queryWrapper);

        if(null!=userInfoReturn){
            String token = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("user:token:" + token,userInfoReturn.getId());
            return token;
        }

        return null;
    }
}
