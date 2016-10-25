package com.fhzz.cn.exploremap.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.fhzz.cn.exploremap.value.StaticValues;

/**
 * Created by sqy on 2016/9/3.
 */
public class SPUtil {

    private static SharedPreferences getSP(Context context){
        return context.getSharedPreferences(StaticValues.SHARED_PREFERENCED_NAME,context.MODE_PRIVATE);
    }

    public static void put(Context context,String key,Object value){

        SharedPreferences.Editor edit = getSP(context).edit();
        if (value instanceof Integer) {
            edit.putInt(key, (Integer) value);
        }else if (value instanceof String) {
            edit.putString(key, (String) value);
        }else if (value instanceof Boolean) {
            edit.putBoolean(key, (Boolean) value);
        }

        edit.commit();
    }

    public static int getInt(Context context,String key){
        return getSP(context).getInt(key, 0);
    }
    public static String getString(Context context,String key){
        return getSP(context).getString(key, null);
    }
    public static boolean getBoolean(Context context, String key){
        return getSP(context).getBoolean(key, false);
    }
    public static void putLoginedPhone(Context context,String value){
        boolean exist = false;
        if(TextUtils.isEmpty(getSP(context).getString(StaticValues.LOGINED_PHONE,null))){
            put(context,StaticValues.LOGINED_PHONE,value);
        }else{
            if(getSP(context).getString(StaticValues.LOGINED_PHONE,null).contains(StaticValues.SPLIT)){
                String[] array = getSP(context).getString(StaticValues.LOGINED_PHONE,null).split(StaticValues.SPLIT);
                for(String s : array){
                    if(value.equals(s)){
                        exist = true;
                        return;
                    }
                }
                if(!exist){
                    put(context,StaticValues.LOGINED_PHONE,getSP(context).getString(StaticValues.LOGINED_PHONE,null) + StaticValues.SPLIT + value);
                }
            }else{
                if(value.equals(getSP(context).getString(StaticValues.LOGINED_PHONE,null))){
                    return;
                }
                String res = getSP(context).getString(StaticValues.LOGINED_PHONE,null) + StaticValues.SPLIT + value;
                put(context,StaticValues.LOGINED_PHONE,res);
            }
        }
    }

    public static void clearByKey(Context context, String key){
        SharedPreferences.Editor edit = getSP(context).edit();
        edit.remove(key).commit();
    }
}
