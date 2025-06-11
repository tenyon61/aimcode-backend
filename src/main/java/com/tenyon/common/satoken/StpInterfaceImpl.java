package com.tenyon.common.satoken;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.tenyon.common.base.constant.UserConstant;
import com.tenyon.web.domain.entity.User;
import com.tenyon.web.service.MenuService;
import com.tenyon.web.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
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
        return null;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 获取当前登录用户
        User user = (User) StpUtil.getSessionByLoginId(loginId).get(UserConstant.USER_LOGIN_STATE);
        // 调用角色服务获取用户所有角色（包括角色继承关系）
        if (ObjectUtil.isNull(user)) {
            return Collections.emptyList();
        }
        return Collections.singletonList(user.getUserRole());
    }
}
