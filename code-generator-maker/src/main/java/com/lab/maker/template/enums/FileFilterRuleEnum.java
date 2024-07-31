package com.lab.maker.template.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 过滤器 过滤方式
 */
@AllArgsConstructor
@Getter
public enum FileFilterRuleEnum {

    CONTAINS("包含", "contains"), STARTS_WITH("前缀匹配", "startsWith"),
    ENDS_WITH("后缀匹配", "endsWith"), REGEX("正则", "regex"), EQUALS("相等", "equals");

    private final String text;

    private final String value;

    /**
     * 根据传入的 value 值, 来返回对应的枚举类型
     *
     * @param value
     * @return
     */
    public static FileFilterRuleEnum getFilterRuleEnumByValue(String value) {
        if (StrUtil.isEmpty(value)) {
            return null;
        }
        for (FileFilterRuleEnum rangeEnum : FileFilterRuleEnum.values()) {
            if (rangeEnum.value.equals(value))
                return rangeEnum;
        }
        return null;
    }

}
