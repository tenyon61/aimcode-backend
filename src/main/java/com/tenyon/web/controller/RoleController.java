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
import com.tenyon.web.domain.entity.Role;
import com.tenyon.web.domain.entity.RoleMenu;
import com.tenyon.web.service.RoleMenuService;
import com.tenyon.web.service.RoleService;
import com.tenyon.web.service.UserRoleService;
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
    private RoleService roleService;

    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private UserRoleService userRoleService;

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "创建角色")
    @PostMapping("/add")
    public RtnData<Long> addRole(@RequestBody Role role) {
        if (role == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = roleService.save(role);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(role.getId());
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "更新角色")
    @PutMapping("/update")
    public RtnData<Boolean> updateRole(@RequestBody Role role) {
        if (role == null || role.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = roleService.updateById(role);
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
        boolean res = roleService.removeById(id);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(true);
    }

    //    @SaCheckPermission(value = {"system:role:list"}, mode = SaMode.OR)
    @Operation(summary = "获取角色列表")
    @PostMapping("/getRolePage")
    public RtnData<Page<Role>> getRolePage(@RequestBody RoleQueryDTO roleQueryDTO) {
        long current = roleQueryDTO.getCurrent();
        long size = roleQueryDTO.getPageSize();
        Page<Role> rolePage = roleService.page(new Page<>(current, size), roleService.getQueryWrapper(roleQueryDTO));
        return RtnData.success(rolePage);
    }

    @SaCheckPermission(value = {"system:role:query"}, mode = SaMode.OR)
    @Operation(summary = "获取角色详情")
    @GetMapping("get/{id}")
    public RtnData<Role> getRoleById(@PathVariable long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Role role = roleService.getById(id);
        ThrowUtils.throwIf(role == null, ErrorCode.NOT_FOUND_ERROR);
        return RtnData.success(role);
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "分配角色菜单权限")
    @PostMapping("/assignMenu")
    public RtnData<Boolean> assignMenuToRole(@RequestParam Long roleId, @RequestBody List<Long> menuIds) {
        if (roleId == null || roleId <= 0 || menuIds == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 先删除原有权限
        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleMenu::getRoleId, roleId);
        roleMenuService.remove(queryWrapper);

        // 添加新权限
        if (!menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                RoleMenu roleMenu = new RoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                roleMenuService.save(roleMenu);
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

        LambdaQueryWrapper<RoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RoleMenu::getRoleId, roleId);
        List<RoleMenu> roleMenus = roleMenuService.list(queryWrapper);

        List<Long> menuIds = roleMenus.stream().map(RoleMenu::getMenuId).collect(java.util.stream.Collectors.toList());

        return RtnData.success(menuIds);
    }

    @SaCheckLogin
    @Operation(summary = "获取当前用户角色标识")
    @GetMapping("/current")
    public RtnData<List<String>> getCurrentUserRoles() {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        List<String> roles = roleService.getUserRoleKeys(userId);
        return RtnData.success(roles);
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "分配用户角色")
    @PostMapping("/assign")
    public RtnData<Boolean> assignUserRoles(@RequestParam Long userId, @RequestBody List<Long> roleIds) {
        if (userId == null || userId <= 0 || roleIds == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        boolean res = userRoleService.assignUserRoles(userId, roleIds);
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
        List<Long> roleIds = userRoleService.getUserRoleIds(userId);
        return RtnData.success(roleIds);
    }
} 