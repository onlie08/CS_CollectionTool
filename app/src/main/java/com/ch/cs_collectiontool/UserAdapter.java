package com.ch.cs_collectiontool;

import com.ch.cs_collectiontool.bean.Group;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class UserAdapter extends BaseQuickAdapter<Group, BaseViewHolder> {
    public UserAdapter(List<Group> data) {
        super(R.layout.list_user_item, data);
    }
    @Override
    protected void convert(BaseViewHolder helper, Group item) {
        helper.setText(R.id.tv_group,item.getGroupName());
        helper.setText(R.id.tv_family_num,item.getDoorNum()+"");
        helper.setText(R.id.tv_plate_num,item.getRoomNum()+"");
        helper.setText(R.id.tv_info,item.getRemark());
    }

}
