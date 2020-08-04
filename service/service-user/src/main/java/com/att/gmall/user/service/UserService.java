package com.att.gmall.user.service;

import com.att.gmall.model.user.UserInfo;

public interface UserService {
    String getUserId(String token);

    String login(UserInfo userInfo);
}
