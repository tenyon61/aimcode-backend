package com.tenyon.web.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tenyon.common.base.constant.UserConstant;
import com.tenyon.common.base.exception.BusinessException;
import com.tenyon.common.base.exception.ErrorCode;
import com.tenyon.common.base.exception.ThrowUtils;
import com.tenyon.common.base.utils.SqlUtils;
import com.tenyon.web.domain.dto.menu.MenuQueryDTO;
import com.tenyon.web.domain.entity.SysMenu;
import com.tenyon.web.domain.entity.SysRole;
import com.tenyon.web.domain.entity.SysRoleMenu;
import com.tenyon.web.domain.entity.SysUserRole;
import com.tenyon.web.mapper.SysMenuMapper;
import com.tenyon.web.mapper.SysRoleMapper;
import com.tenyon.web.mapper.SysRoleMenuMapper;
import com.tenyon.web.mapper.SysUserRoleMapper;
import com.tenyon.web.service.SysMenuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tenyon
 * @description 针对表【menu(菜单权限表)】的数据库操作Service实现
 * @createDate 2025-05-14 11:15:07
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
        implements SysMenuService {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    /**
     * 根据用户ID查询权限集合
     *
     * @param userId 用户ID
     * @return 用户权限标识集合
     */
    @Override
    public List<String> getUserPermissions(Long userId) {
        // 超级管理员拥有所有权限
        if (isAdmin(userId)) {
            return getAllPermissions();
        }

        // 获取用户所有角色ID
        List<Long> roleIds = getUserRoleIds(userId);
        if (roleIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取所有角色的权限并合并
        Set<String> permissionSet = new HashSet<>();
        for (Long roleId : roleIds) {
            // 获取角色状态，确认角色是否可用
            SysRole sysRole = sysRoleMapper.selectById(roleId);
            if (sysRole != null && "0".equals(sysRole.getStatus()) && sysRole.getIsDelete() == 0) {
                // 获取角色的权限列表
                List<String> rolePermissions = getRolePermissions(roleId, "0");
                permissionSet.addAll(rolePermissions);
            }
        }

        return new ArrayList<>(permissionSet);
    }

    /**
     * 根据角色ID获取菜单权限
     *
     * @param roleId 角色ID
     * @param status 菜单状态（0正常 1停用）
     * @return 权限列表
     */
    @Override
    public List<String> getRolePermissions(Long roleId, String status) {
        // 查询角色关联的菜单ID
        LambdaQueryWrapper<SysRoleMenu> roleMenuQuery = new LambdaQueryWrapper<>();
        roleMenuQuery.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuMapper.selectList(roleMenuQuery);

        if (sysRoleMenus.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取菜单ID列表
        List<Long> menuIds = sysRoleMenus.stream()
                .map(SysRoleMenu::getMenuId)
                .collect(Collectors.toList());

        // 查询菜单信息，获取权限标识
        LambdaQueryWrapper<SysMenu> menuQuery = new LambdaQueryWrapper<>();
        menuQuery.in(SysMenu::getId, menuIds)
                .eq(StringUtils.isNotBlank(status), SysMenu::getStatus, status);

        List<SysMenu> sysMenus = baseMapper.selectList(menuQuery);
        return sysMenus.stream()
                .filter(sysMenu -> StringUtils.isNotBlank(sysMenu.getPerms()))
                .map(SysMenu::getPerms)
                .collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<SysMenu> getQueryWrapper(MenuQueryDTO menuQueryDTO) {
        ThrowUtils.throwIf(menuQueryDTO == null, new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空"));

        String menuName = menuQueryDTO.getMenuName();
        String perms = menuQueryDTO.getPerms();
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        String sortField = menuQueryDTO.getSortField();
        String sortOrder = menuQueryDTO.getSortOrder();
        queryWrapper.like(StrUtil.isNotBlank(menuName), "menuName", menuName);
        queryWrapper.like(StrUtil.isNotBlank(perms), "perms", perms);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 获取所有权限
     *
     * @return 所有权限标识列表
     */
    private List<String> getAllPermissions() {
        // 查询所有正常状态的菜单
        LambdaQueryWrapper<SysMenu> menuQuery = new LambdaQueryWrapper<>();
        menuQuery.eq(SysMenu::getStatus, "0");

        List<SysMenu> sysMenus = baseMapper.selectList(menuQuery);
        return sysMenus.stream()
                .filter(sysMenu -> StringUtils.isNotBlank(sysMenu.getPerms()))
                .map(SysMenu::getPerms)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    private List<Long> getUserRoleIds(Long userId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);

        List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectList(queryWrapper);
        return sysUserRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
    }

    /**
     * 判断是否为管理员
     *
     * @param userId 用户ID
     * @return 是否为管理员
     */
    private boolean isAdmin(Long userId) {
        // 获取用户角色
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectList(queryWrapper);

        if (sysUserRoles.isEmpty()) {
            return false;
        }

        // 查询用户角色信息
        List<Long> roleIds = sysUserRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<SysRole> roleQuery = new LambdaQueryWrapper<>();
        roleQuery.in(SysRole::getId, roleIds)
                .eq(SysRole::getStatus, "0")
                .eq(SysRole::getIsDelete, 0);

        List<SysRole> sysRoles = sysRoleMapper.selectList(roleQuery);
        // 检查是否有管理员角色
        for (SysRole sysRole : sysRoles) {
            if (UserConstant.ADMIN_ROLE_KEY.equals(sysRole.getRoleKey())) {
                return true;
            }
        }

        return false;
    }
}




