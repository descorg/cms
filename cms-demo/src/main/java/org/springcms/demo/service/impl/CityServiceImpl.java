package org.springcms.demo.service.impl;

import org.springcms.core.mybatis.base.CmsXBaseServiceImpl;
import org.springcms.demo.entity.City;
import org.springcms.demo.service.CityService;
import org.springcms.demo.mapper.CityMapper;
import org.springcms.demo.vo.TreeCityVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Administrator
* @description 针对表【cms_city(行政区划表)】的数据库操作Service实现
* @createDate 2022-06-03 06:40:25
*/
@Service
public class CityServiceImpl extends CmsXBaseServiceImpl<CityMapper, City, Long> implements CityService{

    @Resource
    CityMapper cityMapper;

    @Override
    public List<TreeCityVO> tree(Long pid) {
        List<TreeCityVO> cityTreeVOList = cityMapper.queryChilds(pid);
        for (TreeCityVO treeVO : cityTreeVOList) {
            if (treeVO != null && treeVO.getChildrens() > 0) {
                treeVO.setChildren(tree(treeVO.getId()));
            }
        }
        return cityTreeVOList;
    }
}
