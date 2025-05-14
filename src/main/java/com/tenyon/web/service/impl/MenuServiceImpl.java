package com.tenyon.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tenyon.web.domain.entity.Menu;
import com.tenyon.web.service.MenuService;
import com.tenyon.web.mapper.MenuMapper;
import org.springframework.stereotype.Service;

/**
* @author tenyon
* @description 针对表【menu(菜单权限表)】的数据库操作Service实现
* @createDate 2025-05-14 11:15:07
*/
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu>
    implements MenuService{

}




