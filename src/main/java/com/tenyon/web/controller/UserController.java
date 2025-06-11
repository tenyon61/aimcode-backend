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
import com.tenyon.web.domain.entity.User;
import com.tenyon.web.domain.vo.user.LoginUserVO;
import com.tenyon.web.domain.vo.user.UserVO;
import com.tenyon.web.service.UserService;
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
    private UserService userService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public RtnData<LoginUserVO> login(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        if (userLoginDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginDTO.getUserAccount();
        String userPassword = userLoginDTO.getUserPassword();
        LoginUserVO loginUserVO = userService.login(userAccount, userPassword);
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
        long userId = userService.register(userAccount, userPassword, checkPassword);
        return RtnData.success(userId);
    }

    @Operation(summary = "用户注销")
    @PostMapping("/logout")
    public RtnData<Boolean> logout() {
        boolean res = userService.logout();
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(res);
    }

    @Operation(summary = "获取当前登录用户")
    @GetMapping("/getLoginUser")
    public RtnData<LoginUserVO> getLoginUser() {
        User loginUser = userService.getLoginUser();
        return RtnData.success(userService.getLoginUserVO(loginUser));
    }

    // region 增删改查

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "创建用户")
    @PostMapping("/add")
    public RtnData<Long> addUser(@RequestBody UserAddDTO userAddDTO) {
        if (userAddDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddDTO, user);
        // 默认密码 11111
        String defaultPassword = "11111";
        String encryptPassword = DigestUtils.md5DigestAsHex((AimConstant.ENCRYPT_SALT + defaultPassword).getBytes());
        user.setUserPassword(encryptPassword);

        boolean res = userService.save(user);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(user.getId());
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "删除用户")
    @DeleteMapping("/delete/{id}")
    public RtnData<Boolean> deleteUser(@PathVariable long id) {
        boolean res = userService.removeById(id);
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
        User user = new User();
        BeanUtils.copyProperties(userUpdateDTO, user);
        boolean res = userService.updateById(user);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(true);
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "根据 id 获取用户（仅管理员）")
    @GetMapping("/get/{id}")
    public RtnData<User> getUserById(@PathVariable long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return RtnData.success(user);
    }

    @Operation(summary = "根据 id 获取包装类")
    @GetMapping("/getUserVO")
    public RtnData<UserVO> getUserVOById(long id) {
        RtnData<User> response = getUserById(id);
        User user = response.getData();
        return RtnData.success(userService.getUserVO(user));
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "分页获取用户列表（仅管理员）")
    @PostMapping("/getUserPage")
    public RtnData<Page<User>> getUserPage(@RequestBody UserQueryDTO userQueryDTO) {
        long current = userQueryDTO.getCurrent();
        long size = userQueryDTO.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryDTO));
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
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryDTO));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVOList(userPage.getRecords());
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
        User loginUser = userService.getLoginUser();
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyDTO, user);
        user.setId(loginUser.getId());
        boolean res = userService.updateById(user);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);
        return RtnData.success(true);
    }

    @SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
    @Operation(summary = "重置密码")
    @PutMapping("/resetPwd/{id}")
    public RtnData<Boolean> resetPwd(@PathVariable long id) {
        User user = userService.getById(id);
        user.setUserPassword(DigestUtils.md5DigestAsHex((AimConstant.ENCRYPT_SALT + "11111").getBytes()));
        boolean res = userService.updateById(user);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR, "密码重置失败");
        return RtnData.success(true);
    }
}
