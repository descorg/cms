package %s.controller;

import io.swagger.annotations.Api;
import org.springcms.core.mybatis.base.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import %s.entity.%s;

@RestController
@RequestMapping("/%s")
@Api(value = "%s", tags = "%s")
public class %sController extends BaseController<%s> {

}
