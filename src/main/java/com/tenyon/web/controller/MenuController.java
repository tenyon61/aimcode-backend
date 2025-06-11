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
import com.tenyon.web.domain.entity.Menu;
import com.tenyon.web.service.MenuService;
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
    private MenuService menuService;

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "创建菜单")
    @PostMapping("/add")
    public RtnData<Long> addMenu(@RequestBody Menu menu) {
        if (menu == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = menuService.save(menu);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(menu.getId());
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "更新菜单")
    @PutMapping("/update")
    public RtnData<Boolean> updateMenu(@RequestBody Menu menu) {
        if (menu == null || menu.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = menuService.updateById(menu);
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
        boolean res = menuService.removeById(id);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(true);
    }

    //    @SaCheckPermission(value = {"system:menu:list"}, mode = SaMode.OR)
    @Operation(summary = "获取菜单列表")
    @PostMapping("/getMenuPage")
    public RtnData<Page<Menu>> getMenuPage(@RequestBody MenuQueryDTO menuQueryDTO) {
        long current = menuQueryDTO.getCurrent();
        long size = menuQueryDTO.getPageSize();
        Page<Menu> menuPage = menuService.page(new Page<>(current, size), menuService.getQueryWrapper(menuQueryDTO));
        return RtnData.success(menuPage);
    }

    //    @SaCheckPermission(value = {"system:menu:query"}, mode = SaMode.OR)
    @Operation(summary = "获取菜单详情")
    @GetMapping("get/{id}")
    public RtnData<Menu> getMenuById(@PathVariable long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Menu menu = menuService.getById(id);
        ThrowUtils.throwIf(menu == null, ErrorCode.NOT_FOUND_ERROR);
        return RtnData.success(menu);
    }

    @SaCheckLogin
    @Operation(summary = "获取当前用户权限标识")
    @GetMapping("/getPermissions")
    public RtnData<List<String>> getCurrentUserPermissions() {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        List<String> permissions = menuService.getUserPermissions(userId);
        return RtnData.success(permissions);
    }
}