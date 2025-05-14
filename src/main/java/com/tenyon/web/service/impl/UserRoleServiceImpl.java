package com.tenyon.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tenyon.web.domain.entity.UserRole;
import com.tenyon.web.service.UserRoleService;
import com.tenyon.web.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

/**
* @author tenyon
* @description 针对表【user_role(用户和角色关联表)】的数据库操作Service实现
* @createDate 2025-05-14 11:15:08
*/
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole>
    implements UserRoleService{

}




