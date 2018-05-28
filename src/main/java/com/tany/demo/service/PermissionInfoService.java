package com.tany.demo.service;

import com.tany.demo.service.model.SysPermission;

import java.util.List;

public interface PermissionInfoService {
    public List<SysPermission> getPermissionByRoleId(Long id);
}
