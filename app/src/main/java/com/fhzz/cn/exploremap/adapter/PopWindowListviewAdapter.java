package com.fhzz.cn.exploremap.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fhzz.cn.exploremap.R;

import java.util.List;

/**
 * Created by Administrator on 2016/10/8.
 */

public class PopWindowListviewAdapter extends BaseAdapter {
    List<String> mLists;
    Context mContext;
    String mCurrentValue;
    public PopWindowListviewAdapter(List<String> lists , Context context,String currentValue){
        this.mContext = context;
        this.mLists = lists;
        this.mCurrentValue = currentValue;
    }
    @Override
    public int getCount() {
        return (mLists == null) ? 0 : mLists.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_list_view_login,viewGroup,false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        holder = (ViewHolder) view.getTag();
        initDate(holder,i);
        return view;
    }
    public void initDate(ViewHolder holder,int position){
        holder.textView.setText(mLists.get(position));
        if(mLists.get(position).equals(mCurrentValue)){
            holder.textView.setTextColor(Color.parseColor("#ff9800"));
        }else{
            holder.textView.setTextColor(Color.parseColor("#000000"));
        }
    }
    static class ViewHolder{
        TextView textView;
        ViewHolder(View v){
            textView = (TextView) v.findViewById(R.id.tv_login_listview);
        }
    }
}
