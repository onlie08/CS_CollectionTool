package com.ch.cs_collectiontool;

import android.content.Context;

import com.ch.cs_collectiontool.bean.CsvJsonBean;
import com.ch.cs_collectiontool.bean.Region;
import com.ch.cs_collectiontool.bean.Street;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ConfigerHelper {
    private Context mContext;
    private static final String AREA_FILE = "area_changsha.csv";//武汉深圳网点边界数据
//    public List<String> villages = new ArrayList<>();
    public List<String> dates = new ArrayList<>();

    public CsvJsonBean csvJsonBean = new CsvJsonBean();
    public CsvJsonBean getCsvJsonBean() {
        return csvJsonBean;
    }

    private static ConfigerHelper instance = null;
    public static ConfigerHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ConfigerHelper(context);
        }
        return instance;
    }

    private ConfigerHelper(Context context) {
        mContext = context;
        readAreaBelong1();
        dealWithData();
        csvJsonBean.setRegions(regions);
    }

    public void readAreaBelong1() {
        try {
            InputStream tmpStream = GetAreaFile();
            BufferedReader bufReader = new BufferedReader(
                    new InputStreamReader(tmpStream, "UTF-8"));
            String str;
            while ((str = bufReader.readLine()) != null) {
                if (str != null && str.length() > 0) {
                    String[] strings = str.split(",");
                    if(!"行政村".equals(strings[2])){
                        dates.add(strings[0].trim() +","+ strings[1].trim() +","+ strings[2].trim());
                    }
                }
            }
            bufReader.close();
            tmpStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void readAreaBelong() {
//        try {
//            InputStream tmpStream = GetAreaFile();
//            BufferedReader bufReader = new BufferedReader(
//                    new InputStreamReader(tmpStream, "UTF-8"));
//            String str;
//            while ((str = bufReader.readLine()) != null) {
//                if (str != null && str.length() > 0) {
//                    String[] strings = str.split(",");
//                    if(!"行政村".equals(strings[2])){
//                        villages.add(strings[1].trim() + strings[2].trim());
//                    }
//                }
//            }
//            bufReader.close();
//            tmpStream.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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

    int pos = 0;
    List<Region> regions = new ArrayList<>();
    private void dealWithData(){
        Region region = new Region();
        List<Street> streets = new ArrayList<>();

        for(int i= pos;i<dates.size();i++){
            String[] tip = dates.get(i).split(",");
            if(i == pos ){
                region.setRegionName(tip[0]);

                Street street = new Street();
                street.setStreetName(tip[1]);
                street.setVillages(tip[2]+",");

                streets.add(street);
                region.setStreets(streets);
                regions.add(region);

            }else {
                if(tip[0].equals(region.getRegionName())){
                    if(tip[1].equals(streets.get(streets.size()-1).getStreetName())){
                        String vill = streets.get(streets.size()-1).getVillages() + tip[2] + ",";
                        streets.get(streets.size()-1).setVillages(vill);
                    }else {
                        Street street = new Street();
                        street.setStreetName(tip[1]);
                        street.setVillages(tip[2]+",");

                        streets.add(street);
                        region.setStreets(streets);
                    }

                }else {
                    pos = i;
                    dealWithData();
                    break;

//                    Region region = new Region();
//                    region.setRegionName(tip[0]);
//
//                    Street street = new Street();
//                    street.setStreetName(tip[1]);
//                    street.setVillages(tip[2]+",");
//
//                    streets.add(street);
//                    region.setStreets(streets);
//                    regions.add(region);
                }
            }

        }

    }

}
