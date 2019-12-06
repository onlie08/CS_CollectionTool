package com.ch.cs_collectiontool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ch.cs_collectiontool.bean.Group;

public class DialogInputController {
    interface DialogListener{
        void saveItem(Group group);
        void delectItem();
        void updateItem(Group group);
    }

    DialogListener listener;

    public void setListener(DialogListener listener) {
        this.listener = listener;
    }

    private Context mContext;
    public EditText edit_input_group;
    public EditText edit_input_door;
    public EditText edit_input_plate;
    public EditText edit_input_info;

    public DialogInputController(Context mContext) {
        this.mContext = mContext;
    }

    public void showAddNewDilog(final Group group){
        AlertDialog.Builder builder = new AlertDialog.Builder((Activity)mContext);
        View view = View
                .inflate((Activity)mContext, R.layout.dialog_enter_info, null);

        TextView tv_delect = view.findViewById(R.id.tv_delect);
        edit_input_group = view.findViewById(R.id.edit_input_group);
        edit_input_door = view.findViewById(R.id.edit_input_door);
        edit_input_plate = view.findViewById(R.id.edit_input_plate);
        edit_input_info = view.findViewById(R.id.edit_input_info);
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
            edit_input_door.setText(group.getDoorNum()+"");
            edit_input_plate.setText(group.getRoomNum()+"");
            edit_input_info.setText(group.getRemark());
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
                hideSoftKeyboard((Activity)mContext);
                dialog.dismiss();
                Group group1 = new Group();
                group1.setGroupName(edit_input_group.getEditableText().toString());
                if(TextUtils.isEmpty(edit_input_door.getEditableText().toString())){
                    group1.setDoorNum(0);
                }else {
                    group1.setDoorNum(Integer.parseInt(edit_input_door.getEditableText().toString()));
                }
                if(TextUtils.isEmpty(edit_input_plate.getEditableText().toString())){
                    group1.setRoomNum(0);
                }else {
                    group1.setRoomNum(Integer.parseInt(edit_input_plate.getEditableText().toString()));
                }
                group1.setRemark(edit_input_info.getEditableText().toString());
                if(null != group){
                    listener.updateItem(group1);
                }else {
                    listener.saveItem(group1);
                }
            }
        });

        tv_delect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard((Activity)mContext);
                dialog.dismiss();
                listener.delectItem();
            }
        });
//        hideSoftKeyboard((Activity)mContext);
    }

    /**
     * 隐藏软键盘(只适用于Activity，不适用于Fragment)
     */
    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit_input_group.getWindowToken(), 0);
    }
}
