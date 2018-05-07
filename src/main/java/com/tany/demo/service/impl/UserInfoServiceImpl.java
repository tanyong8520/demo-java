package com.tany.demo.service.impl;

import com.tany.demo.mapper.UserInfoMapper;
import com.tany.demo.service.UserInfoService;
import com.tany.demo.service.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo findByUsername(String userName) {
        Map<String, Object> map = new HashMap<>();
        map.put("name",userName);
        return  userInfoMapper.queryObjectByName(map);
    }
}
