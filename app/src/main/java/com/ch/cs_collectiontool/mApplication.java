package com.ch.cs_collectiontool;

import android.app.Application;
import android.content.SharedPreferences;

import com.ch.cs_collectiontool.bean.CollectInfo;
import com.mob.MobSDK;


public class mApplication extends Application {
    public static CollectInfo collectInfo;
    public static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        MobSDK.init(this);
        ConfigerHelper.getInstance(this);
        sharedPreferences = getSharedPreferences(AppPreferences.PreferenceKey.SP_NAME_NAME,MODE_PRIVATE);
    }


}
