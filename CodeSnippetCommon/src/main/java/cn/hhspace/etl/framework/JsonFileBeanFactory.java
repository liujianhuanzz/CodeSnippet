package cn.hhspace.etl.framework;

import cn.hhspace.etl.common.ErrorCode;
import cn.hhspace.etl.utils.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Descriptions: 使用一个本地JSON文件来保存所有的对象配置
 * @Author: Jianhuan-LIU
 * @Date: 2021/4/3 1:14 下午
 * @Version: 1.0
 */
public class JsonFileBeanFactory extends JsonBeanFactory {

    private static final Logger logger = LoggerFactory.getLogger(JsonFileBeanFactory.class);

    private String jsonConfigFileName;

    public String getJsonConfigFileName() {
        return jsonConfigFileName;
    }

    public void setJsonConfigFileName(String jsonConfigFileName) {
        this.jsonConfigFileName = jsonConfigFileName;
    }

    /**
     * 通过文件建立插件，会首先在文件目录下搜索文件，如果找不到，再到类路径下查找
     * @param fileName
     * @throws IOException
     */
    private synchronized void buildConfigFromFile(String fileName) throws IOException {
        String jsonConfigStr;
        logger.info("从文件中读取插件配置：" + fileName);
        if (new File(fileName).exists()) {
            jsonConfigStr = ResourceUtil.readStreamToStr(new FileInputStream(fileName));
        } else {
            InputStream inputStream = this.getClass().getResourceAsStream(fileName);
            if(inputStream == null) {
                throw new BeanException(ErrorCode.NODE_RUN_ERROR, "无法从类路径下找到此文件：" + fileName);
            }
            jsonConfigStr = ResourceUtil.readStreamToStr(inputStream);
        }
        createBeans(jsonConfigStr);
    }

    /**
     * 把插件配置保存到某个文件
     * @param fileName
     * @throws IOException
     */
    private synchronized void saveConfigToFile(String fileName) throws IOException {
        String jsonConfigStr;
        logger.info("保存插件配置到文件：" + fileName);
        jsonConfigStr = this.getJsonConfig();
        ResourceUtil.writeStrToFile(fileName, jsonConfigStr);
    }

    /**
     * 初始化插件，一般在这里完成其他插件的引用和装配工作，从BeanInventory中查找插件
     * 初始化工厂管理的插件
     * @param inv
     * @throws Exception
     */
    @Override
    public synchronized void init(BeanInventory inv) throws Exception {
        registerPlugins();
        try {
            buildConfigFromFile(jsonConfigFileName);
        } catch (IOException e) {
            throw new BeanException(ErrorCode.BEAN_FACTORY_IOERROR, "", e);
        }
        super.init(inv);
    }

    /**
     * 保存某个对象到外部存储。对于数据库方式，saveBean就是直接保存，文件方式这个方法并不直接保存，要等saveAllBeans才报存到文件
     *
     * @param id
     */
    @Override
    public void saveBean(String id) {
        //do nothing
    }

    /**
     * 保存全部对象到外部存储
     */
    @Override
    public void saveAllBean() {
        //在JSONFileBeanFactory的逻辑里，对象都是保存在一个文件里，保存一个和保存所有都是一样的
        try {
            saveConfigToFile(jsonConfigFileName);
        } catch (IOException e) {
            throw new BeanException(ErrorCode.BEAN_FACTORY_IOERROR, "", e);
        }
    }
}
