package com.ll.framework.ioc;

public class BeanDefinition {
    private final Class<?> cls;

    public BeanDefinition(Class<?> cls) {
        this.cls = cls;
    }
}
