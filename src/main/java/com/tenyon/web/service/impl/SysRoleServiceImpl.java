package com.tenyon.web.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tenyon.common.base.exception.BusinessException;
import com.tenyon.common.base.exception.ErrorCode;
import com.tenyon.common.base.exception.ThrowUtils;
import com.tenyon.common.base.utils.SqlUtils;
import com.tenyon.web.domain.dto.role.RoleQueryDTO;
import com.tenyon.web.domain.entity.SysRole;
import com.tenyon.web.domain.entity.SysUserRole;
import com.tenyon.web.mapper.SysRoleMapper;
import com.tenyon.web.mapper.SysUserRoleMapper;
import com.tenyon.web.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tenyon
 * @description 针对表【role(角色信息表)】的数据库操作Service实现
 * @createDate 2025-05-14 11:15:08
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
        implements SysRoleService {

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    public List<String> getUserRoleKeys(Long userId) {
        // 获取用户直接拥有的角色ID
        List<Long> userRoleIds = getUserRoleIds(userId);
        if (userRoleIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 根据角色ID获取角色信息
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysRole::getId, userRoleIds)
                .eq(SysRole::getStatus, "0");// 只查询正常状态的角色

        List<SysRole> sysRoles = baseMapper.selectList(queryWrapper);

        // 获取这些角色的所有父级角色（继承关系）
        Set<String> roleKeySet = new HashSet<>();

        // 添加用户直接拥有的角色标识
        for (SysRole sysRole : sysRoles) {
            roleKeySet.add(sysRole.getRoleKey());
        }

        return new ArrayList<>(roleKeySet);
    }

    @Override
    public QueryWrapper<SysRole> getQueryWrapper(RoleQueryDTO roleQueryDTO) {
        ThrowUtils.throwIf(roleQueryDTO == null, new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空"));

        String roleName = roleQueryDTO.getRoleName();
        String sortField = roleQueryDTO.getSortField();
        String sortOrder = roleQueryDTO.getSortOrder();

        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(roleName), "roleName", roleName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    /**
     * 获取用户直接拥有的角色ID列表
     *
     * @param userId 用户ID
     * @return 角色ID集合
     */
    private List<Long> getUserRoleIds(Long userId) {
        LambdaQueryWrapper<SysUserRole> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectList(queryWrapper);
        return sysUserRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
    }

}




