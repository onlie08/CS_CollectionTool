package com.ch.cs_collectiontool.adapter;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ch.cs_collectiontool.R;
import com.ch.cs_collectiontool.bean.Room;
import com.ch.cs_collectiontool.bean.SubRoom;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class SubRoomAdapter extends BaseQuickAdapter<SubRoom, BaseViewHolder> {
    private List<SubRoom> data;

    public interface OnDeleteListener{
        void delete();
        void setOwnerName(int pos,String ownerName);
    }

    OnDeleteListener listener;

    public void setListener(OnDeleteListener listener) {
        this.listener = listener;
    }

    public SubRoomAdapter(List<SubRoom> data) {
        super(R.layout.list_subitem_item, data);
        this.data = data;
    }
    @Override
    protected void convert(final BaseViewHolder helper, final SubRoom item) {
        helper.setText(R.id.tv_sub_room_num,item.getRoomNo()+"");
        final TextView edit_sub_owner_name= helper.getView(R.id.edit_sub_owner_name);
        edit_sub_owner_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                listener.setOwnerName(helper.getAdapterPosition(),s.toString());
            }
        });
        edit_sub_owner_name.setText(item.getOwnerName());
        final ImageView img_delete= helper.getView(R.id.img_delete);
        int pos = getParentPosition(item);
        Log.i("caohai","pos:"+pos);

        if(data.get(data.size()-1) == item){
            img_delete.setVisibility(View.VISIBLE);
        }else {
            img_delete.setVisibility(View.GONE);
        }
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.delete();
            }
        });
    }

}