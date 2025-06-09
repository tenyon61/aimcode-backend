package com.tenyon.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tenyon.web.domain.dto.user.UserQueryDTO;
import com.tenyon.web.domain.entity.SysUser;
import com.tenyon.web.domain.vo.user.LoginUserVO;
import com.tenyon.web.domain.vo.user.UserVO;

import java.util.List;

/**
 * 用户服务
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @return 脱敏后的用户信息
     */
    LoginUserVO login(String userAccount, String userPassword);

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    Long register(String userAccount, String userPassword, String checkPassword);


    /**
     * 用户注册
     *
     * @param sysUser 用户实体
     * @return 新用户 id
     */
    Long register(SysUser sysUser);

    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户
     */
    SysUser getLoginUser();

    /**
     * 用户注销
     *
     * @return Boolean
     */
    Boolean logout();

    /**
     * 获取脱敏的已登录用户信息
     *
     * @return LoginUserVO
     */
    LoginUserVO getLoginUserVO(SysUser sysUser);

    /**
     * 获取脱敏的用户信息
     *
     * @param sysUser 用户实体
     * @return UserVO
     */
    UserVO getUserVO(SysUser sysUser);

    /**
     * 获取脱敏的用户信息
     *
     * @param sysUserList 用户列表
     * @return List<UserVO>
     */
    List<UserVO> getUserVOList(List<SysUser> sysUserList);

    /**
     * 获取查询条件
     *
     * @param userQueryDTO 用户查询请求
     * @return sql 查询条件
     */
    QueryWrapper<SysUser> getQueryWrapper(UserQueryDTO userQueryDTO);

    /**
     * 根据openId获取用户
     *
     * @param mpOpenId 微信开放平台 id
     * @return User
     */
    SysUser getUserByMpOpenId(String mpOpenId);

    String login(Long uid);
}
