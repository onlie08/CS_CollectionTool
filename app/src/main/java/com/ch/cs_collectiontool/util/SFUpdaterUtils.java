package com.ch.cs_collectiontool.util;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.sf.appupdater.AppConfig;
import com.sf.appupdater.AppUpdater;
import com.sf.appupdater.Environment;
import com.sf.appupdater.appupdate.OnPackagePreparedListener;
import com.sf.appupdater.appupdate.OnVersionCheckListener;
import com.sf.appupdater.appupdate.OnVersionCheckListenerAdapter;
import com.sf.appupdater.entity.UpdateInfo;
import com.sf.appupdater.log.LogWriter;

import java.io.File;

public class SFUpdaterUtils {
    public static void initAppUpdater(Context context, String appKey, String appCode) {
        AppUpdater.sharedInstance().init(context, appKey, appCode);
    }

    public static void initAppReport() {
        AppUpdater.sharedInstance().report();
    }

    public static void setAppUpdaterInfo(Context context, String appKey, String appCode, boolean debug, Environment env, boolean tinker, LogWriter writer) {

        AppConfig config = new AppConfig.Builder()
                .context(context)
                .isMarmSit(false)//是否为移动发布平台的测试环境
                .appCode(appCode)//需要在移动发布平台上申请
                .appSecurityKey(appKey)//对应环境的移动发布平台的appkey(sdk中名称为appSecurityKey）
                // 是否debug模式（日志开关），默认false
                .debug(true)
                // 是否开启tinker热修复功能，默认false
                .tinker(false)
                // SDK日志记录接口,调用方如果不需要这些日志可以不调用此方法
                .logWriter(writer)
                .build();
        AppUpdater.sharedInstance().init(config);

    }

    public static void checkVersion(Activity activity) {
        AppUpdater.sharedInstance().checkVersion(activity);
    }

    public static void addCustomTag(String key, String vaule) {
        AppUpdater.sharedInstance().addCustomTag(key, vaule);
    }

    public static void setUserId(String userId) {
        AppUpdater.sharedInstance().setUserId(userId);
    }

    public static void setOnVersionCheckListener(final Context context) {
        AppUpdater.sharedInstance().create(context)
                .setOnVersionCheckListener(new OnVersionCheckListenerAdapter() {
                    @Override
                    public void onNoUpdate() {
                        Toast.makeText(context, "已是最新版本", Toast.LENGTH_SHORT).show();
                    }
                })
                .build().checkVersion();
    }

    public static void checkVersionOnly(final Context context) {
        AppUpdater.sharedInstance().checkVersionOnly(new OnVersionCheckListener() {
            @Override
            public void onNoUpdate() {
                Toast.makeText(context, "已是最新版本", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUpdate(UpdateInfo updateInfo) {
                Toast.makeText(context, "有新版本", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void ifHasNewVersion(final Activity context) {
        AppUpdater.sharedInstance().checkVersionOnly(new OnVersionCheckListener() {
            @Override
            public void onNoUpdate() {
//                Toast.makeText(context, "已是最新版本", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUpdate(UpdateInfo updateInfo) {
                if (updateInfo.hasNewVersion()) {
                    Toast.makeText(context, "有新版本", Toast.LENGTH_SHORT).show();

                }
                checkNewVersion(context);
            }
        });
    }

    public static void checkNewVersion(final Activity context) {
        AppUpdater.sharedInstance().create(context)
//                .setUpdatePrompter(new CustomUpdatePrompter(context))//自定义升级提示对话框
                .build().checkVersion();
    }
//
//    private static void showDisabledDialog(final Activity activity) {
//        final AlertDialogFragment newFragment = AlertDialogFragment.newInstance(R.string.caution,
//                R.string.exit_app_msg, R.string.sure, 0);
//        newFragment.setClickListener(new AlertDialogFragment.AlertDialogClickListener() {
//
//            @Override
//            public void onPositiveClick() {
//                newFragment.getDialog().cancel();
//                activity.finish();
//            }
//
//            @Override
//            public void onNegativeClick() {
//            }
//        });
//        FragmentManager m = ((MapFragmentActivity) activity).getSupportFragmentManager();
//        m.beginTransaction().add(newFragment, "dialog").commitAllowingStateLoss();
//        m.executePendingTransactions();
//    }


    public static void setOnPackagePreparedListener(final Context context) {
        AppUpdater.sharedInstance().create(context)
                .setAutoInstall(false)//手动安装（默认自动安装）
                .setOnPackagePreparedListener(new OnPackagePreparedListener() {
                    @Override
                    public void onPackagePrepared(File file) {
                        // 包准备好（下载完或增量合成完），可以安装了
                    }
                })
                .build()
                .checkVersion();
    }

}

