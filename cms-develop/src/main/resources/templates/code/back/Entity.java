package %s.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springcms.core.mybatis.base.CmsXBaseEntity;

@Data
@TableName("%s%s")
@ApiModel(value = "%s", description = "%s")
public class %s extends CmsXBaseEntity<Integer> {

%s
}
