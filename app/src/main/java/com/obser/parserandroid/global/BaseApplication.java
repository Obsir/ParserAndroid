package com.obser.parserandroid.global;

import android.app.Application;
import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2016/12/4 0004.
 */
public class BaseApplication extends Application {
    private static File file;
    @Override
    public void onCreate() {
        super.onCreate();

    }
    public static File getFile(){
        if(file == null)
            file = new File(Environment.getExternalStorageDirectory(), "test.txt");
        return file;
    }
}
