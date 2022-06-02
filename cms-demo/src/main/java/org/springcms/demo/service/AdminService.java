package org.springcms.demo.service;

import org.springcms.core.mybatis.base.CmsXBaseService;
import org.springcms.demo.entity.Admin;
import org.springcms.demo.mapper.AdminMapper;

/**
* @author Administrator
* @description 针对表【cms_admin(系统账号)】的数据库操作Service
* @createDate 2022-06-01 21:32:47
*/
public interface AdminService extends CmsXBaseService<AdminMapper, Admin, Integer> {

}
