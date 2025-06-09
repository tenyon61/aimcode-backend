package com.tenyon.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tenyon.web.domain.dto.menu.MenuQueryDTO;
import com.tenyon.web.domain.entity.SysMenu;

import java.util.List;

/**
 * 菜单服务接口
 *
 * @author tenyon
 * @date 2025-05-14
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 根据用户ID查询权限集合
     *
     * @param userId 用户ID
     * @return 用户权限标识集合
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 根据角色ID获取菜单权限
     *
     * @param roleId 角色ID
     * @param status 菜单状态（0正常 1停用）
     * @return 权限列表
     */
    List<String> getRolePermissions(Long roleId, String status);

    /**
     * 获取查询条件
     *
     * @param menuQueryDTO 菜单查询请求
     * @return
     */
    QueryWrapper<SysMenu> getQueryWrapper(MenuQueryDTO menuQueryDTO);
}
