package com.lab.maker.generator;

import com.lab.maker.meta.Meta;
import com.lab.maker.meta.MetaManager;

public class MainGenerator {
    public static void main(String[] args) {
        Meta meta = MetaManager.getMetaInstance();
        System.out.println(meta);
    }
}
