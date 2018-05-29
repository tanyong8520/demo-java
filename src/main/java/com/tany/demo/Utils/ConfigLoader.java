package com.tany.demo.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

public class ConfigLoader {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);

    /**
     * 加载配置文件
     * 1、从系统配置的指定位置加载，默认配置为/etc/computing-data/
     * 2、第一步找不到文件时从程序启动位置加载（启动位置指执行启动命令的路径，不一定是jar包路径）
     * 3、第二步找不到时从resources加载
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static Properties load(String fileName) throws IOException {

        InputStream is = null;
        try {
            if (fileName.startsWith(File.separator)) {
                fileName = fileName.substring(1);
            }
            // 从指定位置加载
            File file = new File(Constants.CONFIG_FILE_DIR + fileName);
            if (!file.exists() || !file.isFile()) {
                // 从项目启动位置加载
                file = new File(fileName);
            }
            if (file.exists() && file.isFile()) {
                is = new FileInputStream(file);
            } else {
                // 从classes/下加载
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
            }

            Properties p = new Properties();
            p.load(is);
            return p;
        } catch (IOException e) {
            throw new IOException("load '" + fileName + "' error.", e);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * 获取当前项目所在绝对路径，IDE下运行时获取到的是src所在路径，jar包方式运行时获取到的是jar包所在路径
     *
     * @param clazz 调用类class
     * @return
     */
    public static String getCurrentPath(Class<?> clazz) {
        try {
            URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
            // 防止路径中有中文导致乱码
            String filePath = URLDecoder.decode(url.getPath(), "UTF-8");
            File file = new File(filePath);
            if (!file.isDirectory()) {
                file = file.getParentFile();
            } else if (filePath.endsWith("/target/classes/")) {
                file = file.getParentFile().getParentFile();
            }
            filePath = file.getAbsolutePath();
            return filePath;
        } catch (Exception e) {
            LOG.error("getPath error", e);
        }
        return null;
    }
}
