package com.tenyon.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tenyon.common.base.constant.UserConstant;
import com.tenyon.common.base.exception.BusinessException;
import com.tenyon.common.base.exception.ErrorCode;
import com.tenyon.common.base.exception.ThrowUtils;
import com.tenyon.common.base.response.RtnData;
import com.tenyon.web.domain.dto.role.RoleQueryDTO;
import com.tenyon.web.domain.entity.SysRole;
import com.tenyon.web.domain.entity.SysRoleMenu;
import com.tenyon.web.service.SysRoleMenuService;
import com.tenyon.web.service.SysRoleService;
import com.tenyon.web.service.SysUserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理接口
 *
 * @author tenyon
 * @date 2025-05-14
 */
@Tag(name = "RoleController", description = "角色管理接口")
@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "创建角色")
    @PostMapping("/add")
    public RtnData<Long> addRole(@RequestBody SysRole sysRole) {
        if (sysRole == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = sysRoleService.save(sysRole);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(sysRole.getId());
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "更新角色")
    @PutMapping("/update")
    public RtnData<Boolean> updateRole(@RequestBody SysRole sysRole) {
        if (sysRole == null || sysRole.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = sysRoleService.updateById(sysRole);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(true);
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "删除角色")
    @DeleteMapping("/delete/{id}")
    public RtnData<Boolean> deleteRole(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = sysRoleService.removeById(id);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(true);
    }

    //    @SaCheckPermission(value = {"system:role:list"}, mode = SaMode.OR)
    @Operation(summary = "获取角色列表")
    @PostMapping("/getRolePage")
    public RtnData<Page<SysRole>> getRolePage(@RequestBody RoleQueryDTO roleQueryDTO) {
        long current = roleQueryDTO.getCurrent();
        long size = roleQueryDTO.getPageSize();
        Page<SysRole> rolePage = sysRoleService.page(new Page<>(current, size), sysRoleService.getQueryWrapper(roleQueryDTO));
        return RtnData.success(rolePage);
    }

    @SaCheckPermission(value = {"system:role:query"}, mode = SaMode.OR)
    @Operation(summary = "获取角色详情")
    @GetMapping("get/{id}")
    public RtnData<SysRole> getRoleById(@PathVariable long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysRole sysRole = sysRoleService.getById(id);
        ThrowUtils.throwIf(sysRole == null, ErrorCode.NOT_FOUND_ERROR);
        return RtnData.success(sysRole);
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "分配角色菜单权限")
    @PostMapping("/assignMenu")
    public RtnData<Boolean> assignMenuToRole(@RequestParam Long roleId, @RequestBody List<Long> menuIds) {
        if (roleId == null || roleId <= 0 || menuIds == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 先删除原有权限
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        sysRoleMenuService.remove(queryWrapper);

        // 添加新权限
        if (!menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setRoleId(roleId);
                sysRoleMenu.setMenuId(menuId);
                sysRoleMenuService.save(sysRoleMenu);
            }
        }

        return RtnData.success(true);
    }

    @SaCheckPermission(value = {"system:role:query"}, mode = SaMode.OR)
    @Operation(summary = "获取角色拥有的菜单ID列表")
    @GetMapping("/menus/{roleId}")
    public RtnData<List<Long>> getRoleMenuIds(@PathVariable Long roleId) {
        if (roleId == null || roleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuService.list(queryWrapper);

        List<Long> menuIds = sysRoleMenus.stream().map(SysRoleMenu::getMenuId).collect(java.util.stream.Collectors.toList());

        return RtnData.success(menuIds);
    }

    @SaCheckLogin
    @Operation(summary = "获取当前用户角色标识")
    @GetMapping("/current")
    public RtnData<List<String>> getCurrentUserRoles() {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        List<String> roles = sysRoleService.getUserRoleKeys(userId);
        return RtnData.success(roles);
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "分配用户角色")
    @PostMapping("/assign")
    public RtnData<Boolean> assignUserRoles(@RequestParam Long userId, @RequestBody List<Long> roleIds) {
        if (userId == null || userId <= 0 || roleIds == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean res = sysUserRoleService.assignUserRoles(userId, roleIds);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);

        return RtnData.success(true);
    }

    @SaCheckPermission(value = {"system:user:query"}, mode = SaMode.OR)
    @Operation(summary = "获取用户拥有的角色ID列表")
    @GetMapping("/user/{userId}")
    public RtnData<List<Long>> getUserRoleIds(@PathVariable Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Long> roleIds = sysUserRoleService.getUserRoleIds(userId);
        return RtnData.success(roleIds);
    }
} 