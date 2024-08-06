package com.lab.maker.template.utils;

import cn.hutool.core.util.StrUtil;
import com.lab.maker.meta.Meta;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 代码生成器模板制作 工具类
 */
public class TemplateMakerUtils {

    public static List<Meta.FileConfig.FileInfo> filterRepeatFileInfo(List<Meta.FileConfig.FileInfo> fileInfoList) {
        // 1. 先获取文件信息列表中的 分组部分
        List<Meta.FileConfig.FileInfo> groupPart = fileInfoList.stream().filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey())).collect(Collectors.toList());
        // 2. 获取分组中的所有文件的 inputPath , 并合并为一个 Set
        Set<String> groupFileList = groupPart.stream().flatMap(group -> group.getFiles().stream())
                .map(Meta.FileConfig.FileInfo::getInputPath)
                .collect(Collectors.toSet());
        // 3. 对未分组的文件列表进行去重, 得到 去除分组内文件信息 的 List
        List<Meta.FileConfig.FileInfo> result = fileInfoList.stream().filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                .filter(fileInfo -> !groupFileList.contains(fileInfo.getInputPath()))
                .collect(Collectors.toList());
        // 4. 重新补充上分组信息
        result.addAll(groupPart);
        return result;
    }

}
