package com.ll.framework.ioc;

import com.ll.framework.ioc.annotations.Bean;
import com.ll.framework.ioc.annotations.Component;
import com.ll.framework.ioc.annotations.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationContext {

    private final String basePackage;
    private Reflections reflections;
    private Map<String, BeanDefinition> beanDefinitions;
    private Map<String, Object> beans;

    public ApplicationContext(String basePackage) {
        this.basePackage = basePackage;
    }

    public void init() {
        reflections = new Reflections(
                basePackage,
                Scanners.TypesAnnotated,
                Scanners.MethodsAnnotated,
                Scanners.SubTypes
        );
        beanDefinitions = collectBeanDefinitions();
        beans = new HashMap<>();
    }

    private Map<String, BeanDefinition> collectBeanDefinitions() {
        Map<String, BeanDefinition> beanDefinitions = new HashMap<>();

        findComponentClasses()
                .forEach(cls -> {
                    String beanName = BeanDefinition.getBeanName(cls);
                    beanDefinitions.put(beanName, new BeanDefinition(cls));
                });

        findBeanMethods()
                .forEach((factoryMethod) -> {
                    String beanName = BeanDefinition.getBeanName(factoryMethod);
                    beanDefinitions.put(beanName, new BeanDefinition(factoryMethod));
                });

        return beanDefinitions;
    }

    public Set<Class<?>> findComponentClassesBy(Class<? extends Annotation> annotation) {
        return reflections
                .getTypesAnnotatedWith(annotation)
                .stream()
                .filter(cls -> !cls.isInterface())
                .collect(Collectors.toSet());
    }

    Set<Class<?>> findComponentClasses() {
        return findComponentClassesBy(Component.class);
    }

    Set<Method> findBeanMethods() {
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

        if (beanDefinition == null) {
            return Optional.empty();
        }

        return Optional.of(beanDefinition);
    }

    public <T> T genBean(String beanName) {
        if (beans.containsKey(beanName)) {
            return (T) beans.get(beanName);
        }

        Optional<BeanDefinition> opBeanDefinition = findBeanDefinition(beanName);

        BeanDefinition beanDefinition = opBeanDefinition.orElseThrow(() -> new IllegalArgumentException("Bean not found: " + beanName));

        T bean = createBean(beanDefinition);

        beans.put(beanName, bean);

        return bean;
    }

    private <T> T createBean(BeanDefinition beanDefinition) {
        Object[] dependencyBeans = beanDefinition
                .getDependencyBeanNames()
                .stream()
                .map(this::genBean)
                .toArray();

        if (beanDefinition.hasCustomFactoryMethod()) {
            return createBeanByFactoryMethod(beanDefinition, dependencyBeans);
        } else {
            return createBeanByConstructor(beanDefinition, dependencyBeans);
        }
    }

    private <T> T createBeanByConstructor(BeanDefinition beanDefinition, Object[] dependencyBeans) {
        try {
            T bean = (T) beanDefinition.getCls().getConstructors()[0].newInstance(dependencyBeans);

            return bean;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T createBeanByFactoryMethod(BeanDefinition beanDefinition, Object[] dependencyBeans) {
        Method method = beanDefinition.getFactoryMethod();

        try {
            Object bean = method.invoke(
                    genBean(beanDefinition.getConfigurationBeanName()),
                    dependencyBeans
            );

            return (T) bean;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
