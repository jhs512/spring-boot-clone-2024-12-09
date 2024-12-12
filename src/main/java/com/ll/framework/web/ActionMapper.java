package com.ll.framework.web;

import com.ll.framework.ioc.ApplicationContext;
import com.ll.framework.web.annotations.Controller;
import com.ll.framework.web.annotations.RequestMapping;
import com.ll.framework.web.handler.HandlerExecutor;
import com.ll.framework.web.resolver.PathVariableHandlerMethodArgumentResolver;
import com.ll.framework.web.utils.PathPatternParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class ActionMapper {
    private final ApplicationContext applicationContext;
    private final HandlerExecutor handlerExecutor;
    private Map<String, ActionMethodDefinition> actionMethodDefinitions;

    public ActionMapper(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.handlerExecutor = new HandlerExecutor(List.of(
            new PathVariableHandlerMethodArgumentResolver()
        ));
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
        return actionMethodDefinitions
                .values()
                .stream()
                .filter(actionMethodDefinition -> 
                    PathPatternParser.matches(actionMethodDefinition.getPathFormat(), path)
                )
                .findFirst();
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
        // 메타 애노테이션으로 @RequestMapping이 있는지 확인
        return Arrays.stream(annotationClass.getAnnotations())
                .anyMatch(metaAnnotation -> metaAnnotation.annotationType() == RequestMapping.class);
    }

    public void doAction(ActionMethodDefinition actionMethodDefinition, HttpRequest req, HttpResponse resp) {
        String controllerBeanName = actionMethodDefinition.getControllerBeanName();
        Object controller = applicationContext.genBean(controllerBeanName);
        Method method = actionMethodDefinition.getMethod();

        Map<String, String> pathVariables = 
            PathPatternParser.extractPathVariables(actionMethodDefinition.getPathFormat(), req.getRequestURI());

        req.getPathParams().putAll(pathVariables);

        try {
            Object result = handlerExecutor.execute(method, controller, req, resp);
            
            if (result != null) {
                resp.setBody(result.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T genBean(String beanName) {
        return applicationContext.genBean(beanName);
    }
}