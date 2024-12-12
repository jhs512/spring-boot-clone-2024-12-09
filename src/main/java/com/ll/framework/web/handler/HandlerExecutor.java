package com.ll.framework.web.handler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ll.framework.web.HttpRequest;
import com.ll.framework.web.HttpResponse;
import com.ll.framework.web.resolver.HandlerMethodArgumentResolver;

public class HandlerExecutor {

    private final List<HandlerMethodArgumentResolver> argumentResolvers;

    public HandlerExecutor(List<HandlerMethodArgumentResolver> argumentResolvers) {
        this.argumentResolvers = argumentResolvers;
    }

    public Object execute(Method method, Object controller, HttpRequest req, HttpResponse resp) {
        Object[] args = resolveParameters(method, req, resp);
        
        try {
            return method.invoke(controller, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object[] resolveParameters(Method method, HttpRequest request, HttpResponse response) {
        return Arrays.stream(method.getParameters())
                .map(parameter -> resolveParameter(parameter, request, response))
                .toArray();
    }

    private Object resolveParameter(Parameter parameter, HttpRequest request, HttpResponse response) {
        return argumentResolvers.stream()
                .filter(resolver -> resolver.supportsParameter(parameter))
                .findFirst()
                .map(resolver -> resolver.resolveArgument(parameter, request, response))
                .orElse(null);
    }
}
