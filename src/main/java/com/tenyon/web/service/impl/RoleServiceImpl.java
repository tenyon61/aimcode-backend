package com.tenyon.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tenyon.web.domain.entity.Role;
import com.tenyon.web.service.RoleService;
import com.tenyon.web.mapper.RoleMapper;
import org.springframework.stereotype.Service;

/**
* @author tenyon
* @description 针对表【role(角色信息表)】的数据库操作Service实现
* @createDate 2025-05-14 11:15:08
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService{

}




