package com.ll.framework.ioc;

import com.ll.framework.ioc.annotations.Component;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
        beanDefinitions = findComponentClasses()
                .stream()
                .collect(HashMap::new, (map, cls) -> {
                    String beanName = getBeanName(cls);
                    map.put(beanName, new BeanDefinition(cls));
                }, HashMap::putAll);
    }

    private String getBeanName(Class<?> cls) {
        String beanName = cls.getSimpleName();
        beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);

        return beanName;
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

    public Optional<BeanDefinition> findBeanDefinition(String beanName) {
        BeanDefinition beanDefinition = beanDefinitions.get(beanName);

        if (beanDefinition == null) return Optional.empty();

        return Optional.of(beanDefinition);
    }
}
