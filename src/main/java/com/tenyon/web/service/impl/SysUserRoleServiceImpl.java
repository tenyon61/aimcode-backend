package com.tenyon.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tenyon.web.domain.entity.SysUserRole;
import com.tenyon.web.mapper.SysUserRoleMapper;
import com.tenyon.web.service.SysUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tenyon
 * @description 针对表【user_role(用户和角色关联表)】的数据库操作Service实现
 * @createDate 2025-05-14 11:15:08
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole>
        implements SysUserRoleService {

    /**
     * 给用户分配角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID集合
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignUserRoles(Long userId, List<Long> roleIds) {
        // 先移除用户的所有角色
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        this.remove(queryWrapper);

        // 如果roleIds为空，则只是清空用户角色，不分配新角色
        if (roleIds == null || roleIds.isEmpty()) {
            return true;
        }

        // 批量插入用户角色关系
        List<SysUserRole> sysUserRoles = new ArrayList<>();
        for (Long roleId : roleIds) {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(userId);
            sysUserRole.setRoleId(roleId);
            sysUserRoles.add(sysUserRole);
        }

        return this.saveBatch(sysUserRoles);
    }

    /**
     * 获取用户角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID集合
     */
    @Override
    public List<Long> getUserRoleIds(Long userId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> sysUserRoles = this.list(queryWrapper);

        return sysUserRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
    }
}




