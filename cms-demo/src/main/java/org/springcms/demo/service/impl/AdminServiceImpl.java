package org.springcms.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springcms.core.jwt.utils.JwtUtils;
import org.springcms.core.jwt.vo.CmsUser;
import org.springcms.core.utils.StringUtils;
import org.springcms.demo.entity.Admin;
import org.springcms.demo.service.AdminService;
import org.springcms.demo.mapper.AdminMapper;
import org.springcms.core.mybatis.base.CmsXBaseServiceImpl;
import org.springcms.demo.vo.LoginVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
* @author Administrator
* @description 针对表【cms_admin(系统账号)】的数据库操作Service实现
* @createDate 2022-06-01 21:32:47
*/
@Service
public class AdminServiceImpl extends CmsXBaseServiceImpl<AdminMapper, Admin, Integer> implements AdminService{

    @Resource
    AdminMapper adminMapper;

    @Override
    public LoginVO Login(String name, String pass) throws Exception {
        if (name == null || name.isEmpty()) {
            throw new Exception("User name cannot be empty");
        }
        if (pass == null || pass.isEmpty()) {
            throw new Exception("Login password cannot be empty");
        }

        LambdaQueryWrapper<Admin> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Admin::getName, name);
        if (!adminMapper.exists(lqw)) {
            throw new Exception("Account does not exist");
        }

        Admin admin = adminMapper.selectOne(lqw);
        if (admin.getLoginFail() >= Admin.AdminStatue.ADMIN_STATUE_LOCK.ordinal()) {
            if (admin.getStatus() != Admin.AdminStatue.ADMIN_STATUE_LOCK.ordinal()) {
                admin.setStatus(Admin.AdminStatue.ADMIN_STATUE_LOCK.ordinal());
                adminMapper.updateById(admin);
            }

            throw new Exception("Account locked");
        }

        if (!admin.getPass().equalsIgnoreCase(StringUtils.md5(pass))) {
            admin.setLoginFail(admin.getLoginFail() + 1);
            adminMapper.updateById(admin);

            throw new Exception("Login password error");
        }

        admin.setLoginNums(admin.getLoginNums() + 1);
        admin.setLastLoginTime(new Date());
        admin.setLoginFail(0);
        adminMapper.updateById(admin);

        CmsUser user = new CmsUser();
        BeanUtils.copyProperties(admin, user);

        String token = JwtUtils.createToken(admin.getId(), admin.getName());
        JwtUtils.addAccessToken(String.valueOf(admin.getId()), token, user);

        LoginVO loginVO = new LoginVO();
        loginVO.setId(admin.getId());
        loginVO.setName(admin.getName());
        loginVO.setToken(token);

        return loginVO;
    }
}




