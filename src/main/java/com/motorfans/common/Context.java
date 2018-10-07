package com.motorfans.common;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Context implements Variable {

    private static Context context = null;
    private Properties props = new Properties();

    private Context() {}

    public static Context getContext() {
        if(context == null) {
            synchronized (Context.class) {
                if(context == null) {
                    String classPath = Context.class.getResource("/").toString();
                    classPath = classPath.replaceAll("file:/", "");
                    context = new Context();
                    context.doPath(new File(classPath));
                }
            }
        }
        return context;
    }

    private void doPath(File file) {
        if(file.isDirectory()) {
            File[] files = file.listFiles();
            for(File f1 : files) {
                doPath(f1);
            }
        } else {
             if(file.getName().endsWith(".properties")) {
                loadProperty(file.getPath());
            }
        }
    }

    private void loadProperty(String path) {
        try {
            props.load(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProp(String propName) {
        return props.getProperty(propName);
    }

}
