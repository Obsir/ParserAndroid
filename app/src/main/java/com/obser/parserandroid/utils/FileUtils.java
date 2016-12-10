package com.obser.parserandroid.utils;

import com.lidroid.xutils.util.IOUtils;
import com.obser.parserandroid.global.BaseApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Administrator on 2016/12/7 0007.
 */
public class FileUtils {
    public static void saveLocal(String string) {


        File file = BaseApplication.getFile();
        BufferedWriter bufferedWriter = null;
        try {
            FileWriter fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(string);//把整个json文件保存起来
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            IOUtils.closeQuietly(bufferedWriter);
        }
    }
}
