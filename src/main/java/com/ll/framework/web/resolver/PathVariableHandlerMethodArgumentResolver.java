package com.ll.framework.web.resolver;

import java.lang.reflect.Parameter;

import com.ll.framework.web.HttpRequest;
import com.ll.framework.web.HttpResponse;
import com.ll.framework.web.annotations.PathVariable;
import com.ll.framework.web.converter.TypeConverter;

public class PathVariableHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(Parameter parameter) {
        return parameter.isAnnotationPresent(PathVariable.class);
    }

    @Override
    public Object resolveArgument(Parameter parameter, HttpRequest request, HttpResponse response) {
        PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
        String name = pathVariable.value().isEmpty() ? parameter.getName() : pathVariable.value();
        String value = request.getPathParam(name);

        return TypeConverter.convert(value, parameter.getType());
    }
}
