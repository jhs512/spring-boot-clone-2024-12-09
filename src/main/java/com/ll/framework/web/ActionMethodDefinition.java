package com.ll.framework.web;

import com.ll.framework.ioc.BeanDefinition;
import com.ll.framework.web.annotations.RequestMapping;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

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
        if (requestMapping != null) {
            return requestMapping.value().isEmpty() ? requestMapping.value() : requestMapping.value();
        }
        return ""; // 클래스 레벨에 RequestMapping이 없으면 기본 경로는 빈 문자열
    }

    private String extractPathFromMethod(Method method) {
        for (Annotation annotation : method.getAnnotations()) {
            if (isRequestMappingRelated(annotation.annotationType())) {
                try {
                    // 메서드의 경로 추출
                    return (String) annotation.annotationType()
                            .getMethod("value") // "path" 속성 호출
                            .invoke(annotation);
                } catch (NoSuchMethodException e) {
                    // path 속성이 없으면 value 속성 호출
                    try {
                        return (String) annotation.annotationType()
                                .getMethod("value")
                                .invoke(annotation);
                    } catch (Exception ex) {
                        throw new RuntimeException("value 속성을 가져오는 데 실패했습니다.", ex);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("path 속성을 가져오는 데 실패했습니다.", e);
                }
            }
        }
        throw new IllegalStateException("해당 메서드에 @RequestMapping 계열 애노테이션이 없습니다.");
    }

    private boolean isRequestMappingRelated(Class<? extends Annotation> annotationClass) {
        // 직접 @RequestMapping인지 확인
        if (annotationClass == RequestMapping.class) {
            return true;
        }
        // 메타애노테이션으로 @RequestMapping이 있는지 확인
        return annotationClass.isAnnotationPresent(RequestMapping.class);
    }

    private String combinePaths(String classPath, String methodPath) {
        // 클래스와 메서드 경로를 "/"로 결합, 중복된 "/"는 제거
        String fullPath = (classPath + "/" + methodPath).replaceAll("//+", "/");
        fullPath = fullPath.startsWith("/") ? fullPath : "/" + fullPath;

        if (fullPath.endsWith("/"))
            fullPath = fullPath.substring(0, fullPath.length() - 1);

        return fullPath;
    }

    public String getControllerBeanName() {
        return BeanDefinition.getBeanName(method.getDeclaringClass());
    }
}
