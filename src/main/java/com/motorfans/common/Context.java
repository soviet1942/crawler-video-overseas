package com.motorfans.common;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Context implements Variable {

    private static Context instance = new Context();
    private static Properties props = new Properties();

    private Context() {}

    static {
        instance.loadAllProps();
    }

    /**
     * load properties
     */
    private void loadAllProps() {
        String scanPath = (StringUtils.isEmpty(PROP_SCAN_PATH)) ? "/" : PROP_SCAN_PATH;
        String classPath = Context.class.getResource(scanPath).toString();
        classPath = classPath.replaceAll("file:/", "");
        instance.loadProp(new File(classPath));
    }

    private void loadProp(File file) {
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            for(File f1 : files) {
                loadProp(f1);
            }
        } else {
             if(file.getName().endsWith(".properties")) {
                 try {
                     props.load(new FileInputStream(file.getPath()));
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
        }
    }

    /**
     * get property
     * @param propName
     * @return
     */
    public static String getProp(String propName) {
        return props.getProperty(propName);
    }

    public static String getProp(String propName, String defaultValue) {
        return props.getProperty(propName) == null ? defaultValue : props.getProperty(propName);
    }



}
