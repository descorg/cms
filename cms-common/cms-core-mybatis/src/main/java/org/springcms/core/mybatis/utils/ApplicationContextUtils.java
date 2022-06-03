package org.springcms.core.mybatis.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextUtils implements ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        logger.info("applicationContext正在初始化", applicationContext);
        ApplicationContextUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getSpringContext() {
        return applicationContext;
    }

    public static Object getBean(String className) throws BeansException, IllegalArgumentException {
        if(className == null || className.length() <= 0) {
            throw new IllegalArgumentException("Class name is empty");
        }

        String beanName = null;
        if(className.length() > 1) {
            beanName = className.substring(0, 1).toLowerCase() + className.substring(1);
        } else {
            beanName = className.toLowerCase();
        }
        return applicationContext != null ? applicationContext.getBean(beanName) : null;
    }
}
