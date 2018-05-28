package com.tany.demo.service.impl;

import com.tany.demo.mapper.RoleInfoMapper;
import com.tany.demo.service.RoleInfoService;
import com.tany.demo.service.model.SysRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("roleInfoService")
public class RoleInfoServiceImpl implements RoleInfoService{
    @Autowired
    private RoleInfoMapper roleInfoMapper;

    @Override
    public SysRole getRoleById(Long id) {
        Map<String, Object> map = new HashMap<>();
        map.put("roleId",id);
        return roleInfoMapper.queryRoleByRole(map);
    }
}
