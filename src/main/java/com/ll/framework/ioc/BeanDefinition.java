package com.ll.framework.ioc;

import java.lang.reflect.Method;

public class BeanDefinition {
    private final Class<?> cls;
    private Method factoryMethod;

    public BeanDefinition(Class<?> cls) {
        this.cls = cls;
    }

    public BeanDefinition(Method factoryMethod) {
        this.cls = factoryMethod.getReturnType();
        this.factoryMethod = factoryMethod;
    }
}
