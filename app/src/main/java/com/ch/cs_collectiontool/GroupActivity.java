package com.ch.cs_collectiontool;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ch.cs_collectiontool.bean.Group;
import com.ch.cs_collectiontool.bean.RequestResult;
import com.ch.cs_collectiontool.bean.User;
import com.ch.cs_collectiontool.util.SFUpdaterUtils;
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

public class GroupActivity extends AppCompatActivity {
    ImmersionBar mImmersionBar;
    GroupAdapter groupAdapter;
    @BindView(R.id.tv_close)
    TextView tvClose;
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
    @BindView(R.id.btn_add_road)
    Button btnAddRoad;

    List<Group> groups = new ArrayList<>();
    private DialogInputController dialogInputController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
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
        SFUpdaterUtils.checkVersion(this);
//        initData();
    }

    @Override
    protected void onResume(){
        super.onResume();
        getInfo();
    }

    private void initData() {
        groups = mApplication.collectInfo.getVillage().getGroups();
        loadListData();
    }

    private void loadListData() {
        groupAdapter = new GroupAdapter(groups, GroupActivity.this);
        recyclerUser.setAdapter(groupAdapter);
        groupAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {
        dialogInputController = new DialogInputController(GroupActivity.this);
        dialogInputController.showEditGroupDilog(groups.get(position));
        dialogInputController.setAllGroups(groups);
        dialogInputController.setGroupListener(new DialogInputController.DialogGroupListener() {
            @Override
            public void saveItem(Group group) {
                saveGroup(group);
            }

            @Override
            public void delectItem() {
                deleteGroup(groups.get(position));
            }

            @Override
            public void updateItem(Group group) {
                saveGroup(group);
            }

        });
            }
        });
    }

    private void initView() {
        recyclerUser.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if(null == mApplication.collectInfo.getVillage()){
            return;
        }
        tvUser.setText(mApplication.collectInfo.getVillage().getUserName());
        tvRegion.setText(mApplication.collectInfo.getVillage().getCounty());
        tvAddress.setText(mApplication.collectInfo.getVillage().getAddress());
        tvPhoneNum.setText(mApplication.collectInfo.getVillage().getTelephone());
        tvSave.setVisibility(View.GONE);
    }

    @OnClick({R.id.tv_close, R.id.tv_save, R.id.btn_add_one,R.id.btn_add_road})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                finish();
                break;
            case R.id.tv_save:
                if (groups.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "请填写信息后提交", Toast.LENGTH_LONG).show();
                    return;
                }
                saveVillage();
                break;
            case R.id.btn_add_road:
//                dialogInputController1 = new DialogInputController(GroupActivity.this);
//                dialogInputController1.showEditGroupDilog1(null);
//                dialogInputController1.setGroupListener(new DialogInputController.DialogGroupListener() {
//                    @Override
//                    public void saveItem(Group group) {
//                        groups.add(group);
//                        loadListData();
//                    }
//
//                    @Override
//                    public void delectItem() {
//
//                    }
//
//                    @Override
//                    public void updateItem(Group group) {
//
//                    }
//
//                });
                break;
            case R.id.btn_add_one:
                dialogInputController = new DialogInputController(GroupActivity.this);
                dialogInputController.showEditGroupDilog(null);
                dialogInputController.setAllGroups(groups);
                dialogInputController.setGroupListener(new DialogInputController.DialogGroupListener() {
                    @Override
                    public void saveItem(Group group) {
                        saveGroup(group);
//                        groups.add(group);
//                        loadListData();
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


    private void getInfo() {
        String telephone = (String) AppPreferences.instance().get("telephone", "");
        User user = new User();
        user.setTelephone(telephone);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), new Gson().toJson(user));

        RetrofitUtil.getInstance().getApiService()
                .getInfo(requestBody)
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
                            Toast.makeText(GroupActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("caohai", new Gson().toJson(value));
                            mApplication.collectInfo = value.getResult();
                            initData();
//                            startActivity(new Intent(getApplicationContext(), RoomActivity.class));
                        }

                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(GroupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void deleteGroup(Group group){
        DeleteGroup deleteGroup = new DeleteGroup();
        deleteGroup.setId(group.getId());
        String requestString = new Gson().toJson(deleteGroup);
        Log.i("caohai", requestString);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), requestString);

        RetrofitUtil.getInstance().getApiService()
                .deleteGroup(requestBody)
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
                            Toast.makeText(GroupActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("caohai", new Gson().toJson(value));
                            getInfo();
                        }
                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onError(Throwable e) {
//                        Toast.makeText(GroupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void saveVillage() {
        for (int i = 0; i < groups.size(); i++) {
            groups.get(i).setRooms(null);
        }
        requestGroup requestGroup = new requestGroup();
        requestGroup.setVid(mApplication.collectInfo.getVillage().getId());
        requestGroup.setGroups(groups);
        String requestString = new Gson().toJson(requestGroup);
        Log.i("caohai", requestString);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), requestString);

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
                            Toast.makeText(GroupActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("caohai", new Gson().toJson(value));
                            getInfo();
                        }
                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onError(Throwable e) {
//                        Toast.makeText(GroupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void saveGroup(Group group) {
        group.setVid(mApplication.collectInfo.getVillage().getId());
        String requestString = new Gson().toJson(group);
        Log.i("caohai", requestString);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), requestString);

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
                        Toast.makeText(GroupActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                        if (value.getCode() == 0) {
                            Log.i("caohai", new Gson().toJson(value));
                            getInfo();
                        }
                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onError(Throwable e) {
                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }



    class DeleteGroup{
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    class requestGroup {
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

    class GroupJson {
        private List<Group> groups;

        public List<Group> getGroups() {
            return groups;
        }

        public void setGroups(List<Group> groups) {
            this.groups = groups;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (requestCode == 99 && null != data) {
            String result = data.getStringExtra("result");
            if(null != dialogInputController){
                dialogInputController.setLocation(result);
            }
            Toast.makeText(getApplicationContext(),"坐标选取成功",Toast.LENGTH_LONG).show();
        }
    }
}
