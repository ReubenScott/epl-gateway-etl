package com.covidien.etl.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @ClassName: SpringUtil
 * @Description:
 */
public final class SpringUtil {
    /**
     * SpringUtil.
     */
    private static SpringUtil springUtil = new SpringUtil();
    /**
     * ApplicationContext.
     */
    private ApplicationContext ctx;
    /**
     * @Title: SpringUtil
     * @Description:
     */
    private SpringUtil() {
        ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    }
    /**
     * @Title: getInstance
     * @Description:
     * @return SpringUtil
     */
    public static SpringUtil getInstance() {
        return springUtil;
    }
    /**
     * @Title: getBean
     * @Description:
     * @param beanName
     * beanName
     * @param type
     * type
     * @param <T>
     * T
     * @return <T> T
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(final String beanName, final Class<T> type) {
        return (T) ctx.getBean(beanName);
    }

}
