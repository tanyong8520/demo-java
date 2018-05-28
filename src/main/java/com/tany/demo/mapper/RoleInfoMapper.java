package com.tany.demo.mapper;

import com.tany.demo.service.model.SysRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface RoleInfoMapper {

    public SysRole queryRoleByRole(Map<String, Object> map);
}
