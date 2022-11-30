package cn.hhspace.etl.app.springcfg;

import cn.hhspace.etl.framework.JsonBeanFactory;
import cn.hhspace.etl.framework.JsonFileBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Jianhuan-LIU
 * @Date: 2022/11/15 17:49
 * @Descriptions: 保存插件的方式
 */

@Configuration
public class BeanFactoryCfg {

    @Bean
    @ConditionalOnProperty(prefix = "etl.beanfactory", name = "type", havingValue = "File")
    public JsonBeanFactory getBeanFactory() {
        return new JsonFileBeanFactory();
    }
}
