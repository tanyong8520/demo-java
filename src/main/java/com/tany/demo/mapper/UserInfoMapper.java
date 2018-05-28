package com.tany.demo.mapper;

import com.tany.demo.service.model.UserAndRole;
import com.tany.demo.service.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserInfoMapper {
    UserAndRole queryObjectByName(Map<String, Object> map);

    UserInfo queryUserByName(Map<String, Object> map);
}
