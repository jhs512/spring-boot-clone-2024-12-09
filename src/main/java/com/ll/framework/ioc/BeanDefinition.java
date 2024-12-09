package com.ll.framework.ioc;

import lombok.Getter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class BeanDefinition {
    @Getter
    private final Class<?> cls;
    @Getter
    private Method factoryMethod;

    public static String getBeanName(Class<?> cls) {
        String beanName = cls.getSimpleName();
        beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);

        return beanName;
    }

    public static String getBeanName(Method factoryMethod) {
        return factoryMethod.getName();
    }

    public BeanDefinition(Class<?> cls) {
        this.cls = cls;
    }

    public BeanDefinition(Method factoryMethod) {
        this.cls = factoryMethod.getReturnType();
        this.factoryMethod = factoryMethod;
    }

    public List<String> getDependencyBeans() {
        return Arrays
                .stream(
                        hasCustomFactoryMethod() ? factoryMethod.getParameters() : cls.getConstructors()[0].getParameters()
                )
                .map(parameter -> parameter.getName())
                .toList();
    }

    public boolean hasCustomFactoryMethod() {
        return factoryMethod != null;
    }

    public String getConfigurationBeanName() {
        return hasCustomFactoryMethod()
                ? getBeanName(factoryMethod.getDeclaringClass())
                : null;
    }
}
