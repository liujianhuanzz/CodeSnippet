package cn.hhspace.etl.utils;


import cn.hutool.core.util.EscapeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @Descriptions:
 * @Author: Jianhuan-LIU
 * @Date: 2021/3/22 5:47 下午
 * @Version: 1.0
 */
public class PackageSearch {

    private static final Logger logger = LoggerFactory.getLogger(PackageSearch.class);

    List<String> classNames = null;
    NameFilter fnFilter = null;
    boolean childPackage = true;

    public PackageSearch(boolean searchChild, NameFilter filter) {
        this.fnFilter = filter;
        this.childPackage = searchChild;
    }

    /**
     * 获取某包下所有的资源名
     * @param packageName
     * @return
     * @throws IOException
     */
    public List<String> getResNamesFromPkg(String packageName) throws IOException {
        classNames = new ArrayList<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = "";
        if (packageName != null) {
            packagePath = packageName.replace(".", "/");
        }
        packagePath = EscapeUtil.safeUnescape(packagePath);
        Enumeration<URL> resources = loader.getResources(packagePath);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            logger.info("search context classpath  url:{}", url.toString());
            String type = url.getProtocol();
            if (type.equals("file")) {
                getResourceNameByFile(url.getPath());
            } else if (type.equals("jar")){
                getResourceNameByJar(url.getPath());
            }
        }

        return classNames;
    }

    private void getResourceNameByJar(String jarPath) {
    }

    /**
     * 从项目文件获取某包下所有类
     * @param filePath
     */
    private void getResourceNameByFile(String filePath) {
        filePath = EscapeUtil.safeUnescape(filePath);
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        for (File childFile: childFiles) {
            if (childFile.isDirectory()) {
                if (childPackage) {
                    getResourceNameByFile(childFile.getPath());
                }
            } else {
                String fileName = childFile.getName();
                if (fnFilter.accept(fileName)) {
                    String childFilePath = childFile.getAbsolutePath();
                    //childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9);
                    childFilePath = childFilePath.replace("\\", "/");
                    classNames.add(childFilePath);
                }
            }
        }
    }
}
