package org.yxw.io;

public final class PropertyExpr {
    private final String key;
    private final String defaultValue;

    public PropertyExpr(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() { return key; }
    public String getDefaultValue() { return defaultValue; }

}
