package com.tenyon.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tenyon.web.domain.entity.SysUserRole;

import java.util.List;

/**
 * 用户角色服务接口
 *
 * @author tenyon
 * @date 2025-05-14
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    /**
     * 给用户分配角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID集合
     * @return 操作结果
     */
    boolean assignUserRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID集合
     */
    List<Long> getUserRoleIds(Long userId);

}
