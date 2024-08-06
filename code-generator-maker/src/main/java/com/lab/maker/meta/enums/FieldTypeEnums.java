package com.lab.maker.meta.enums;

/**
 * modelConfig 中的 model 字段类型枚举
 */
public enum FieldTypeEnums {
    STRING("String"), BOOLEAN("boolean");

    private String type;

    FieldTypeEnums(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
