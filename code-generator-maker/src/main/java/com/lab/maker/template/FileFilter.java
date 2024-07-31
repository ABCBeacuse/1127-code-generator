package com.lab.maker.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.lab.maker.template.enums.FileFilterRangeEnum;
import com.lab.maker.template.enums.FileFilterRuleEnum;
import com.lab.maker.template.model.FileFilterConfig;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件过滤功能类
 */
public class FileFilter {

    /**
     * 单个文件或者文件目录下所有文件的过滤
     *
     * @param filterList 针对于 当前 path 对应的文件 或者 对应的目录 的过滤规则
     * @param path 绝对路径
     * @return
     */
    public static List<File> doFilter(List<FileFilterConfig> filterList, String path) {
        List<File> files = FileUtil.loopFiles(path);
        return files.stream().filter(file -> doSingleFileFilter(filterList, file)).collect(Collectors.toList());
    }


    /**
     * 单个文件的过滤
     *
     * @param filterList
     * @param file
     * @return
     */
    public static boolean doSingleFileFilter(List<FileFilterConfig> filterList, File file) {
        String fileName = file.getName();
        String fileContent = FileUtil.readUtf8String(file);
        if (CollUtil.isEmpty(filterList)) {
            return true;
        }
        boolean result = true;
        for (FileFilterConfig config : filterList) {
            FileFilterRangeEnum rangeEnum = FileFilterRangeEnum.getFilterRangEnumByValue(config.getRange());
            if (rangeEnum == null) {
                continue;
            }
            FileFilterRuleEnum ruleEnum = FileFilterRuleEnum.getFilterRuleEnumByValue(config.getRule());
            if (ruleEnum == null) {
                continue;
            }
            String filterValue = config.getValue();
            String content = fileContent;
            switch (rangeEnum) {
                case FILE_NAME:
                    content = fileName;
                    break;
                case FILE_CONTENT:
                    content = fileContent;
                    break;
                default:
            }

            switch (ruleEnum) {
                case CONTAINS:
                    result = content.contains(filterValue);
                    break;
                case STARTS_WITH:
                    result = content.startsWith(filterValue);
                    break;
                case ENDS_WITH:
                    result = content.endsWith(filterValue);
                    break;
                case REGEX:
                    result = content.matches(filterValue);
                    break;
                case EQUALS:
                    result = content.equals(filterValue);
                    break;
            }

            if (!result) {
                return false;
            }
        }
        return true;
    }
}
