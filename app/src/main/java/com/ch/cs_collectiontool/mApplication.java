package com.ch.cs_collectiontool;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.ch.cs_collectiontool.bean.CollectInfo;
import com.ch.cs_collectiontool.util.SFUpdaterUtils;
import com.mob.MobSDK;
import com.sf.appupdater.log.LogInfo;
import com.sf.appupdater.log.LogWriter;


public class mApplication extends Application {
    public static CollectInfo collectInfo;
    public static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        MobSDK.init(this);
        ConfigerHelper.getInstance(this);
        sharedPreferences = getSharedPreferences(AppPreferences.PreferenceKey.SP_NAME_NAME,MODE_PRIVATE);
        LogWriter logWriter = new LogWriter() {
            @Override
            public void write(LogInfo logInfo) {
                Log.d("logInfo", "logInfo: " + logInfo);
            }
        };
        SFUpdaterUtils.setAppUpdaterInfo(this,"34b85cc09353b8424354ef99ee64294f","dd260b372e5343dfb219575527e7c061",true, com.sf.appupdater.Environment.PRODUCTION,false,logWriter);

    }


}
