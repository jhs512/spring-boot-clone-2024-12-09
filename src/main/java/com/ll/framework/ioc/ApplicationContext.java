package com.ll.framework.ioc;

import com.ll.framework.ioc.annotations.Bean;
import com.ll.framework.ioc.annotations.Component;
import com.ll.framework.ioc.annotations.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationContext {
    private final String basePackage;
    private Reflections reflections;
    private Map<String, BeanDefinition> beanDefinitions;

    public ApplicationContext(String basePackage) {
        this.basePackage = basePackage;
    }

    public void init() {
        reflections = new Reflections(basePackage, Scanners.TypesAnnotated);
        beanDefinitions = collectBeanDefinitions();
    }

    private Map<String, BeanDefinition> collectBeanDefinitions() {
        Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

        findComponentClasses()
                .forEach(cls -> {
                    String beanName = getBeanName(cls);
                    beanDefinitions.put(beanName, new BeanDefinition(cls));
                });

        findBeanMethods()
                .forEach((factoryMethod) -> {
                    String beanName = getBeanName(factoryMethod);
                    beanDefinitions.put(beanName, new BeanDefinition(factoryMethod));
                });

        return beanDefinitions;
    }

    private String getBeanName(Class<?> cls) {
        String beanName = cls.getSimpleName();
        beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);

        return beanName;
    }

    private String getBeanName(Method factoryMethod) {
        return factoryMethod.getName();
    }

    <T> Class<T> findComponentClassBy(Class<T> cls) {
        return findComponentClasses()
                .stream()
                .filter(cls::isAssignableFrom)
                .map(c -> (Class<T>) c)
                .findFirst()
                .orElse(null);
    }

    private Set<Class<?>> findComponentClasses() {
        return reflections
                .getTypesAnnotatedWith(Component.class)
                .stream()
                .filter(cls -> !cls.isInterface())
                .collect(Collectors.toSet());
    }

    private Set<Method> findBeanMethods() {
        return reflections
                .getTypesAnnotatedWith(Configuration.class)
                .stream()
                .filter(cls -> !cls.isInterface())
                .flatMap(cls -> Arrays.stream(cls.getDeclaredMethods()))
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .collect(Collectors.toSet());
    }

    public Optional<BeanDefinition> findBeanDefinition(String beanName) {
        BeanDefinition beanDefinition = beanDefinitions.get(beanName);

        if (beanDefinition == null) return Optional.empty();

        return Optional.of(beanDefinition);
    }
}
