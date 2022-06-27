package org.springcms.core.mybatis.base;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.validation.annotation.Validated;

import java.util.Date;
import java.util.List;

/**
 *
 * @param <M> mapper
 * @param <T> entity
 * @param <K> 自增字段类型
 */
@Validated
public class CmsXBaseServiceImpl<M extends CmsXBaseMapper<T, K>, T extends CmsXBaseEntity, K> extends ServiceImpl<M, T> implements CmsXBaseService<M, T, K> {

    @Override
    public boolean save(T entity) {
        resolveEntity(entity);
        return super.save(entity);
    }

    @Override
    public boolean updateById(T entity) {
        resolveEntity(entity);
        return super.updateById(entity);
    }

    @Override
    public Boolean deleteByIds(List ids) {
        return super.removeByIds(ids);
    }

    private void resolveEntity(T entity) {
        entity.setCreateTime(new Date());
    }
}
