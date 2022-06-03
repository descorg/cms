package org.springcms.core.mybatis.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springcms.core.mybatis.response.R;
import org.springcms.core.mybatis.utils.ApplicationContextUtils;
import org.springcms.core.mybatis.vo.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

@Component
public class BaseController<T extends CmsXBaseEntity> {

//    @Lazy
//    @Autowired
//    protected CmsXBaseService entityService;

    @PostMapping("")
    @ApiOperation(value = "新增")
    public R<Boolean> insert(@RequestBody T entity) throws Exception {
        CmsXBaseService entityService = getEntityService();
        if (entityService.save(entity)) {
            return R.success("success");
        } else {
            return R.fail("Insert failed");
        }
    }

    @DeleteMapping("")
    @ApiOperation(value = "删除")
    public R<Boolean> delete(@RequestBody List ids) throws Exception {
        CmsXBaseService entityService = getEntityService();
        if (entityService.deleteByIds(ids)) {
            return R.success("success");
        } else {
            return R.fail("delete failed");
        }
    }

    @PutMapping("")
    @ApiOperation(value = "更新")
    public R<Boolean> update(@RequestBody T entity) throws Exception {
        CmsXBaseService entityService = getEntityService();
        if (entityService.updateById(entity)) {
            return R.success("success");
        } else {
            return R.fail("update failed");
        }
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "详情")
    public R<T> detail(@PathVariable Serializable id) throws Exception {
        CmsXBaseService entityService = getEntityService();
        Object entity = entityService.getById(id);
        if (entity != null) {
            return R.data((T)entity, "success");
        } else {
            return R.fail("update failed");
        }
    }

    @GetMapping("/")
    @ApiOperation(value = "分页查询")
    public R<IPage<T>> page(T entity, Query query) throws Exception {
        CmsXBaseService entityService = getEntityService();
        IPage<T> pages = entityService.page(new Page<>(query.getCurrent(), query.getSize()), new QueryWrapper(entity));
        return R.data(pages);
    }

    /**
     * 获取service
     * @return
     */
    protected CmsXBaseService getEntityService() throws Exception {
        Class<T> tClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        CmsXBaseService entityService = (CmsXBaseService)ApplicationContextUtils.getBean(String.format("%sService", tClass.getSimpleName()));
        return entityService;
    }

}
