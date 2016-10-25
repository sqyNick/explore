package com.fhzz.cn.exploremap.util;

import android.content.Context;
import android.os.Environment;

import com.fhzz.cn.exploremap.value.StaticValues;

import java.io.File;

/**
 * Created by Administrator on 2016/10/8.
 */

public class PhotoUtil {
    public static String getDirPath(Context context){
        String filePath;
        String phone = SPUtil.getString(context,StaticValues.NOW_LOGIN_PHONE);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            filePath = Environment.getExternalStorageDirectory() + "/" + StaticValues.APP_DIRECTORY + "/" + phone + "/" + StaticValues.CAPTURE_PIC_DIR;
        } else {
            filePath = context.getCacheDir() + StaticValues.APP_DIRECTORY + "/" + phone + "/" + StaticValues.CAPTURE_PIC_DIR;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return filePath;
    }
}
