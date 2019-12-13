package com.ch.cs_collectiontool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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

import java.util.ArrayList;
import java.util.List;

public class DialogInputController {
    private List<SubRoom> subRooms = new ArrayList<>();
    private List<Room> allRooms = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();
    private String location;
    DialogGroupListener groupListener;
    public void setGroupListener(DialogGroupListener listener) {
        this.groupListener = listener;
    }

    public void setLocation(String result) {
        location = result;
    }

    public void setAllRooms(List<Room> allRooms) {
        this.allRooms = allRooms;
    }

    public void setAllGroups(List<Group> groups) {
        this.groups = groups;
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

                boolean groupNameOk = true;
                for(Group group1 : groups){
                    if(null == group){
                        if(group1.getGroupName().equals(edit_input_group.getEditableText().toString())){
                            groupNameOk = false;
                        }
                    }else {
                        if(group1.getGroupName().equals(edit_input_group.getEditableText().toString()) && group1.getId() != group.getId()){
                            groupNameOk = false;
                        }
                    }
                }
                if(!groupNameOk){
                    Toast.makeText(mContext,edit_input_group.getEditableText().toString()+"已存在，请检查",Toast.LENGTH_LONG).show();
                    return;
                }
                hideSoftKeyboard((Activity)mContext);
                dialog.dismiss();
                Group group1 = new Group();
                group1.setGroupName(edit_input_group.getEditableText().toString());
                group1.setRoomNum(Integer.parseInt(edit_input_plate.getEditableText().toString()));
                if(null == location){
                    if(null != group){
                        group1.setLat(group.getLat());
                        group1.setLng(group.getLng());
                    }
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

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                View alarm = ((Activity)mContext).getLayoutInflater().inflate(R.layout.dialog_alarm_layout,null);
                builder.setView(alarm);
                builder.setCancelable(true);

                final Dialog dialog1 = builder.create();
                dialog1.show();

                TextView tv_alarm_info = alarm.findViewById(R.id.tv_alarm_info);
                tv_alarm_info.setText("注意数据删除后无法恢复，是否确认删除？");
                Button btn_report = alarm.findViewById(R.id.btn_report);
                btn_report.setText("确认");
                btn_report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hideSoftKeyboard((Activity)mContext);
                        dialog.dismiss();
                        groupListener.delectItem();

                        dialog1.dismiss();
                    }
                });
                Button btn_cancel = alarm.findViewById(R.id.btn_cancel);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog1.dismiss();
                    }
                });


            }
        });

        tv_choose_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext,LocationActivity.class);
                if(null != group){
                    if(group.getLng() != 0){
                        intent.putExtra("location",group.getLat()+","+group.getLng());
                    }
                }
                ((Activity) mContext).startActivityForResult(intent,99);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv_choose_gps.setText("位置已选取");
                    }
                },1000);
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
        final EditText tv_input_door = view.findViewById(R.id.tv_input_door);
        final ImageView img_reserve = view.findViewById(R.id.img_reserve);
        final EditText edit_input_owner = view.findViewById(R.id.edit_input_owner);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);
        Button btn_save = view.findViewById(R.id.btn_save);

        builder.setView(view);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();
        dialog.show();

        boolean isStreet = false;
        if(room.getBelongGroup().contains("街") || room.getBelongGroup().contains("路")
                || room.getBelongGroup().contains("巷") || room.getBelongGroup().contains("道")
                || room.getBelongGroup().contains("里") || room.getBelongGroup().contains("堂")){
            tv_input_door.setEnabled(true);
            isStreet = true;
        }else {
            tv_input_door.setEnabled(false);

        }
        tv_input_door.setText(room.getRoomNo()+"");
        tv_input_group.setText(room.getBelongGroup());

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

        final boolean finalIsStreet = isStreet;
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(edit_input_owner.getEditableText().toString()) && !img_reserve.isSelected() ){
                    Toast.makeText(mContext,"实体名称和预留信息不可同时为空",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(tv_input_door.getEditableText().toString())){
                    Toast.makeText(mContext,"门牌号不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                boolean subRoomOk = true;
                for(SubRoom subRoom : subRooms){
                    if(TextUtils.isEmpty(subRoom.getOwnerName())){
                        subRoomOk = false;
                    }
                }
                if(!subRoomOk){
                    Toast.makeText(mContext,"分支实体名称不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                if(finalIsStreet){
                    int roomNum = 0;
                    for(Room room1 : allRooms){
                        if(String.valueOf(room1.getRoomNo()).equals(tv_input_door.getEditableText().toString())){
                            roomNum++;
                        }
                    }
                    if(roomNum>1){
                        Toast.makeText(mContext,"输入门牌号有重复，请检查",Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                hideSoftKeyboard((Activity)mContext);
                dialog.dismiss();
                if(TextUtils.isEmpty(edit_input_owner.getEditableText().toString())){
                    room.setOwnerName("");
                }else {
                    room.setOwnerName(edit_input_owner.getEditableText().toString());
                }
                room.setRoomNo(Integer.parseInt(tv_input_door.getEditableText().toString()));
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
