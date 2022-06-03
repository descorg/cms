package org.springcms.demo.service;

import org.springcms.core.mybatis.base.CmsXBaseService;
import org.springcms.demo.entity.City;
import org.springcms.demo.mapper.CityMapper;

/**
* @author Administrator
* @description 针对表【cms_city(行政区划表)】的数据库操作Service
* @createDate 2022-06-03 06:40:25
*/
public interface CityService extends CmsXBaseService<CityMapper, City, Long> {

}
