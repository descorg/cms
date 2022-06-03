package org.springcms.demo.service.impl;

import org.springcms.core.mybatis.base.CmsXBaseServiceImpl;
import org.springcms.demo.entity.City;
import org.springcms.demo.service.CityService;
import org.springcms.demo.mapper.CityMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【cms_city(行政区划表)】的数据库操作Service实现
* @createDate 2022-06-03 06:40:25
*/
@Service
public class CityServiceImpl extends CmsXBaseServiceImpl<CityMapper, City, Long> implements CityService{

}




