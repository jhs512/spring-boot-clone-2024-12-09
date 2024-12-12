package com.ll.framework.web.resolver;

import java.lang.reflect.Parameter;

import com.ll.framework.web.HttpRequest;
import com.ll.framework.web.HttpResponse;

public interface HandlerMethodArgumentResolver {

    boolean supportsParameter(Parameter parameter);

    Object resolveArgument(Parameter parameter, HttpRequest request, HttpResponse response);
}
