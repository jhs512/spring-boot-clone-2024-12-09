package com.ll.framework.web;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HttpRequest {

    private String method;
    private String requestURI;
    private String protocol;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> queryParams = new HashMap<>();
    private Map<String, String> pathParams = new HashMap<>();
    private String body;

    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }

    public void setHeader(String name, String value) {
        headers.put(name.toLowerCase(), value);
    }

    public void parseQueryParams() {
        if (requestURI.contains("?")) {
            String[] parts = requestURI.split("\\?", 2);
            requestURI = parts[0];
            String[] params = parts[1].split("&");
            for (String param : params) {
                String[] keyValue = param.split("=", 2);
                if (keyValue.length == 2) {
                    queryParams.put(keyValue[0], keyValue[1]);
                }
            }
        }
    }

    public void setPathParam(String name, String value) {
        pathParams.put(name, value);
    }

    public String getPathParam(String name) {
        return pathParams.get(name);
    }
}
