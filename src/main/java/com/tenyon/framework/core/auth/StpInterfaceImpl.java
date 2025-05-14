package com.tenyon.framework.core.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.tenyon.common.constant.UserConstant;
import com.tenyon.web.domain.entity.User;
import com.tenyon.web.service.MenuService;
import com.tenyon.web.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * sa-token权限验证接口实现
 *
 * @author yovvis
 * @date 2025/1/4
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private MenuService menuService;
    
    @Autowired
    private RoleService roleService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 获取登录用户的权限列表
        User currentUser = (User) StpUtil.getSession().get(UserConstant.USER_LOGIN_STATE);
        if (currentUser == null) {
            return new ArrayList<>();
        }
        // 调用权限服务获取用户所有的权限列表（根据用户ID查询）
        return menuService.getUserPermissions(currentUser.getId());
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 获取当前登录用户
        User currentUser = (User) StpUtil.getSession().get(UserConstant.USER_LOGIN_STATE);
        if (currentUser == null) {
            return new ArrayList<>();
        }
        // 调用角色服务获取用户所有角色（包括角色继承关系）
        return roleService.getUserRoleKeys(currentUser.getId());
    }
}
