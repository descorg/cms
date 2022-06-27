package org.springcms.core.mybatis.base;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springcms.core.mybatis.response.R;
import org.springcms.core.mybatis.utils.ApplicationContextUtils;
import org.springcms.core.mybatis.vo.Query;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class BaseController<T extends CmsXBaseEntity> {

//    @Lazy
//    @Autowired
//    protected CmsXBaseService entityService;

    @PostMapping("")
    @ApiOperation(value = "新增")
    public R<Boolean> insert(@RequestBody T entity) throws Exception {
        CmsXBaseService entityService = getEntityService();
        entity.setId(null);
        entity.setCreateTime(new Date());
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
        entity.setCreateTime(new Date());
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

    @GetMapping("")
    @ApiOperation(value = "分页查询")
    public R<IPage<T>> page(T entity, Query query) throws Exception {
        if (query.getCurrent() == null || query.getCurrent() < 1) {
            query.setCurrent(1);
        }
        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(10);
        }
        CmsXBaseService entityService = getEntityService();
        QueryWrapper queryWrapper = getWrapper(entity);
        IPage<T> pages = entityService.page(new Page<>(query.getCurrent(), query.getSize()), queryWrapper);
        return R.data(pages);
    }

    @GetMapping("/template")
    @ApiOperation(value = "模版")
    public void template(HttpServletResponse response) throws Exception {
        Class clazz = getEntityType();
        Constructor constructor = clazz.getConstructor();

        List<Map<String, Object>> mapList = new ArrayList<>();
//        Map<String, Object> map = beanToMap(constructor.newInstance());
        Map<String, Object> map = getFields(constructor.newInstance());
        mapList.add(map);

        ExcelWriter writer = ExcelUtil.getWriter();
        writer.write(mapList);

        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", String.format("attachment;filename=%s.xls", clazz.getSimpleName().toLowerCase()));
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out);
        writer.close();
    }

    @PostMapping("/import")
    @ApiOperation(value = "导入")
    public R<Boolean> imports(@RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (!filename.endsWith(".xls") && !filename.endsWith(".xlsx")) {
            return R.fail("只能上传Excel文件");
        }

        try {
            CmsXBaseService entityService = getEntityService();

            ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
            List<T> lists = reader.readAll(getEntityType());
            reader.close();

            for (T entity : lists) {
                T tmp = (T)entityService.getOne(getWrapper(entity));
                if (tmp != null) {
                    entityService.update(entity, getWrapper(entity));
                } else {
                    entityService.save(entity);
                }
            }
            return R.data(true, "ok");
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/export")
    @ApiOperation(value = "导出")
    public void export(HttpServletResponse response, T entity, Query query,
                       @ApiParam(name = "format", value = "文件格式：xls、csv、txt", example = "xls", defaultValue = "xls") String format) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        CmsXBaseService entityService = getEntityService();
        QueryWrapper queryWrapper = getWrapper(entity);
        IPage<T> pages = entityService.page(new Page<>(query.getCurrent(), query.getSize()), queryWrapper);

        ExcelWriter writer = ExcelUtil.getWriter();
        writer.write(pages.getRecords());

        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", String.format("attachment;filename=%s.xls", sdf.format(new Date())));
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out);
        writer.close();
    }

    /**
     * 获取service
     * @return
     */
    protected CmsXBaseService getEntityService() throws Exception {
        CmsXBaseService entityService = (CmsXBaseService) ApplicationContextUtils.getBean(String.format("%sService", getEntityType().getSimpleName()));
        return entityService;
    }

    protected CmsXBaseWrapper getWrapper(T entity) throws Exception {
        String packName = entity.getClass().getPackage().getName();
        String clsName = String.format("%swrapper.%sWrapper", packName.substring(0, packName.length() - "entity".length()), entity.getClass().getSimpleName());
        try {
            Class<?> cclass = Class.forName(clsName);
            Constructor constructor = cclass.getDeclaredConstructor(entity.getClass());
            return (CmsXBaseWrapper) constructor.newInstance(entity);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
            return new CmsXBaseWrapper(entity);
        }
    }

    protected Class<T> getEntityType() {
        Class<T> tClass = (Class<T>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return tClass;
    }

    /**
     * 获取实体类中所有属性，包含swagger注释
     * @param bean
     * @return
     * @param <T>
     */
    protected <T> Map<String, Object> beanToMap(T bean) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>(16);
        if (bean != null) {
            //使用反射获取对象的类属性
            Field[] declaredFields = bean.getClass().getDeclaredFields();
            BeanMap beanMap = BeanMap.create(bean);
            for (Field field : declaredFields) {
                //设置成true才能获取注解名
                field.setAccessible(true);
                //获取类上注解信息
                ApiModelProperty annotation = field.getAnnotation(ApiModelProperty.class);
                if (annotation != null) {
                    //获取注解绑定的value值
                    String value = annotation.value();
                    map.put(value, field.getName());
                }
            }
        }
        return map;
    }

    /**
     * 获取实体类中所有属性
     * @param bean
     * @return
     */
    protected <T> Map<String, Object> getFields(T bean) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>(16);

        //使用反射获取对象的类属性
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            map.put(field.getName(), null);
        }

        return map;
    }

}
