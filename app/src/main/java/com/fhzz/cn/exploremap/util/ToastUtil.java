package com.fhzz.cn.exploremap.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fhzz.cn.exploremap.R;

/**
 * Created by Administrator on 2016/10/26.
 */

public class ToastUtil {
    protected static Toast toast;
    protected static  View toastContent;
    public static void show(Context context , String msg){
        if(toast == null){
            toast = new Toast(context);
        }
        if(toastContent == null){
            toastContent = LayoutInflater.from(context).inflate(R.layout.toast_layout_style,null);
        }
        TextView content = (TextView) toastContent.findViewById(R.id.tv_toast);
        content.setText(msg);
        toast.setView(toastContent);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}
