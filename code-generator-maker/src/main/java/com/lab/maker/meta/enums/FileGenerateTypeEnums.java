package com.lab.maker.meta.enums;

public enum FileGenerateTypeEnums {
    STATIC("static", "静态生成"), DYNAMIC("dynamic", "动态生成");

    private String value;

    private String text;

    FileGenerateTypeEnums(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
