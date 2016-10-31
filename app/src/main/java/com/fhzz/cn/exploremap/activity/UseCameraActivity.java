package com.fhzz.cn.exploremap.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.fhzz.cn.exploremap.util.LogUtil;
import com.fhzz.cn.exploremap.util.PhotoUtil;
import com.fhzz.cn.exploremap.value.PhotoConstant;

import java.io.File;



/**
 * 照片生成的目录在 sd卡的/a/image/camera/.. .jpg
 *
 * @author baozi
 *
 */
public class UseCameraActivity extends Activity {
    private String mImageFilePath;


    private Activity mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断 activity被销毁后 有没有数据被保存下来
        if (savedInstanceState != null) {
            mImageFilePath = savedInstanceState.getString("ImageFilePath");
            File mFile = new File(mImageFilePath);
            if (mFile.exists()) {
                Intent rsl = new Intent();
                rsl.putExtra(PhotoConstant.IMAGE_PATH, mImageFilePath);
                setResult(Activity.RESULT_OK, rsl);
                finish();
            } else {
            }
        }

        mContext = this;
        if (savedInstanceState == null) {
            initialUI();
        }

    }

    public void initialUI() {
        //根据时间生成 后缀为  .jpg 的图片
        long ts = System.currentTimeMillis();
        mImageFilePath = PhotoUtil.getDirPath(this) +"/"+ ts + ".jpg";
        File out = new File(mImageFilePath);
        showCamera(out);
    }

    private void showCamera(File out) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(out)); // set
        startActivityForResult(intent, PhotoConstant.GET_IMAGE_REQ);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (PhotoConstant.GET_IMAGE_REQ == requestCode && resultCode == Activity.RESULT_OK) {
            Intent rsl = new Intent();
            rsl.putExtra(PhotoConstant.IMAGE_PATH, mImageFilePath);
            mContext.setResult(Activity.RESULT_OK, rsl);
            mContext.finish();
        } else {
            mContext.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("ImageFilePath", mImageFilePath + "");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

}
