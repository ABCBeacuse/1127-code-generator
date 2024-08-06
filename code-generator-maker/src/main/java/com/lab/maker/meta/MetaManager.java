package com.lab.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

/**
 * 读取 resource 下的 meta.json 元信息 文件, 并赋值给 Meta 对象
 * <p>
 * 单例模式（双检索）, 保证 “多线程环境下, 只读取文件一次”, 只创建一个 Meta 对象
 */
public class MetaManager {

    private static volatile Meta meta;

    public static Meta getMetaInstance() {
        // 多线程下, 挡住大部分并发线程
        if (meta == null) {
            // 可能会有少量线程进到这里, 由于是同时判断, 可能会有几个线程都判断 meta == null
            synchronized (MetaManager.class) {
                // 挨个进入到这里, 再次判断一下, 防止 后进入的线程 重复创建
                if (meta == null) {
                    meta = initMeta();
                }
            }
        }
        return meta;
    }

    private static Meta initMeta() {
        // 读取 classpath 类路径下的文件
        String metaInfo = ResourceUtil.readUtf8Str("springboot-init-meta.json");
        // 将 JSON 字符串 转为 Meta 对象
        Meta meta = JSONUtil.toBean(metaInfo, Meta.class);
        // 校验 meta 元数据 对象 数据规则
        MetaValidator.doValidator(meta);
        return meta;
    }

}
