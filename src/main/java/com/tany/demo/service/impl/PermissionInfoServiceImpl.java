package com.tany.demo.service.impl;

import com.tany.demo.mapper.PermissionInfoMapper;
import com.tany.demo.service.PermissionInfoService;
import com.tany.demo.service.model.SysPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PermissionInfoServiceImpl implements PermissionInfoService {
    @Autowired
    private PermissionInfoMapper permissionInfoMapper;

    @Override
    public List<SysPermission> getPermissionByRoleId(Long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("roleId",id);
        return permissionInfoMapper.queryPermissionByRole(map);
    }
}
