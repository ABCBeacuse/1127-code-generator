package com.lab.maker.meta.enums;

/**
 * fileConfig 中的 type 类型
 */
public enum FileTypeEnum {
    DIR("目录", "dir"), FILE("文件", "file"), GROUP("目录", "group");

    private String text;
    private String value;

    FileTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
