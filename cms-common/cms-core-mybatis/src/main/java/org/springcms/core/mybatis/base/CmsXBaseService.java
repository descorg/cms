package org.springcms.core.mybatis.base;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 * @param <M> mapper
 * @param <T> entity
 * @param <K> 自增字段类型
 */
public interface CmsXBaseService<M, T, K> extends IService<T> {

    /**
     * 删除多条记录
     * @param ids
     * @return
     */
    Boolean deleteByIds(List ids);

}
