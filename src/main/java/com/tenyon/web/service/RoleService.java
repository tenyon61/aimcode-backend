package com.tenyon.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tenyon.web.domain.dto.role.RoleQueryDTO;
import com.tenyon.web.domain.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author tenyon
 * @date 2025-05-14
 */
public interface RoleService extends IService<Role> {

    /**
     * 获取用户的所有角色标识
     *
     * @param userId 用户ID
     * @return 角色标识集合
     */
    List<String> getUserRoleKeys(Long userId);

    /**
     * 获取查询条件
     *
     * @param roleQueryDTO 角色查询请求
     * @return sql 查询条件
     */
    QueryWrapper<Role> getQueryWrapper(RoleQueryDTO roleQueryDTO);
}
