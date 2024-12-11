package com.ll.framework.web;

import com.ll.framework.ioc.ApplicationContext;
import com.ll.framework.web.annotations.Controller;
import com.ll.framework.web.annotations.PathVariable;
import com.ll.framework.web.annotations.RequestMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class ActionMapper {
    private final ApplicationContext applicationContext;
    private Map<String, ActionMethodDefinition> actionMethodDefinitions;

    public ActionMapper(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void init() {
        this.actionMethodDefinitions = applicationContext.findComponentClassesBy(Controller.class)
                .stream()
                .flatMap(cls -> Arrays.stream(cls.getDeclaredMethods()))
                .filter(this::hasRequestMappingAnnotation) // 메타애노테이션 기반 필터링
                .map(ActionMethodDefinition::new)
                .sorted(Comparator.comparing(ActionMethodDefinition::getPathFormatLength).reversed())
                .collect(
                        LinkedHashMap::new,
                        (map, actionMethodDefinition) -> map.put(actionMethodDefinition.getPathFormat(), actionMethodDefinition),
                        Map::putAll
                );
    }

    public Optional<ActionMethodDefinition> findActionMethodDefinition(String pathFormat) {
        return Optional.ofNullable(actionMethodDefinitions.get(pathFormat));
    }

    public Optional<ActionMethodDefinition> findActionMethodDefinitionByActionPath(String path) {
        return this.actionMethodDefinitions
                .entrySet()
                .stream()
                .filter(entry -> matchPath(entry.getKey(), path))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    private boolean matchPath(String pathTemplate, String path) {
        String[] pathTemplateParts = pathTemplate.split("/");
        String[] pathParts = path.split("/");

        if (pathTemplateParts.length != pathParts.length) {
            return false;
        }

        for (int i = 0; i < pathTemplateParts.length; i++) {
            if (pathTemplateParts[i].startsWith("{") && pathTemplateParts[i].endsWith("}")) {
                continue;
            }

            if (!pathTemplateParts[i].equals(pathParts[i])) {
                return false;
            }
        }

        return true;
    }

    private boolean hasRequestMappingAnnotation(Method method) {
        // 메서드에 붙은 애노테이션 중 하나라도 @RequestMapping 메타애노테이션을 가지면 true
        return Arrays.stream(method.getAnnotations())
                .anyMatch(annotation -> isRequestMappingRelated(annotation.annotationType()));
    }

    private boolean isRequestMappingRelated(Class<? extends Annotation> annotationClass) {
        // 직접 @RequestMapping인지 확인
        if (annotationClass == RequestMapping.class) {
            return true;
        }
        // 메타애노테이션으로 @RequestMapping이 있는지 확인
        return Arrays.stream(annotationClass.getAnnotations())
                .anyMatch(metaAnnotation -> metaAnnotation.annotationType() == RequestMapping.class);
    }

    public <T> T doAction(ActionMethodDefinition actionMethodDefinition, String path) {
        String controllerBeanName = actionMethodDefinition.getControllerBeanName();

        T controller = applicationContext.genBean(controllerBeanName);
        Method method = actionMethodDefinition.getMethod();

        Object[] params = Arrays.stream(method.getParameters())
                .map(parameter -> {
                    // 만약에 @PathVariable이 붙어있는 파라미터라면
                    if (parameter.isAnnotationPresent(PathVariable.class)) {

                        String[] pathBits = path.split("/");
                        String[] pathFormatBits = actionMethodDefinition.getPathFormat().split("/");

                        Map<String, String> pathVariables = new HashMap<>();
                        for (int i = 0; i < pathBits.length; i++) {
                            if (pathFormatBits[i].startsWith("{") && pathFormatBits[i].endsWith("}")) {
                                pathVariables.put(pathFormatBits[i].substring(1, pathFormatBits[i].length() - 1), pathBits[i]);
                            }
                        }

                        String pathVariable = pathVariables.get(parameter.getName());

                        if (parameter.getType() == int.class) {
                            return Integer.parseInt(pathVariable);
                        }
                        else if (parameter.getType() == long.class) {
                            return Long.parseLong(pathVariable);
                        }
                        else {
                            return pathVariable;
                        }
                    }

                    return genBean(parameter.getType().getSimpleName());
                })
                .toArray();

        try {
            return (T) method.invoke(controller, params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T genBean(String beanName) {
        return applicationContext.genBean(beanName);
    }
}