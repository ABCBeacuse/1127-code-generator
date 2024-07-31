package com.lab.maker.template.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileFilterRangeEnum {
    FILE_NAME("文件名称", "fileName"),
    FILE_CONTENT("文件内容", "fileContent");

    private final String text;

    private final String value;

    /**
     * 根据传入的 value 值, 来返回对应的枚举类型
     *
     * @param value
     * @return
     */
    public static FileFilterRangeEnum getFilterRangEnumByValue(String value) {
        if (StrUtil.isEmpty(value)) {
            return null;
        }
        for (FileFilterRangeEnum rangeEnum : FileFilterRangeEnum.values()) {
            if (rangeEnum.value.equals(value))
                return rangeEnum;
        }
        return null;
    }
}
