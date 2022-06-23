package org.springcms.develop.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class TemplateUtils {

    @Autowired
    public static final TemplateEngine engine = ApplicationContextHolder.getBean(TemplateEngine.class);

    /**
     * 生成静态文件
     * @param freeTempName 模板名称
     * @param context 数据内容
     * @param outFilePath 输出路径
     * @return
     */
    public static boolean process(String freeTempName, Context context, String outFilePath) {
        FileWriter fileWriter = null;
        try {
            if (!(new File(outFilePath)).getParentFile().exists()) {
                new File(outFilePath).getParentFile().mkdirs();
            }
            fileWriter = new FileWriter(outFilePath);
            engine.process(freeTempName, context, fileWriter);

            int byteread = 0;
            byte[] buf = new byte[1024];
            StringBuffer sb = new StringBuffer();
            Resource resource = new ClassPathResource(String.format("templates/%s", freeTempName));
            InputStream is = resource.getInputStream();
            while ((byteread = is.read(buf)) != -1) {
                //  System.out.write(tempbytes, 0, byteread);
                String str = new String(buf, 0, byteread);
                sb.append(str);
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static boolean process(String freeTempName, String outFilePath, Object... args) {
        FileWriter fileWriter = null;
        try {
            if (!(new File(outFilePath)).getParentFile().exists()) {
                new File(outFilePath).getParentFile().mkdirs();
            }
            fileWriter = new FileWriter(outFilePath);

            int byteread = 0;
            byte[] buf = new byte[1024];
            StringBuffer sb = new StringBuffer();
            Resource resource = new ClassPathResource(String.format("templates/%s", freeTempName));
            InputStream is = resource.getInputStream();
            while ((byteread = is.read(buf)) != -1) {
                //  System.out.write(tempbytes, 0, byteread);
                String str = new String(buf, 0, byteread);
                sb.append(str);
            }
            is.close();

            String outStr = String.format(sb.toString(), args);
            fileWriter.write(outStr);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}

