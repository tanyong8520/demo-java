package com.tany.demo.shiro;

import com.tany.demo.service.PermissionInfoService;
import com.tany.demo.service.RoleInfoService;
import com.tany.demo.service.UserInfoService;
import com.tany.demo.service.model.SysPermission;
import com.tany.demo.service.model.SysRole;
import com.tany.demo.service.model.UserInfo;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.List;

public class MyShiroRealm extends AuthorizingRealm{
    @Resource
    private UserInfoService userInfoService;

    @Resource
    private RoleInfoService  roleInfoService;

    @Resource
    private PermissionInfoService permissionInfoService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        Object principal =  principals.getPrimaryPrincipal();
        UserInfo userInfo = new UserInfo();
        BeanUtils.copyProperties(principal, userInfo);
        SysRole sysRole = roleInfoService.getRoleById(userInfo.getRoleId());
        authorizationInfo.addRole(sysRole.getRole());
        List<SysPermission> sysPermissionList = permissionInfoService.getPermissionByRoleId(sysRole.getId());
        for(SysPermission sysPermission : sysPermissionList){
            authorizationInfo.addStringPermission(sysPermission.getUrl());
        }


        return authorizationInfo;
    }

    /*主要是用来进行身份认证的，也就是说验证用户输入的账号和密码是否正确。*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
            throws AuthenticationException {
        System.out.println("MyShiroRealm.doGetAuthenticationInfo()");
        //获取用户的输入的账号.
        String username = (String) token.getPrincipal();
//        System.out.println(token.getCredentials());
        //通过username从数据库中查找 User对象，如果找到，没找到.
        //实际项目中，这里可以根据实际情况做缓存，如果不做，Shiro自己也是有时间间隔机制，2分钟内不会重复执行该方法
        UserInfo userInfo = userInfoService.findByUsername(username);
        System.out.println("----->>userInfo="+userInfo);
        if (userInfo == null) {
            throw new UnknownAccountException();
        }
        if (userInfo.getState() == 1) { //账户冻结
            throw new LockedAccountException();
        }
//        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
//                userInfo.getName(), //用户名
//                userInfo.getPassword(), //密码
//                ByteSource.Util.bytes(userInfo.getCredentialsSalt()),//salt=username+salt
//                getName()  //realm name
//        );
//        return authenticationInfo;
      return  new SimpleAuthenticationInfo(userInfo, userInfo.getPassword(), getName());

    }

}
