package com.ch.cs_collectiontool;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ch.cs_collectiontool.bean.RequestResult;
import com.ch.cs_collectiontool.bean.Village;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;

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
    private String TAG = EnterInfoActivity.class.getSimpleName();
    ImmersionBar mImmersionBar;
    @BindView(R.id.img_close)
    ImageView imgClose;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.edit_name)
    EditText editName;
    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.tv_region)
    TextView tvRegion;
    @BindView(R.id.tv_address)
    EditText tvAddress;
    @BindView(R.id.webview)
    WebView webview;

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
        editPhone.setText(mApplication.collectInfo.getUser().getTelephone());
//        webview.loadUrl("https://www.baidu.com");
    }

    private void initView() {

    }

    @OnClick({R.id.img_close, R.id.tv_save, R.id.tv_region})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                break;
            case R.id.tv_save:
                if(checkInputLegal()){
                    saveVillage();
                }else {
                    Toast.makeText(getApplicationContext(),"请输入完整信息后提交",Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.tv_region:
                break;
        }
    }

    private boolean checkInputLegal(){
        return !TextUtils.isEmpty(editName.getText().toString()) && !TextUtils.isEmpty(editPhone.getText().toString()) && !TextUtils.isEmpty(tvRegion.getText().toString()) && !TextUtils.isEmpty(tvAddress.getText().toString());
    }

    private void saveVillage() {
        String telephone = (String) AppPreferences.instance().get("telephone", "");
        final Village village = new Village();
        village.setTelephone(telephone);
        village.setUid(mApplication.collectInfo.getUser().getId());
        village.setUserName(editName.getText().toString());
        village.setCounty(tvRegion.getText().toString());
        village.setVillage(tvRegion.getText().toString());
        village.setAddress(tvAddress.getText().toString());
        RequestBody requestBody  = FormBody.create(MediaType.parse("application/json"), new Gson().toJson(village));

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
                            Log.i("caohai",new Gson().toJson(value));
                            mApplication.collectInfo.setVillage(village);
                            startActivity(new Intent(getApplicationContext(),UserCountActivity.class));
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
