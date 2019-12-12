package com.ch.cs_collectiontool;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ConfigerHelper {
    private Context mContext;
    private static final String AREA_FILE = "area_changsha.csv";//武汉深圳网点边界数据
    public List<String> villages = new ArrayList<>();

    private static ConfigerHelper instance = null;
    public static ConfigerHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ConfigerHelper(context);
        }
        return instance;
    }

    private ConfigerHelper(Context context) {
        mContext = context;
        readAreaBelong();
    }

    public void readAreaBelong() {
        try {
            InputStream tmpStream = GetAreaFile();
            BufferedReader bufReader = new BufferedReader(
                    new InputStreamReader(tmpStream, "UTF-8"));
            String str;
            while ((str = bufReader.readLine()) != null) {
                if (str != null && str.length() > 0) {
                    String[] strings = str.split(",");
                    if(!"行政村".equals(strings[2])){
                        villages.add(strings[1].trim() + strings[2].trim());
                    }
                }
            }
            bufReader.close();
            tmpStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取配置文件
     *
     * @return
     */
    public InputStream GetAreaFile() {
        InputStream tmpStream = null;
        try {
            tmpStream = mContext.getResources().getAssets()
                    .open(AREA_FILE);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return mContext.getResources().getAssets()
                        .open(AREA_FILE);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return tmpStream;
    }
}
