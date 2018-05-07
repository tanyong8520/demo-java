package com.tany.demo.service;


import com.tany.demo.service.model.UserInfo;

public interface UserInfoService {
    UserInfo findByUsername(String userName);
}
