package com.ll.framework.web.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PathPatternParser {

    public static boolean matches(String pattern, String path) {
        if (pattern.equals(path)) {
            return true;
        }

        String regex = pattern
                .replaceAll("\\{[^/]+\\}", "[^/]+")
                .replaceAll("/", "\\\\/");

        Pattern compiledPattern = Pattern.compile("^" + regex + "$");
        return compiledPattern.matcher(path).matches();
    }

    public static Map<String, String> extractPathVariables(String pattern, String path) {
        Map<String, String> pathVariables = new HashMap<>();

        String[] patternParts = pattern.split("/");
        String[] pathParts = path.split("/");

        if (patternParts.length != pathParts.length) {
            return pathVariables;
        }

        for (int i = 0; i < patternParts.length; i++) {
            String patternPart = patternParts[i];
            if (patternPart.startsWith("{") && patternPart.endsWith("}")) {
                String variableName = patternPart.substring(1, patternPart.length() - 1);
                pathVariables.put(variableName, pathParts[i]);
            }
        }

        return pathVariables;
    }
}
