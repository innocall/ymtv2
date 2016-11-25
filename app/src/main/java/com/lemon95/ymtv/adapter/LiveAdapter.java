package com.lemon95.ymtv.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lemon95.ymtv.R;
import com.lemon95.ymtv.utils.StringUtils;
import com.starschina.networkutils.MyVolley;
import com.starschina.types.DChannel;
import com.starschina.types.Epg;
import com.starschina.volley.toolbox.ImageLoader;
import com.starschina.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxiaotie on 2016/10/24.
 */
public class LiveAdapter extends BaseAdapter {

    private List<DChannel> mDatas = new ArrayList<>();
    private Context context;

    public LiveAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<DChannel> list){
        mDatas.clear();
        mDatas.addAll(list);
    }

    @Override
    public int getCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    @Override
    public DChannel getItem(int position) {
        return mDatas != null ? mDatas.get(position): null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            convertView = View.inflate(context, R.layout.item_live_item, null);
            holder = new ViewHolder();
            holder.lemon95_menu_item = (TextView) convertView.findViewById(R.id.lemon95_menu_item);
            holder.lemon95_menu_item_next = (TextView) convertView.findViewById(R.id.lemon95_menu_item_next);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        DChannel channel = mDatas.get(position);
        if(channel != null){
            holder.lemon95_menu_item.setText(channel.name);
            Epg next = channel.currentEpg;
            if (next != null && StringUtils.isNotBlank(next.name)) {
                holder.lemon95_menu_item_next.setVisibility(View.VISIBLE);
                holder.lemon95_menu_item_next.setText("正在播放：" + next.name);
            } else {
                holder.lemon95_menu_item_next.setVisibility(View.INVISIBLE);
            }
        }
        return convertView;
    }

    class ViewHolder{
        TextView lemon95_menu_item;
        TextView lemon95_menu_item_next;
    }
}