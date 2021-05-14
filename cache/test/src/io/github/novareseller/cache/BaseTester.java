package io.github.novareseller.cache;

import io.github.novareseller.cache.bean.TestBean;

/**
 * @author: Bowen huang
 * @date: 2021/05/13
 */
public class BaseTester {

    public void sleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TestBean getBean(Integer age) {
        TestBean bean = new TestBean();
        bean.setName("lovely.bowen");
        bean.setAge(age);
        return bean;
    }

}
