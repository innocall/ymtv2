package com.lemon95.ymtv.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lemon95.ymtv.R;
import com.lemon95.ymtv.bean.LiveType;
import com.starschina.types.DChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wuxiaotie on 2016/10/26.
 */
public class LiveTypeAdapter extends BaseAdapter {

    private List<LiveType> list = new ArrayList<>();
    private Context context;

    public LiveTypeAdapter(Context context, List<LiveType> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            convertView = View.inflate(context, R.layout.item_video_view_meun, null);
            holder = new ViewHolder();
            holder.lemon95_menu_item = (TextView) convertView.findViewById(R.id.lemon95_menu_item);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        LiveType LiveType = list.get(position);
        if(LiveType != null){
            holder.lemon95_menu_item.setText(LiveType.getName());
        }
        return convertView;
    }

    class ViewHolder{
        TextView lemon95_menu_item;
    }
}
