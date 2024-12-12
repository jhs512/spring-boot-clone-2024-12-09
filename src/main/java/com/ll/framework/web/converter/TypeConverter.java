package com.ll.framework.web.converter;

public class TypeConverter {

    public static Object convert(String value, Class<?> type) {
        if (value == null) {
            return null;
        }

        try {
            if (type == String.class) {
                return value;
            } else if (type == int.class || type == Integer.class) {
                return Integer.parseInt(value);
            } else if (type == long.class || type == Long.class) {
                return Long.parseLong(value);
            } else if (type == double.class || type == Double.class) {
                return Double.parseDouble(value);
            } else if (type == boolean.class || type == Boolean.class) {
                return Boolean.parseBoolean(value);
            }

            throw new IllegalArgumentException("지원하지 않는 타입입니다: " + type.getName());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("값 '%s'을(를) %s(으)로 변환할 수 없습니다.", value, type.getName()),
                    e
            );
        }
    }
}
