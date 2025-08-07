package org.yxw.io;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.util.*;
import java.util.function.Function;

public class PropertyResolver {
    Logger logger = LoggerFactory.getLogger(getClass());

    Map<String, String> properties = new HashMap<>();
    Map<Class<?>, Function<String, Object>> converters = new HashMap<>();

    public PropertyResolver(Properties props) {
        this.properties.putAll(System.getenv());
        Set<String> names = props.stringPropertyNames();
        for (String name : names) {
            this.properties.put(name, props.getProperty(name));
        }
        if (logger.isDebugEnabled()) {
            List<String> keys = new ArrayList<>(this.properties.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                logger.debug("PropertyResolver: {}={}", key, this.properties.get(key));
            }
        }

        // String类型:
        converters.put(String.class, s -> s);
        // boolean类型:
        converters.put(boolean.class, Boolean::parseBoolean);
        converters.put(Boolean.class, Boolean::valueOf);
        // int类型:
        converters.put(int.class, Integer::parseInt);
        converters.put(Integer.class, Integer::valueOf);
        // 其他基本类型...
        // Date/Time类型:
        converters.put(LocalDate.class, LocalDate::parse);
        converters.put(LocalTime.class, LocalTime::parse);
        converters.put(LocalDateTime.class, LocalDateTime::parse);
        converters.put(ZonedDateTime.class, ZonedDateTime::parse);
        converters.put(Duration.class, Duration::parse);
        converters.put(ZoneId.class, ZoneId::of);
    }

    public boolean containsProperty(String key) {
        return this.properties.containsKey(key);
    }

    public void registerConverter(Class<?> targetType, Function<String, Object> converter) {
        this.converters.put(targetType, converter);
    }

    @Nullable
    public String getProperty(String key) {
        // 解析${abc.xyz:defaultValue}
        PropertyExpr expr = parsePropertyExpr(key);
        if (expr != null) {
            if (expr.getDefaultValue() != null) {
                return getProperty(expr.getKey(), expr.getDefaultValue());
            } else {
                return getRequiredProperty(expr.getKey());
            }
        }
        String value = this.properties.get(key);
        if (value != null) {
            return parseValue(value);
        }
        return value;
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value == null ? parseValue(defaultValue) : value;
    }

    public <T> T getProperty(String key, Class<T> targetType) {
        String value = getProperty(key);
        if (value == null) {
            return null;
        }
        return convert(value, targetType);
    }

    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return convert(value, targetType);
    }

    public String getRequiredProperty(String key) {
        String value = getProperty(key);
        return Objects.requireNonNull(value, "Property '" + key + "' not found.");
    }

    public <T> T getRequiredProperty(String key, Class<T> targetType) {
        T value = getProperty(key, targetType);
        return Objects.requireNonNull(value, "Property '" + key + "' not found.");
    }

    public String parseValue(String value) {
        PropertyExpr expr = parsePropertyExpr(value);
        if (expr == null) {
            return value;
        }
        if (expr.getDefaultValue() != null) {
            return getProperty(expr.getKey(), expr.getDefaultValue());
        } else {
            return getRequiredProperty(expr.getKey());
        }
    }

    /*
    * 解析${abc.xyz:defaultValue}类似的表达式
    * */
    public PropertyExpr parsePropertyExpr(String key) {
        if (key.startsWith("${") && key.endsWith("}")) {
            // 是否存在默认值
            int n = key.indexOf(":");
            if (n == (-1)) {
                // 没有默认值: ${key}
                String k = key.substring(2, key.length() - 1);
                return new PropertyExpr(k, null);
            }else {
                // 有defaultValue: ${key:default}
                String k = key.substring(2, n);
                return new PropertyExpr(k, key.substring(n + 1, key.length() - 1));
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    <T> T convert(String value, Class<T> targetType) {
        Function<String, Object> fn = this.converters.get(targetType);
        if (fn == null) {
            throw new IllegalArgumentException("Unsupported value type: " + targetType.getName());
        }
        return (T) fn.apply(value);
    }



}

