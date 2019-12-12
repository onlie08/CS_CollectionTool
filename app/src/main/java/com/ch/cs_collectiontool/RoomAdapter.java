package com.ch.cs_collectiontool;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.ch.cs_collectiontool.bean.Room;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class RoomAdapter extends BaseQuickAdapter<Room, BaseViewHolder> {
    public RoomAdapter(List<Room> data) {
        super(R.layout.list_checkin_item, data);
    }
    @Override
    protected void convert(BaseViewHolder helper, Room item) {
        helper.setText(R.id.tv_group,item.getBelongGroup());
        helper.setText(R.id.tv_family_num, item.getRoomNo()+"");
        helper.setText(R.id.tv_family_owner,TextUtils.isEmpty(item.getOwnerName()) ? "" : item.getOwnerName());
        ImageView img_checkin = helper.getView(R.id.img_checkin);
        if(item.isReserve()){
            img_checkin.setSelected(true);
        }else {
            img_checkin.setSelected(false);
        }
    }

}
