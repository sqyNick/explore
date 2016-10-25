package com.fhzz.cn.exploremap.view;

import android.view.View;

/**
 * Created by Administrator on 2016/10/14.
 */

public class TargetView {
    View target;
    public TargetView(View view){
        target = view;
    }
    public int getWidth(){
        return target.getLayoutParams().width;
    }
    public void setWidth(int width){
        target.getLayoutParams().width = width;
        target.requestLayout();
    }

    public int getHeight(){
        return target.getLayoutParams().height;
    }
    public void setHeight(int height){
        target.getLayoutParams().height = height;
        target.requestLayout();
    }
}
