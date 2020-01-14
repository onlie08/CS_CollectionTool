package com.ch.cs_collectiontool;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ch.cs_collectiontool.bean.CsvJsonBean;
import com.ch.cs_collectiontool.bean.Region;
import com.ch.cs_collectiontool.bean.RequestResult;
import com.ch.cs_collectiontool.bean.Street;
import com.ch.cs_collectiontool.bean.User;
import com.ch.cs_collectiontool.bean.Village;
import com.ch.cs_collectiontool.view.SpinnerController;
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

public class EnterInfoActivity extends AppCompatActivity {
    @BindView(R.id.tv_close)
    TextView tvClose;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.edit_name)
    EditText editName;
    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.tv_region)
    TextView tvRegion;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.webview)
    WebView webview;

    ImmersionBar mImmersionBar;
    private String TAG = EnterInfoActivity.class.getSimpleName();
    private int selectPos = 0;
    private CsvJsonBean csvJsonBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_info);
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
        csvJsonBean = ConfigerHelper.getInstance(this).getCsvJsonBean();
        if (null == mApplication.collectInfo) {
            return;
        }
        editPhone.setText(mApplication.collectInfo.getUser().getTelephone());
        if(null != mApplication.collectInfo.getVillage()){
            editName.setText(mApplication.collectInfo.getVillage().getUserName());
            editPhone.setText(mApplication.collectInfo.getVillage().getTelephone());
            tvRegion.setText(mApplication.collectInfo.getVillage().getCounty());
            tvAddress.setText(mApplication.collectInfo.getVillage().getAddress());
        }

    }

    private void initView() {

    }

    @OnClick({R.id.tv_close, R.id.tv_save, R.id.tv_region, R.id.tv_address})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                finish();
                break;
            case R.id.tv_region:
                final List<String> regions = getRegions(csvJsonBean.getRegions());
                SpinnerController spinnerRegion = new SpinnerController(EnterInfoActivity.this);
                spinnerRegion.showSpinnerDialog(regions);
                spinnerRegion.setListener(new SpinnerController.SpinnerListener() {
                    @Override
                    public void selectResult(String date) {
                        selectPos = regions.indexOf(date);
                        tvRegion.setText(date);
                        tvAddress.setText("");

                    }
                });
                break;
            case R.id.tv_address:
                final List<String> streets = getStreets(csvJsonBean.getRegions().get(selectPos).getStreets());
                SpinnerController spinnerStreets = new SpinnerController(EnterInfoActivity.this);
                spinnerStreets.showSpinnerDialog(streets);
                spinnerStreets.setListener(new SpinnerController.SpinnerListener() {
                    @Override
                    public void selectResult(final String date) {
                        int pos = streets.indexOf(date);
                        final List<String> villages = getVillages(csvJsonBean.getRegions().get(selectPos).getStreets().get(pos));
                        SpinnerController spinnerVillages = new SpinnerController(EnterInfoActivity.this);
                        spinnerVillages.showSpinnerDialog(villages);
                        spinnerVillages.setListener(new SpinnerController.SpinnerListener() {
                            @Override
                            public void selectResult(String date1) {
                                tvAddress.setText(date +" "+ date1);
                            }
                        });
                    }
                });
                break;
            case R.id.tv_save:
                if (checkInputLegal()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EnterInfoActivity.this);
                    View alarm = getLayoutInflater().inflate(R.layout.dialog_alarm_layout,null);
                    builder.setView(alarm);
                    builder.setCancelable(true);

                    final Dialog dialog = builder.create();
                    dialog.show();

                    Button btn_report = alarm.findViewById(R.id.btn_report);
                    btn_report.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveVillage();
                            dialog.dismiss();
                        }
                    });
                    Button btn_cancel = alarm.findViewById(R.id.btn_cancel);
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "请输入完整信息后提交", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }

    private List<String> getVillages(Street street) {
        List<String> results = new ArrayList<>();
        String strings[] = street.getVillages().split(",");
        for (int i=0;i<strings.length;i++){
            results.add(strings[i]);
        }
        return results;
    }

    private List<String> getStreets(List<Street> streets) {
        List<String> results = new ArrayList<>();
        for (Street street : streets){
            results.add(street.getStreetName());
        }
        return results;
    }

    private List<String> getRegions(List<Region> regions) {
        List<String> results = new ArrayList<>();
        for(Region region : regions){
            results.add(region.getRegionName());
        }
        return results;
    }

    private boolean checkInputLegal() {
        return !TextUtils.isEmpty(editName.getText().toString()) && !TextUtils.isEmpty(editPhone.getText().toString()) && !TextUtils.isEmpty(tvRegion.getText().toString()) && !TextUtils.isEmpty(tvAddress.getText().toString());
    }

    private void saveVillage() {
        final Village village = new Village();
        village.setUid(mApplication.collectInfo.getUser().getId());
        village.setUserName(editName.getText().toString());
        village.setTelephone(editPhone.getText().toString().trim());
        village.setProvince("湖南省");
        village.setCity("长沙市");
        village.setCounty(tvRegion.getText().toString());
        String add = tvAddress.getText().toString();
        String adds[] = add.split("\\s+");
        village.setTown(adds[0]);
        village.setVillage(adds[1]);
        village.setAddress(add);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), new Gson().toJson(village));

        RetrofitUtil.getInstance().getApiService()
                .saveVillage(requestBody)
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
                            Toast.makeText(EnterInfoActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("caohai", new Gson().toJson(value));
                            getInfo();
                        }
                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(EnterInfoActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
                            Toast.makeText(EnterInfoActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("caohai", new Gson().toJson(value));
                            mApplication.collectInfo = value.getResult();
                            startActivity(new Intent(getApplicationContext(), GroupActivity.class));
                            finish();
                        }

                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(EnterInfoActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


}
