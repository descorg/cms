package %s.service.impl;

import %s.entity.%s;
import %s.mapper.%sMapper;
import %s.service.%sService;
import org.mybatis.spring.annotation.MapperScan;
import org.springcms.core.mybatis.base.CmsXBaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
@MapperScan(basePackages = "%s.mapper")
public class %sServiceImpl extends CmsXBaseServiceImpl<%sMapper, %s, Integer> implements %sService {

}
