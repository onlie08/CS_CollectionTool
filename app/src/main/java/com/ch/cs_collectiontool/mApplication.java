package com.ch.cs_collectiontool;

import android.app.Application;
import android.content.SharedPreferences;

import com.ch.cs_collectiontool.bean.CollectInfo;
import com.gyf.barlibrary.ImmersionBar;
import com.mob.MobSDK;

import cn.smssdk.SMSSDK;

public class mApplication extends Application {
    public static CollectInfo collectInfo;
    public static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        MobSDK.init(this);
        sharedPreferences = getSharedPreferences(AppPreferences.PreferenceKey.SP_NAME_NAME,MODE_PRIVATE);
    }

}
