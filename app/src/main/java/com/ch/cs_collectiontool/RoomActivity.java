package com.ch.cs_collectiontool;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ch.cs_collectiontool.bean.Group;
import com.ch.cs_collectiontool.bean.RequestResult;
import com.ch.cs_collectiontool.bean.Room;
import com.ch.cs_collectiontool.bean.User;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.Collections;
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

public class RoomActivity extends AppCompatActivity {
    ImmersionBar mImmersionBar;
    RoomAdapter roomAdapter;
    @BindView(R.id.tv_close)
    TextView tvClose;
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

    private List<Room> allRooms = new ArrayList<>();
    private List<Room> fliteRooms = new ArrayList<>();
    private Group editGroup;
    private DialogInputController dialogInputController;

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
        editGroup = (Group)getIntent().getSerializableExtra("group");

        initView();
        initData();
    }

    private void initData() {
        if(null == editGroup){
            return;
        }
        if(editGroup != null){
            initList(editGroup);
            fillDateToList(editGroup);
        }else {
            Toast.makeText(getApplicationContext(),"信息填写不正确，已退出", Toast.LENGTH_LONG).show();
        }
    }

    private void initList(Group group){
        int roomsNum = group.getRoomNum();
        List<Room> rooms = new ArrayList<>();
        for(int i=0;i<roomsNum;i++){
            Room temp = new Room();
            temp.setGid(group.getId());
            temp.setBelongGroup(group.getGroupName());
            temp.setRoomNo(i + 1);
            rooms.add(temp);
        }
        allRooms.addAll(rooms);

        loadList(allRooms);
    }

    private void loadList(final List<Room> allRooms){
        roomAdapter = new RoomAdapter(allRooms);
        roomAdapter.removeAllFooterView();
        recyclerCheckIn.setAdapter(roomAdapter);
        roomAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                dialogInputController = new DialogInputController(RoomActivity.this);
                dialogInputController.showEditRoomDilog(allRooms.get(position));
                dialogInputController.setRoomListener(new DialogInputController.DialogRoomListener() {
                    @Override
                    public void saveItem(Room room) {
                        saveRoomInfo(room);
                    }
                });

            }
        });
    }

    private void fillDateToList(Group group){
        if(group.getRooms() != null && group.getRooms().size()>0){
            for(Room room : group.getRooms()){
                int roomNo = room.getRoomNo();
                for(int i=0;i<allRooms.size();i++){
                    if(allRooms.get(i).getRoomNo() == roomNo){
                        allRooms.get(i).setOwnerName(room.getOwnerName());
                        allRooms.get(i).setRemark(room.getRemark());
                        allRooms.get(i).setReserve(room.isReserve());
                    }
                }
            }
        }

        roomAdapter.notifyDataSetChanged();
    }


    private void initView() {
        recyclerCheckIn.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        tvUser.setText(mApplication.collectInfo.getVillage().getUserName());
        tvRegion.setText(mApplication.collectInfo.getVillage().getCounty());
        tvAddress.setText(mApplication.collectInfo.getVillage().getAddress());
        tvPhoneNum.setText(mApplication.collectInfo.getVillage().getTelephone());

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!TextUtils.isEmpty(s.toString())){
                    fliteRooms.clear();
                    for(Room room : allRooms){
                        if(null != room.getOwnerName() && room.getOwnerName().contains(s.toString().trim())){
                            fliteRooms.add(room);
                        }
                    }
                    loadList(fliteRooms);
                }else {
                    loadList(allRooms);
                }
            }
        });
    }

    @OnClick({R.id.tv_close, R.id.tv_save, R.id.img_fliter})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                finish();
                break;
            case R.id.tv_save:
                Toast.makeText(getApplicationContext(),"提交成功", Toast.LENGTH_LONG).show();
                break;
            case R.id.img_fliter:
                if(imgFliter.isSelected()){
                    imgFliter.setSelected(false);
                }else {
                    imgFliter.setSelected(true);
                }
                Collections.reverse(allRooms);
                roomAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void getInfo() {
        String telephone = (String) AppPreferences.instance().get("telephone", "");
        User user = new User();
        user.setTelephone(telephone);
        RequestBody requestBody  = FormBody.create(MediaType.parse("application/json"), new Gson().toJson(user));

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
                            Toast.makeText(RoomActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("caohai",new Gson().toJson(value));
                            mApplication.collectInfo = value.getResult();
                        }

                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(RoomActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void saveRoomInfo(Room room) {
        String requestString = new Gson().toJson(room);
        Log.i("caohai",requestString);
        RequestBody requestBody  = FormBody.create(MediaType.parse("application/json"),requestString);

        RetrofitUtil.getInstance().getApiService()
                .saveRoom(requestBody)
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
                            Toast.makeText(RoomActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("caohai",new Gson().toJson(value));
                            Toast.makeText(RoomActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                            roomAdapter.notifyDataSetChanged();
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

}
