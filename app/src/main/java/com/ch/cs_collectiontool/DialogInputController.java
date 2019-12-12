package com.ch.cs_collectiontool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ch.cs_collectiontool.activity.LocationActivity;
import com.ch.cs_collectiontool.bean.Group;
import com.ch.cs_collectiontool.bean.Room;
import com.ch.cs_collectiontool.bean.SubRoom;
import com.ch.cs_collectiontool.view.SubItemController;
import com.google.gson.Gson;
import com.sfmap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;

public class DialogInputController {
    private List<SubRoom> subRooms = new ArrayList<>();
    private String location;
    DialogGroupListener groupListener;
    public void setGroupListener(DialogGroupListener listener) {
        this.groupListener = listener;
    }

    public void setLocation(String result) {
        location = result;
    }

    interface DialogGroupListener{
        void saveItem(Group group);
        void delectItem();
        void updateItem(Group group);
    }


    DialogRoomListener roomListener;
    public void setRoomListener(DialogRoomListener listener) {
        this.roomListener = listener;
    }
    interface DialogRoomListener{
        void saveItem(Room room);
    }

    private Context mContext;
    View view;

    public DialogInputController(Context mContext) {
        this.mContext = mContext;
    }

    public void showEditGroupDilog(final Group group){
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity)mContext);
        view = View
                .inflate((Activity)mContext, R.layout.dialog_group_input, null);

        TextView tv_delect = view.findViewById(R.id.tv_delect);
        final EditText edit_input_group = view.findViewById(R.id.edit_input_group);
        final EditText edit_input_plate = view.findViewById(R.id.edit_input_plate);
        final TextView tv_choose_gps = view.findViewById(R.id.tv_choose_gps);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        Button btn_save = view.findViewById(R.id.btn_save);

        builder.setView(view);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.show();

        if(group == null){
            tv_delect.setVisibility(View.GONE);
        }else {
            tv_delect.setVisibility(View.VISIBLE);
            edit_input_group.setText(group.getGroupName());
            edit_input_plate.setText(group.getRoomNum()+"");
            if(group.getLat() == 0){
                tv_choose_gps.setText("");
            }else {
                tv_choose_gps.setText("位置已选取");
            }
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard((Activity)mContext);
                dialog.dismiss();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(edit_input_group.getEditableText().toString())){
                    Toast.makeText(mContext,"请输入队/组信息",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(edit_input_plate.getEditableText().toString())){
                    Toast.makeText(mContext,"请输入门牌号数量",Toast.LENGTH_LONG).show();
                    return;
                }
                if(!tv_choose_gps.getText().equals("位置已选取")){
                    Toast.makeText(mContext,"请输入定位信息",Toast.LENGTH_LONG).show();
                    return;
                }
                hideSoftKeyboard((Activity)mContext);
                dialog.dismiss();
                Group group1 = new Group();
                group1.setGroupName(edit_input_group.getEditableText().toString());
                group1.setRoomNum(Integer.parseInt(edit_input_plate.getEditableText().toString()));
                if(null == location){
                    group1.setLat(group.getLat());
                    group1.setLng(group.getLng());
                }else {
                    String[] loc = location.split(",");
                    group1.setLat(Double.parseDouble(loc[0]));
                    group1.setLng(Double.parseDouble(loc[1]));
                }
                if(null != group){
                    group1.setVid(group.getVid());
                    group1.setId(group.getId());
                    groupListener.updateItem(group1);
                }else {
                    groupListener.saveItem(group1);
                }
            }
        });

        tv_delect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard((Activity)mContext);
                dialog.dismiss();
                groupListener.delectItem();
            }
        });

        tv_choose_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) mContext).startActivityForResult(new Intent().setClass(mContext, LocationActivity.class),99);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv_choose_gps.setText("位置已选取");
                    }
                },1000);
            }
        });
    }


    public void showEditGroupDilog1(final Group group){
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity)mContext);
        view = View
                .inflate((Activity)mContext, R.layout.dialog_road_input, null);

        TextView tv_delect = view.findViewById(R.id.tv_delect);
        final EditText edit_rode_name = view.findViewById(R.id.edit_rode_name);
        final EditText edit_room_num = view.findViewById(R.id.edit_room_num);
        final EditText edit_gps = view.findViewById(R.id.edit_gps);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        Button btn_save = view.findViewById(R.id.btn_save);

        builder.setView(view);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.show();

        if(group == null){
            tv_delect.setVisibility(View.GONE);
        }else {
            tv_delect.setVisibility(View.VISIBLE);
            edit_rode_name.setText(group.getGroupName());
            edit_room_num.setText(group.getRoomNum()+"");
            edit_gps.setText(group.getRemark());
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                hideSoftKeyboard((Activity)mContext);
                dialog.dismiss();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(edit_rode_name.getEditableText().toString())){
                    Toast.makeText(mContext,"请输入街/路/巷信息",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(edit_room_num.getEditableText().toString())){
                    Toast.makeText(mContext,"请输入门牌号数量",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(edit_gps.getEditableText().toString())){
                    Toast.makeText(mContext,"请输入定位信息",Toast.LENGTH_LONG).show();
                    return;
                }
//                hideSoftKeyboard((Activity)mContext);
                dialog.dismiss();
                Group group1 = new Group();
                group1.setGroupName(edit_rode_name.getEditableText().toString());
                group1.setRoomNum(Integer.parseInt(edit_room_num.getEditableText().toString()));
                group1.setRemark(edit_gps.getEditableText().toString());
                if(null != group){
                    groupListener.updateItem(group1);
                }else {
                    groupListener.saveItem(group1);
                }
            }
        });

        tv_delect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                hideSoftKeyboard((Activity)mContext);
                dialog.dismiss();
                groupListener.delectItem();
            }
        });
    }

    public void showEditRoomDilog(final Room room){
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity)mContext);
        view = View
                .inflate((Activity)mContext, R.layout.dialog_room_input, null);

        LinearLayout ln_subitem = view.findViewById(R.id.ln_subitem);
        final SubItemController subItemController = new SubItemController(mContext);
        subItemController.attachRoot(ln_subitem);
        subItemController.setListener(new SubItemController.OnDeleteListener() {
            @Override
            public void delete() {
                subRooms.remove(subRooms.size()-1);
                if(subRooms.size()>0){
                    subRooms.get(subRooms.size()-1).setEnableDelete(true);
                }
                subItemController.fillData(subRooms);
            }

            @Override
            public void setOwnerName(int pos, String ownerName) {
                subRooms.get(pos).setOwnerName(ownerName);
            }
        });

        TextView tv_add_subitem = view.findViewById(R.id.tv_add_subitem);
        TextView tv_input_group = view.findViewById(R.id.tv_input_group);
        EditText tv_input_door = view.findViewById(R.id.tv_input_door);
        final ImageView img_reserve = view.findViewById(R.id.img_reserve);
        final EditText edit_input_owner = view.findViewById(R.id.edit_input_owner);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        Button btn_save = view.findViewById(R.id.btn_save);

        builder.setView(view);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.show();


        tv_input_group.setText(room.getBelongGroup());
        tv_input_door.setText(room.getRoomNo()+"");
        edit_input_owner.setText(TextUtils.isEmpty(room.getOwnerName()) ? "" : room.getOwnerName());
        if(!TextUtils.isEmpty(room.getRemark())){
            String remark = room.getRemark();
            SubRoomList subRoomList = new Gson().fromJson(remark,SubRoomList.class);
            subRooms = subRoomList.getSubRooms();
            subItemController.fillData(subRooms);
        }

        if(room.isReserve()){
            img_reserve.setSelected(true);
        }else {
            img_reserve.setSelected(false);
        }
        edit_input_owner.requestFocus();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard((Activity)mContext);
                dialog.dismiss();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(edit_input_owner.getEditableText().toString()) && !img_reserve.isSelected() ){
                    Toast.makeText(mContext,"实体名称和预留信息不可同时为空",Toast.LENGTH_LONG).show();
                    return;
                }

                hideSoftKeyboard((Activity)mContext);
                dialog.dismiss();
                if(TextUtils.isEmpty(edit_input_owner.getEditableText().toString())){
                    room.setOwnerName("");
                }else {
                    room.setOwnerName(edit_input_owner.getEditableText().toString());
                }
                room.setReserve(img_reserve.isSelected());
                if(!subRooms.isEmpty()){
                    SubRoomList subRoomList = new SubRoomList();
                    subRoomList.setSubRooms(subRooms);
                    room.setRemark(new Gson().toJson(subRoomList));
                }

                roomListener.saveItem(room);
            }
        });
        img_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(img_reserve.isSelected()){
                    img_reserve.setSelected(false);
                }else {
                    img_reserve.setSelected(true);
                }
            }
        });
        tv_add_subitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubRoom subRoom = new SubRoom();
                clearSubRoomsStatue();
                subRoom.setRoomNo(room.getRoomNo()+"-"+(subRooms.size()+1));
                subRoom.setEnableDelete(true);
                subRooms.add(subRoom);
                subItemController.fillData(subRooms);
            }
        });

    }

    private void clearSubRoomsStatue() {
        for (int i=0;i<subRooms.size();i++){
            subRooms.get(i).setEnableDelete(false);
        }
    }

    /**
     * 隐藏软键盘(只适用于Activity，不适用于Fragment)
     */
    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    class SubRoomList{
        private List<SubRoom> subRooms;

        public List<SubRoom> getSubRooms() {
            return subRooms;
        }

        public void setSubRooms(List<SubRoom> subRooms) {
            this.subRooms = subRooms;
        }
    }

}
