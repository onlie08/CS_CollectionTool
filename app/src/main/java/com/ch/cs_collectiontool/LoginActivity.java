package com.ch.cs_collectiontool;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ch.cs_collectiontool.bean.CollectInfo;
import com.ch.cs_collectiontool.bean.ErrorBean;
import com.ch.cs_collectiontool.bean.RequestResult;
import com.ch.cs_collectiontool.bean.User;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity {
    ImmersionBar mImmersionBar;
    @BindView(R.id.img_close)
    ImageView imgClose;
    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.btn_message)
    Button btnMessage;
    @BindView(R.id.edit_sms)
    EditText editSms;
    @BindView(R.id.constraint_login)
    ConstraintLayout constraintLogin;

    /**
     * status 代表信息采集的进度，0：未注册，1：采集村，2：采集组，3：采集户
     */
    private int status = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        String collectInfo = (String) AppPreferences.instance().get("telephone", "");
        if (TextUtils.isEmpty(collectInfo)) {
            status = 0;
        } else {
            getInfo();
        }
    }

    private void initView() {
        tvSave.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fadeIn(constraintLogin);
            }
        },2000);
        registeSMSSDK();
        initSMSButton();
    }


    @OnClick({R.id.img_close, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_close:
                finish();
                break;
            case R.id.btn_login:
                requesetLogin();
                if (TextUtils.isEmpty(editPhone.getEditableText().toString())) {
                    Toast.makeText(getApplicationContext(), "请输入手机号", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(editSms.getEditableText().toString())) {
                    Toast.makeText(getApplicationContext(), "请输入验证码", Toast.LENGTH_LONG).show();
                    return;
                }
                SMSSDK.submitVerificationCode("86", editPhone.getEditableText().toString(), editSms.getEditableText().toString());
                break;
        }
    }

    private void requesetLogin() {
        final User user = new User();
        user.setTelephone(editPhone.getEditableText().toString().trim());
        RequestBody requestBody  = FormBody.create(MediaType.parse("application/json"), new Gson().toJson(user));

        RetrofitUtil.getInstance().getApiService()
                .requestLogin(requestBody)
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
                            Toast.makeText(LoginActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            AppPreferences.instance().put("telephone",editPhone.getEditableText().toString().trim());
                            startActivity(new Intent(getApplicationContext(), EnterInfoActivity.class));
                        }

                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), String.valueOf(msg.obj), Toast.LENGTH_LONG).show();
        }
    };

    public void registeSMSSDK() {
        SMSSDK.registerEventHandler(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message message = new Message();
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // TODO 处理验证成功的结果
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        Log.i("EventHandler", "提交验证码成功");
                        message.obj = "登陆成功";
                        handler.sendMessage(message);
                        requesetLogin();
                    } else if
                    (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) { //获取验证码成功
                        message.obj = "正在获取验证码...";
                        handler.sendMessage(message);
                        Log.i("EventHandler", "正在获取验证码...");
                    } else if
                    (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                        Log.i("EventHandler", "返回支持发送验证码的国家列表");
                    }
                } else {
                    // TODO 处理错误的结果
                    try {
                        String error = String.valueOf(data);
                        error = error.substring(20);
                        Gson gson = new Gson();
                        ErrorBean errorBean = gson.fromJson(error, ErrorBean.class);
                        message.arg1 = errorBean.getStatus();
                        message.obj = errorBean.getDetail();
                        handler.sendMessage(message);
                        Log.i("EventHandler", "错误：" + errorBean.getDetail());
                    } catch (Exception e) {
                        message.obj = "未知错误";
                        handler.sendMessage(message);
                    }

                }
            }
        });
    }

    int MAX_COUNT_TIME = 30;
    Disposable mDisposable;

    private void initSMSButton() {
        Observable mObservableCountTime = RxView.clicks(btnMessage)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Object, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(Object o) throws Exception {
                        String phone = editPhone.getText().toString();
                        if (TextUtils.isEmpty(phone)) {
                            Toast.makeText(getApplicationContext(), "手机号不能为空", Toast.LENGTH_SHORT).show();
                            return Observable.empty();
                        }
                        if (phone.length() != 11) {
                            Toast.makeText(getApplicationContext(), "请填写正确的手机号", Toast.LENGTH_SHORT).show();
                            return Observable.empty();
                        }
                        // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
                        SMSSDK.getVerificationCode("86", phone);
                        return Observable.just(true);
                    }
                })
                .flatMap(new Function<Boolean, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(Boolean aBoolean) throws Exception {
                        RxView.enabled(btnMessage).accept(false);
                        RxTextView.text(btnMessage).accept("剩余 " + MAX_COUNT_TIME + " 秒");
                        return Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())
                                .take(MAX_COUNT_TIME)
                                //将递增数字替换成递减的倒计时数字
                                .map(new Function<Long, Long>() {
                                    @Override
                                    public Long apply(Long aLong) throws Exception {

                                        return MAX_COUNT_TIME - (aLong + 1);
                                    }
                                });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());

        Consumer<Long> mConsumerCountTime = new Consumer<Long>() {
            @Override
            public void accept(Long aLong) {
                //显示剩余时长。当倒计时为 0 时，还原 btn 按钮.
                try {
                    if (aLong == 0) {
                        RxView.enabled(btnMessage).accept(true);
                        RxTextView.text(btnMessage).accept("获取验证码");
                    } else {
                        RxTextView.text(btnMessage).accept("剩余 " + aLong + " 秒");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        mDisposable = mObservableCountTime.subscribe(mConsumerCountTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    public void fadeIn(View view, float startAlpha, float endAlpha, long duration) {
        if (view.getVisibility() == View.VISIBLE) return;
        view.setVisibility(View.VISIBLE);
        Animation animation = new AlphaAnimation(startAlpha, endAlpha);
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    public void fadeIn(View view) {
        fadeIn(view, 0F, 1F, 500);
        view.setEnabled(true);
    }

    public void fadeOut(View view) {
        if (view.getVisibility() != View.VISIBLE) return;
        view.setEnabled(false);
        Animation animation = new AlphaAnimation(1F, 0F);
        animation.setDuration(500);
        view.startAnimation(animation);
        view.setVisibility(View.GONE);
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
                            Toast.makeText(LoginActivity.this, value.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("caohai",new Gson().toJson(value));
                            mApplication.collectInfo = value.getResult();
                            gotoActivity(value.getResult());
                        }

                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        mDisposable.dispose();//注销
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void gotoActivity(CollectInfo collectInfo){
        status = 1;
        if (collectInfo.getVillage() != null) {
            status = 2;
            if (collectInfo.getVillage().getGroups() != null) {
                status = 3;
            }
        }
        status = 2;
        switch (status){
            case 1:
                startActivity(new Intent(getApplicationContext(), EnterInfoActivity.class));
                break;
            case 2:
                startActivity(new Intent(getApplicationContext(), UserCountActivity.class));
                break;
            case 3:
                startActivity(new Intent(getApplicationContext(), UserCheckInActivity.class));
                break;
        }
    }
}

