package com.ll.framework.web;

import com.ll.framework.ioc.BeanDefinition;
import com.ll.framework.web.annotations.RequestMapping;
import com.ll.framework.web.utils.RequestMappingUtils;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.ll.framework.web.utils.RequestMappingUtils.combinePaths;

public class ActionMethodDefinition {
    @Getter
    private final Method method;
    @Getter
    private final String pathFormat;

    public int getPathFormatLength() {
        return pathFormat.length();
    }

    public ActionMethodDefinition(Method method) {
        this.method = method;
        this.pathFormat = extractFullPath(method);
    }

    private String extractFullPath(Method method) {
        // 1. 클래스 레벨의 RequestMapping 경로 추출
        String classPath = extractPathFromClass(method.getDeclaringClass());

        // 2. 메서드 레벨의 RequestMapping 경로 추출
        String methodPath = extractPathFromMethod(method);

        // 3. 경로를 결합
        return combinePaths(classPath, methodPath);
    }

    private String extractPathFromClass(Class<?> clazz) {
        RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
        return requestMapping != null ? requestMapping.value() : "";
    }

    private String extractPathFromMethod(Method method) {
        for (Annotation annotation : method.getAnnotations()) {
            if (RequestMappingUtils.isRequestMappingRelated(annotation.annotationType())) {
                return RequestMappingUtils.extractPathFromAnnotation(annotation);
            }
        }
        throw new IllegalStateException("No @RequestMapping related annotation found on method");
    }

    public String getControllerBeanName() {
        return BeanDefinition.getBeanName(method.getDeclaringClass());
    }
}
