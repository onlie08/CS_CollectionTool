package com.ch.cs_collectiontool;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ch.cs_collectiontool.bean.Group;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserCheckInActivity extends AppCompatActivity {
    ImmersionBar mImmersionBar;
    UserAdapter userAdapter;
    @BindView(R.id.img_close)
    ImageView imgClose;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.edit_search)
    EditText editSearch;
    @BindView(R.id.tv_user)
    TextView tvUser;
    @BindView(R.id.tv_region)
    TextView tvRegion;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_phone_num)
    TextView tvPhoneNum;
    @BindView(R.id.recycler_check_in)
    RecyclerView recyclerCheckIn;
    @BindView(R.id.img_fliter)
    ImageView imgFliter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_checkin);
        ButterKnife.bind(this);
        mImmersionBar = ImmersionBar.with(this)
                //解决软键盘与底部输入框冲突问题
                .keyboardEnable(true)
                //状态栏字体是深色，不写默认为亮色
                .statusBarDarkFont(true, 0.2f)
                //状态栏颜色，不写默认透明色
                .statusBarColor(R.color.dark)
                //解决状态栏和布局重叠问题，任选其一，默认为false，当为true时一定要指定statusBarColor()，不然状态栏为透明色
                .fitsSystemWindows(true);
        mImmersionBar.init();
        initView();
        initData();
    }

    private void initData() {
        List<Group> strings = new ArrayList<>();
//        for (int i = 0; i < 15; i++) {
//            Group userBean = new Group();
//            userBean.setGroup(i+"组");
//            userBean.setDoorNum(i);
//            userBean.setPlateNum(i);
//            userBean.setInfo("暂无备注");
//            strings.add(userBean);
//        }
        userAdapter = new UserAdapter(strings);
        recyclerCheckIn.setAdapter(userAdapter);
    }

    private void initView() {
        recyclerCheckIn.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @OnClick({R.id.img_close, R.id.tv_save, R.id.img_fliter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                break;
            case R.id.tv_save:
                break;
            case R.id.img_fliter:
                break;
        }
    }
}
