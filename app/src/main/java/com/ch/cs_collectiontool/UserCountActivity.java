package com.ch.cs_collectiontool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ch.cs_collectiontool.bean.Group;
import com.ch.cs_collectiontool.bean.RequestResult;
import com.ch.cs_collectiontool.bean.User;
import com.ch.cs_collectiontool.bean.Village;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class UserCountActivity extends AppCompatActivity {
    ImmersionBar mImmersionBar;
    UserAdapter userAdapter;
    @BindView(R.id.img_close)
    ImageView imgClose;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.tv_user)
    TextView tvUser;
    @BindView(R.id.tv_region)
    TextView tvRegion;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_phone_num)
    TextView tvPhoneNum;
    @BindView(R.id.recycler_user)
    RecyclerView recyclerUser;
    @BindView(R.id.btn_add_one)
    Button btnAddOne;

    List<Group> groups = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_count);
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
        String groupsJson = (String)AppPreferences.instance().get("groups","");
        if(TextUtils.isEmpty(groupsJson)){
            return;
        }
        GroupJson group = new Gson().fromJson(groupsJson,GroupJson.class);
        groups = group.getGroups();
        loadListData();
    }

    private void loadListData(){
        GroupJson groupJson = new GroupJson();
        groupJson.setGroups(groups);
        AppPreferences.instance().put("groups",new Gson().toJson(groupJson));
        userAdapter = new UserAdapter(groups);
        recyclerUser.setAdapter(userAdapter);
        userAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
                DialogInputController dialogInputController = new DialogInputController(UserCountActivity.this);
                dialogInputController.showAddNewDilog(groups.get(position));
                dialogInputController.setListener(new DialogInputController.DialogListener() {
                    @Override
                    public void saveItem(Group group) {
                        groups.add(group);
                        loadListData();

                    }

                    @Override
                    public void delectItem() {
                        groups.remove(position);
                        loadListData();
                    }

                    @Override
                    public void updateItem(Group group) {
                        groups.get(position).setGroupName(group.getGroupName());
                        groups.get(position).setDoorNum(group.getDoorNum());
                        groups.get(position).setRoomNum(group.getRoomNum());
                        groups.get(position).setRemark(group.getRemark());
                        loadListData();
                    }
                });
            }
        });
    }

    private void initView() {
        recyclerUser.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        tvUser.setText(mApplication.collectInfo.getVillage().getUserName());
        tvRegion.setText(mApplication.collectInfo.getVillage().getCounty());
        tvAddress.setText(mApplication.collectInfo.getVillage().getAddress());
        tvPhoneNum.setText(mApplication.collectInfo.getVillage().getTelephone());
    }

    @OnClick({R.id.img_close, R.id.tv_save,R.id.btn_add_one})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                finish();
                break;
            case R.id.tv_save:
                if(groups.isEmpty()){
                    Toast.makeText(getApplicationContext(),"请填写信息后提交",Toast.LENGTH_LONG).show();
                    return;
                }
                saveVillage();
                break;
            case R.id.btn_add_one:
                DialogInputController dialogInputController = new DialogInputController(UserCountActivity.this);
                dialogInputController.showAddNewDilog(null);
                dialogInputController.setListener(new DialogInputController.DialogListener() {
                    @Override
                    public void saveItem(Group group) {
                        groups.add(group);
                        loadListData();
                    }

                    @Override
                    public void delectItem() {

                    }

                    @Override
                    public void updateItem(Group group) {

                    }
                });
                break;
        }
    }


//    private void getInfo() {
//        String telephone = (String) AppPreferences.instance().get("telephone", "");
//        User user = new User();
//        user.setTelephone(telephone);
//        RequestBody requestBody  = FormBody.create(MediaType.parse("application/json"), new Gson().toJson(user));
//
//        RetrofitUtil.getInstance().getApiService()
//                .getInfo(requestBody)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<RequestResult>() {
//                    private Disposable mDisposable;
//
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        mDisposable = d;
//                    }
//
//                    @Override
//                    public void onNext(RequestResult value) {
//                        if (value.getCode() != 0) {
//                            Toast.makeText(UserCountActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
//                        } else {
//                            Log.i("caohai",new Gson().toJson(value));
//                            mApplication.collectInfo = value.getResult();
//                            groups = mApplication.collectInfo.getVillage().getGroups();
//                            loadListData();
//                        }
//
//                        mDisposable.dispose();//注销
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Toast.makeText(UserCountActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
//                        mDisposable.dispose();//注销
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
//    }

    private void saveVillage() {
        for(int i=0;i<groups.size();i++){
            groups.get(i).setRooms(null);
        }
        requestGroup requestGroup = new requestGroup();
        requestGroup.setVid(mApplication.collectInfo.getVillage().getUid());
        requestGroup.setGroups(groups);
        String requestString = new Gson().toJson(requestGroup);
        Log.i("caohai",requestString);
        RequestBody requestBody  = FormBody.create(MediaType.parse("application/json"),requestString);

        RetrofitUtil.getInstance().getApiService()
                .saveGroup(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RequestResult>() {
                    private Disposable mDisposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(RequestResult value) {
                        if (value.getCode() != 0) {
                            Toast.makeText(UserCountActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("caohai",new Gson().toJson(value));
                            mApplication.collectInfo.getVillage().setGroups(groups);
                            startActivity(new Intent(getApplicationContext(),UserCheckInActivity.class));
                        }
                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onError(Throwable e) {
//                        Toast.makeText(UserCountActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    class requestGroup{
        private int vid;
        private List<Group> groups;

        public int getVid() {
            return vid;
        }

        public void setVid(int vid) {
            this.vid = vid;
        }

        public List<Group> getGroups() {
            return groups;
        }

        public void setGroups(List<Group> groups) {
            this.groups = groups;
        }
    }

    class GroupJson{
        private List<Group> groups;

        public List<Group> getGroups() {
            return groups;
        }

        public void setGroups(List<Group> groups) {
            this.groups = groups;
        }
    }
}
