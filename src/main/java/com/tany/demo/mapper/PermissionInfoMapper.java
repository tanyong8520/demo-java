package com.tany.demo.mapper;

import com.tany.demo.service.model.SysPermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PermissionInfoMapper {

    public List<SysPermission> queryPermissionByRole(Map<String, Object> map);
}
