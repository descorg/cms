package org.springcms.core.mybatis.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public class CmsXBaseWrapper<T extends CmsXBaseEntity> extends QueryWrapper<T> {

	public CmsXBaseWrapper() {
		super();
	}

	public CmsXBaseWrapper(T entity) {
		super();

		//自动匹配进行模糊查询
//		Map<String, Object> map = BeanUtils.beanToMap(entity);
//		for(Map.Entry<String, Object> item : map.entrySet()){
//			if (item.getValue() == null) {
//				continue;
//			}
//			if (item.getValue() instanceof Integer) {
//				if ((Integer)item.getValue() == 0) {
//					continue;
//				} else {
//					super.eq(item.getKey(), item.getValue());
//				}
//			}
//			if (item.getValue() instanceof Long) {
//				if ((Long)item.getValue() == 0L) {
//					continue;
//				} else {
//					super.eq(item.getKey(), item.getValue());
//				}
//			}
//		}

		if (entity.getStartTime() != null && !entity.getStartTime().isEmpty()) {
			super.gt("create_time", entity.getStartTime());
		}
		if (entity.getEndTime() != null && !entity.getEndTime().isEmpty()) {
			super.lt("create_time", entity.getEndTime());
		}
	}
}
