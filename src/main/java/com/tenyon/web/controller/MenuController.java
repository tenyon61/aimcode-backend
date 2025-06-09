package com.tenyon.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tenyon.common.base.constant.UserConstant;
import com.tenyon.common.base.exception.BusinessException;
import com.tenyon.common.base.exception.ErrorCode;
import com.tenyon.common.base.exception.ThrowUtils;
import com.tenyon.common.base.response.RtnData;
import com.tenyon.web.domain.dto.menu.MenuQueryDTO;
import com.tenyon.web.domain.entity.SysMenu;
import com.tenyon.web.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理接口
 *
 * @author tenyon
 * @date 2025-05-14
 */
@Tag(name = "MenuController", description = "菜单管理接口")
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private SysMenuService sysMenuService;

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "创建菜单")
    @PostMapping("/add")
    public RtnData<Long> addMenu(@RequestBody SysMenu sysMenu) {
        if (sysMenu == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = sysMenuService.save(sysMenu);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(sysMenu.getId());
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "更新菜单")
    @PutMapping("/update")
    public RtnData<Boolean> updateMenu(@RequestBody SysMenu sysMenu) {
        if (sysMenu == null || sysMenu.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = sysMenuService.updateById(sysMenu);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(true);
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "删除菜单")
    @DeleteMapping("/delete/{id}")
    public RtnData<Boolean> deleteMenu(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = sysMenuService.removeById(id);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(true);
    }

    //    @SaCheckPermission(value = {"system:menu:list"}, mode = SaMode.OR)
    @Operation(summary = "获取菜单列表")
    @PostMapping("/getMenuPage")
    public RtnData<Page<SysMenu>> getMenuPage(@RequestBody MenuQueryDTO menuQueryDTO) {
        long current = menuQueryDTO.getCurrent();
        long size = menuQueryDTO.getPageSize();
        Page<SysMenu> menuPage = sysMenuService.page(new Page<>(current, size), sysMenuService.getQueryWrapper(menuQueryDTO));
        return RtnData.success(menuPage);
    }

    //    @SaCheckPermission(value = {"system:menu:query"}, mode = SaMode.OR)
    @Operation(summary = "获取菜单详情")
    @GetMapping("get/{id}")
    public RtnData<SysMenu> getMenuById(@PathVariable long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysMenu sysMenu = sysMenuService.getById(id);
        ThrowUtils.throwIf(sysMenu == null, ErrorCode.NOT_FOUND_ERROR);
        return RtnData.success(sysMenu);
    }

    @SaCheckLogin
    @Operation(summary = "获取当前用户权限标识")
    @GetMapping("/getPermissions")
    public RtnData<List<String>> getCurrentUserPermissions() {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        List<String> permissions = sysMenuService.getUserPermissions(userId);
        return RtnData.success(permissions);
    }
}