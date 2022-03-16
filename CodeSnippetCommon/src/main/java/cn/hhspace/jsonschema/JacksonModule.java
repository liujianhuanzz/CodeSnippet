package cn.hhspace.jsonschema;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/3/15 5:04 下午
 * @Descriptions: Jackson与Guice配合使用
 */
public class JacksonModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bindConstant().annotatedWith(Names.named("name")).to("Jackson");
        binder.bind(TestAnotherPerson.class);
    }
}
