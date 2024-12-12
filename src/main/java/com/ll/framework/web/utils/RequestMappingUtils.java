package com.ll.framework.web.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.ll.framework.web.annotations.RequestMapping;

public class RequestMappingUtils {

    public static boolean hasRequestMappingAnnotation(Method method) {
        return Arrays.stream(method.getAnnotations())
                .anyMatch(annotation -> isRequestMappingRelated(annotation.annotationType()));
    }

    public static boolean isRequestMappingRelated(Class<? extends Annotation> annotationType) {
        return annotationType == RequestMapping.class
                || Arrays.stream(annotationType.getAnnotations())
                        .anyMatch(meta -> meta.annotationType() == RequestMapping.class);
    }

    public static String extractFullPath(Method method) {
        String classPath = extractPathFromClass(method.getDeclaringClass());
        String methodPath = extractPathFromMethod(method);
        return combinePaths(classPath, methodPath);
    }

    public static String extractHttpMethod(Method method) {
        return Arrays.stream(method.getAnnotations())
                .filter(annotation -> isRequestMappingRelated(annotation.annotationType()))
                .findFirst()
                .map(RequestMappingUtils::getHttpMethodFromAnnotation)
                .orElse("GET");
    }

    private static String extractPathFromClass(Class<?> clazz) {
        RequestMapping annotation = clazz.getAnnotation(RequestMapping.class);
        return annotation != null ? annotation.value() : "";
    }

    private static String extractPathFromMethod(Method method) {
        return Arrays.stream(method.getAnnotations())
                .filter(annotation -> isRequestMappingRelated(annotation.annotationType()))
                .findFirst()
                .map(RequestMappingUtils::extractPathFromAnnotation)
                .orElseThrow(() -> new IllegalStateException("No @RequestMapping related annotation found"));
    }

    private static String getHttpMethodFromAnnotation(Annotation annotation) {
        try {
            RequestMapping requestMapping = annotation.annotationType().getAnnotation(RequestMapping.class);
            return requestMapping != null ? requestMapping.method() : "GET";
        } catch (Exception e) {
            return "GET";
        }
    }

    public static String extractPathFromAnnotation(Annotation annotation) {
        try {
            return (String) annotation.annotationType()
                    .getMethod("value")
                    .invoke(annotation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract path from annotation", e);
        }
    }

    public static String combinePaths(String... paths) {
        String combined = String.join("/", paths).replaceAll("//+", "/");
        combined = combined.startsWith("/") ? combined : "/" + combined;

        if (combined.length() > 1 && combined.endsWith("/")) {
            combined = combined.substring(0, combined.length() - 1);
        }

        return combined;
    }
}
