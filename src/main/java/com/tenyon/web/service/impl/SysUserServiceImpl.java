package com.tenyon.web.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tenyon.common.base.constant.AimConstant;
import com.tenyon.common.base.constant.UserConstant;
import com.tenyon.common.base.exception.BusinessException;
import com.tenyon.common.base.exception.ErrorCode;
import com.tenyon.common.base.exception.ThrowUtils;
import com.tenyon.common.base.utils.SqlUtils;
import com.tenyon.web.domain.dto.user.UserQueryDTO;
import com.tenyon.web.domain.entity.SysUser;
import com.tenyon.web.domain.enums.UserRoleEnum;
import com.tenyon.web.domain.vo.user.LoginUserVO;
import com.tenyon.web.domain.vo.user.UserVO;
import com.tenyon.web.mapper.SysUserMapper;
import com.tenyon.web.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {


    @Override
    public Long register(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 1.账户不能重复
            LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(SysUser::getUserAccount, userAccount);
            long count = this.count(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2.加密
            String encryptPassword = DigestUtils.md5DigestAsHex((AimConstant.ENCRYPT_SALT + userPassword).getBytes());
            // 3.插入数据
            SysUser sysUser = new SysUser();
            sysUser.setUserAccount(userAccount);
            sysUser.setUserPassword(encryptPassword);
            sysUser.setUserRole(UserRoleEnum.USER.getValue());
            boolean res = this.save(sysUser);
            ThrowUtils.throwIf(!res, ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            return sysUser.getId();
        }
    }

    @Transactional
    @Override
    public Long register(SysUser sysUser) {
        sysUser.setUserRole("user");
        boolean res = this.save(sysUser);
        ThrowUtils.throwIf(!res, ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        return sysUser.getId();
    }

    @Override
    public LoginUserVO login(String userAccount, String userPassword) {
        // 1. 校验
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((AimConstant.ENCRYPT_SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        SysUser sysUser = this.getOne(queryWrapper);
        // 用户不存在
        if (sysUser == null) {
            log.info("user login failed, account cannot match password");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        StpUtil.login(sysUser.getId());
        StpUtil.getSession().set(UserConstant.USER_LOGIN_STATE, sysUser);
        return this.getLoginUserVO(sysUser);
    }

    /**
     * 获取当前登录用户
     *
     * @return
     */
    @Override
    public SysUser getLoginUser() {
        // 先判断是否已登录
        Object loginId = StpUtil.getLoginIdDefaultNull();
        ThrowUtils.throwIf(loginId == null, ErrorCode.NOT_LOGIN_ERROR);
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
//        long userId = currentUser.getId();
//        currentUser = userService.getById(userId);
//        if (currentUser == null) {
//            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
//        }
        return (SysUser) StpUtil.getSession().get(UserConstant.USER_LOGIN_STATE);
    }

    @Override
    public Boolean logout() {
        StpUtil.checkLogin();
        // 移除登录态
        StpUtil.logout();
        return true;
    }

    @Override
    public LoginUserVO getLoginUserVO(SysUser sysUser) {
        if (sysUser == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(sysUser, loginUserVO);
        // 组装token
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        loginUserVO.setToken(tokenInfo.tokenValue);
        return loginUserVO;
    }

    @Override
    public UserVO getUserVO(SysUser sysUser) {
        if (sysUser == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(sysUser, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> getUserVOList(List<SysUser> sysUserList) {
        if (CollectionUtils.isEmpty(sysUserList)) {
            return new ArrayList<>();
        }
        return sysUserList.stream().map(this::getUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<SysUser> getQueryWrapper(UserQueryDTO userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

    @Override
    public SysUser getUserByMpOpenId(String mpOpenId) {
        return lambdaQuery().eq(SysUser::getMpOpenId, mpOpenId).one();
    }

    @Override
    public String login(Long uid) {
        SysUser sysUser = this.getById(uid);
        // 用户不存在
        if (sysUser == null) {
            log.info("user login failed, account cannot match password");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        StpUtil.login(sysUser.getId());
        StpUtil.getSession().set(UserConstant.USER_LOGIN_STATE, sysUser);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        return tokenInfo.tokenValue;
    }
}
