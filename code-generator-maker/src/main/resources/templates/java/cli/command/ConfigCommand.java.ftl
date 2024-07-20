package ${basePackage}.maker.cli.command;

import cn.hutool.core.util.ReflectUtil;
import ${basePackage}.maker.model.DataModel;
import picocli.CommandLine.Command;

import java.lang.reflect.Field;

@Command(name = "config", description = "查看该命令需要传递的参数信息", mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable {

    @Override
    public void run() {
        // 利用反射来获取 MainTemplateModel 配置类中的属性信息
        Field[] fields = ReflectUtil.getFields(DataModel.class);
        for (Field field : fields) {
            System.out.println("- 参数类型：" + field.getType() + " - 参数名称：" + field.getName());
        }
    }
}