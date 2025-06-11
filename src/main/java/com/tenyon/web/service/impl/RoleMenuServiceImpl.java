package com.tenyon.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tenyon.web.domain.entity.RoleMenu;
import com.tenyon.web.mapper.RoleMenuMapper;
import com.tenyon.web.service.RoleMenuService;
import org.springframework.stereotype.Service;

/**
 * @author tenyon
 * @description 针对表【role_menu(角色和菜单关联表)】的数据库操作Service实现
 * @createDate 2025-05-14 11:15:08
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu>
        implements RoleMenuService {

}




