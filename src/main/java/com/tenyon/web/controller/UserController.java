package com.tenyon.web.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tenyon.common.base.constant.AimConstant;
import com.tenyon.common.base.constant.UserConstant;
import com.tenyon.common.base.exception.BusinessException;
import com.tenyon.common.base.exception.ErrorCode;
import com.tenyon.common.base.exception.ThrowUtils;
import com.tenyon.common.base.response.RtnData;
import com.tenyon.web.domain.dto.user.*;
import com.tenyon.web.domain.entity.SysUser;
import com.tenyon.web.domain.vo.user.LoginUserVO;
import com.tenyon.web.domain.vo.user.UserVO;
import com.tenyon.web.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 用户接口
 *
 * @author tenyon
 * @date 2025/1/6
 */
@Tag(name = "UserController", description = "用户管理接口")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private SysUserService sysUserService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public RtnData<LoginUserVO> login(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        if (userLoginDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginDTO.getUserAccount();
        String userPassword = userLoginDTO.getUserPassword();
        LoginUserVO loginUserVO = sysUserService.login(userAccount, userPassword);
        return RtnData.success(loginUserVO);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public RtnData<Long> register(@RequestBody @Valid UserRegisterDTO userRegisterDTO) {
        if (userRegisterDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterDTO.getUserAccount();
        String userPassword = userRegisterDTO.getUserPassword();
        String checkPassword = userRegisterDTO.getCheckPassword();
        long userId = sysUserService.register(userAccount, userPassword, checkPassword);
        return RtnData.success(userId);
    }

    @Operation(summary = "用户注销")
    @PostMapping("/logout")
    public RtnData<Boolean> logout() {
        boolean res = sysUserService.logout();
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(res);
    }

    @Operation(summary = "获取当前登录用户")
    @GetMapping("/getLoginUser")
    public RtnData<LoginUserVO> getLoginUser() {
        SysUser loginSysUser = sysUserService.getLoginUser();
        return RtnData.success(sysUserService.getLoginUserVO(loginSysUser));
    }

    // region 增删改查

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "创建用户")
    @PostMapping("/add")
    public RtnData<Long> addUser(@RequestBody UserAddDTO userAddDTO) {
        if (userAddDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userAddDTO, sysUser);
        // 默认密码 11111
        String defaultPassword = "11111";
        String encryptPassword = DigestUtils.md5DigestAsHex((AimConstant.ENCRYPT_SALT + defaultPassword).getBytes());
        sysUser.setUserPassword(encryptPassword);

        boolean res = sysUserService.save(sysUser);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(sysUser.getId());
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "删除用户")
    @DeleteMapping("/delete/{id}")
    public RtnData<Boolean> deleteUser(@PathVariable long id) {
        boolean res = sysUserService.removeById(id);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(true);
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "更新用户")
    @PostMapping("/update")
    public RtnData<Boolean> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        if (userUpdateDTO == null || userUpdateDTO.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userUpdateDTO, sysUser);
        boolean res = sysUserService.updateById(sysUser);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(true);
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "根据 id 获取用户（仅管理员）")
    @GetMapping("/get/{id}")
    public RtnData<SysUser> getUserById(@PathVariable long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysUser sysUser = sysUserService.getById(id);
        ThrowUtils.throwIf(sysUser == null, ErrorCode.NOT_FOUND_ERROR);
        return RtnData.success(sysUser);
    }

    @Operation(summary = "根据 id 获取包装类")
    @GetMapping("/getUserVO")
    public RtnData<UserVO> getUserVOById(long id) {
        RtnData<SysUser> response = getUserById(id);
        SysUser sysUser = response.getData();
        return RtnData.success(sysUserService.getUserVO(sysUser));
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "分页获取用户列表（仅管理员）")
    @PostMapping("/getUserPage")
    public RtnData<Page<SysUser>> getUserPage(@RequestBody UserQueryDTO userQueryDTO) {
        long current = userQueryDTO.getCurrent();
        long size = userQueryDTO.getPageSize();
        Page<SysUser> userPage = sysUserService.page(new Page<>(current, size),
                sysUserService.getQueryWrapper(userQueryDTO));
        return RtnData.success(userPage);
    }

    @Operation(summary = "分页获取用户封装列表")
    @PostMapping("/getUserVOPage")
    public RtnData<Page<UserVO>> getUserVOPage(@RequestBody UserQueryDTO userQueryDTO) {
        if (userQueryDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryDTO.getCurrent();
        long size = userQueryDTO.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<SysUser> userPage = sysUserService.page(new Page<>(current, size),
                sysUserService.getQueryWrapper(userQueryDTO));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = sysUserService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return RtnData.success(userVOPage);
    }

    // endregion

    @SaCheckLogin
    @Operation(summary = "更新个人信息")
    @PostMapping("/updateMy")
    public RtnData<Boolean> updateMyUser(@RequestBody UserUpdateMyDTO userUpdateMyDTO) {
        if (userUpdateMyDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        SysUser loginSysUser = sysUserService.getLoginUser();
        SysUser sysUser = new SysUser();
        BeanUtils.copyProperties(userUpdateMyDTO, sysUser);
        sysUser.setId(loginSysUser.getId());
        boolean res = sysUserService.updateById(sysUser);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(true);
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "重置密码")
    @PutMapping("/resetPwd/{id}")
    public RtnData<Boolean> resetPwd(@PathVariable long id) {
        SysUser sysUser = sysUserService.getById(id);
        sysUser.setUserPassword(DigestUtils.md5DigestAsHex((AimConstant.ENCRYPT_SALT + "11111").getBytes()));
        boolean res = sysUserService.updateById(sysUser);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "密码重置失败");
        return RtnData.success(true);
    }
}
