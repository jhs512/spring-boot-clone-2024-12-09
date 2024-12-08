package com.ll.framework.ioc;

import com.ll.framework.ioc.annotations.Component;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.Set;
import java.util.stream.Collectors;

public class ApplicationContext {
    private final String basePackage;
    private Reflections reflections;

    public ApplicationContext(String basePackage) {
        this.basePackage = basePackage;
        System.out.println("ApplicationContext: " + basePackage);
    }

    public void init() {
        reflections = new Reflections(basePackage, Scanners.TypesAnnotated);
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
}
