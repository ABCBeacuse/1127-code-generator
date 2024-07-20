package com.lab.maker.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.lab.maker.generator.file.FileGenerator;
import com.lab.maker.model.DataModel;
import lombok.Data;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "generator", mixinStandardHelpOptions = true)
@Data
public class GeneratorCommand implements Callable<Integer> {

    @Option(names = {"-l", "--loop"}, arity = "0..1", description = "是否循环", interactive = true)
    private String loop;

    @Option(names = {"-a", "--author"}, arity = "0..1", description = "输入用户姓名", interactive = true)
    private String author;

    @Option(names = {"-o", "--output"}, arity = "0..1", description = "输入自定义信息", interactive = true)
    private String outputContext;

    @Override
    public Integer call() throws Exception {
        DataModel model = new DataModel();
        BeanUtil.copyProperties(this, model);
        FileGenerator.doGenerator(model);
        return 0;
    }
}
