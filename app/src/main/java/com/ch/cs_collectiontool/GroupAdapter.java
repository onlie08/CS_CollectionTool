package com.ch.cs_collectiontool;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;

import com.ch.cs_collectiontool.bean.Group;
import com.ch.cs_collectiontool.bean.Room;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class GroupAdapter extends BaseQuickAdapter<Group, BaseViewHolder> {
    private Context context;
    public GroupAdapter(List<Group> data,Context context) {
        super(R.layout.list_group_item, data);
        this.context = context;
    }
    @Override
    protected void convert(BaseViewHolder helper, final Group item) {
        helper.setText(R.id.tv_group,item.getGroupName());
        helper.setText(R.id.tv_group_num,item.getRoomNum()+"");
        TextView tv_info = helper.getView(R.id.tv_info);
        tv_info.setText(checkProgress(item));
//        tv_info.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
//        tv_info.getPaint().setAntiAlias(true);//抗锯齿

        tv_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("group",item);
                intent.setClass(context, RoomActivity.class);
                context.startActivity(intent);
            }
        });
    }

    private String checkProgress(Group item){
        int undone = 0 ;
        if(null == item.getRooms() || item.getRooms().size() == 0){
            return "未填写("+undone+"/"+item.getRoomNum()+")";
        }else {
            if (item.getRooms().size() < item.getRoomNum()) {
                return "填写中(" + item.getRooms().size() + "/" + item.getRoomNum() + ")";
            } else {
                return "已完成(" + item.getRoomNum() + "/" + item.getRoomNum() + ")";
            }
        }
    }

}
