package org.springcms.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springcms.demo.entity.Admin;
import org.springcms.demo.service.AdminService;
import org.springcms.demo.mapper.AdminMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【cms_admin(系统账号)】的数据库操作Service实现
* @createDate 2022-06-01 21:32:47
*/
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin>
    implements AdminService{

}




