package com.ch.cs_collectiontool.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ch.cs_collectiontool.AppPreferences;
import com.ch.cs_collectiontool.R;
import com.gyf.barlibrary.ImmersionBar;
import com.sfmap.api.location.SfMapLocation;
import com.sfmap.api.location.SfMapLocationClient;
import com.sfmap.api.location.SfMapLocationClientOption;
import com.sfmap.api.location.SfMapLocationListener;
import com.sfmap.api.maps.CameraUpdate;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.BitmapDescriptor;
import com.sfmap.api.maps.model.BitmapDescriptorFactory;
import com.sfmap.api.maps.model.CameraPosition;
import com.sfmap.api.maps.model.LatLng;
import com.sfmap.api.maps.model.Marker;
import com.sfmap.api.maps.model.MarkerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mob.MobSDK.getContext;

public class LocationActivity extends AppCompatActivity implements MapController.OnMapLoadedListener, MapController.OnCameraChangeListener, SfMapLocationListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.tv_close)
    TextView tvClose;
    @BindView(R.id.map)
    MapView mapView;

    ImmersionBar mImmersionBar;
    private String TAG = LocationActivity.class.getSimpleName();
    private MapController mMapController;
    private LatLng location;
    private Marker locMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
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
        mapView.onCreate(savedInstanceState);// 此方法必须调用
        init();
        startLocation();
    }

    /**
     * 初始化地图控制器
     */
    private void init() {
        tvSave.setText("确定");
        tvTitle.setText("选取位置");
        tvClose.setVisibility(View.GONE);
        mMapController = mapView.getMap();
        //设置比例尺绝对位置， 第一个参数表示横坐标，第二个表示纵坐标
        mMapController.getUiSettings().setScaleControlsEnabled(true);
        mMapController.getUiSettings().setZoomControlsEnabled(false);
        mMapController.setOnMapLoadedListener(this);
        mMapController.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        new LatLng(28.374132, 112.813891), //112.813891,28.374132
                        18)
        );
        mMapController.setOnCameraChangeListener(this);

        String lat = getIntent().getStringExtra("location");
        if(TextUtils.isEmpty(lat)){
            lat = (String) AppPreferences.instance().get("location","");
        }
        if(!TextUtils.isEmpty(lat)){
            String[] loc = lat.split(",");
            LatLng latLng = new LatLng(Double.parseDouble(loc[0]),Double.parseDouble(loc[1]));
            mMapController.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
            mMapController.moveCamera(CameraUpdateFactory.zoomTo(18));
        }
    }

    /**
     * 方法重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        stopLocation();
        if (null != locationClient) {
            locationClient.destroy();
            locationClient = null;
            locationOption = null;
        }
    }

    @Override
    public void onMapLoaded() {
        location = mMapController.getCameraPosition().target;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        location = cameraPosition.target;
    }

    @OnClick({R.id.tv_save, R.id.tv_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_save:
                if(null == location){
                    return;
                }
                AppPreferences.instance().put("location",location.latitude+","+location.longitude);
                Intent i = new Intent();
                i.putExtra("result", location.latitude+","+location.longitude);
                setResult(3, i);
                finish();
                break;
            case R.id.tv_close:
                if(null == location){
                    return;
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        AppPreferences.instance().put("location",location.latitude+","+location.longitude);
        Intent i1 = new Intent();
        i1.putExtra("result", location.latitude+","+location.longitude);
        setResult(3, i1);
        super.onBackPressed();//注释掉这行,back键不退出activity
    }

    private SfMapLocationClient locationClient = null;
    private SfMapLocationClientOption locationOption = null;
    private boolean firstIn = true;
    public void startLocation() {
        if(locationClient==null){
            locationClient = new SfMapLocationClient(this.getApplicationContext());
            locationOption = new SfMapLocationClientOption();
            // 设置定位模式为低功耗模式
            locationOption.setLocationMode(SfMapLocationClientOption.SfMapLocationMode.High_Accuracy);
            // 设置定位间隔为2秒
            locationOption.setInterval(1 * 1000);
            locationOption.setNeedAddress(false);
//            locationOption.setUseGjc02(false);
            // 设置定位监听
            locationClient.setLocationListener(this);
            locationClient.setLocationOption(locationOption);
            locationClient.startLocation();
        }
    }

    public void stopLocation() {
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient = null;
        }
    }

    @Override
    public void onLocationChanged(SfMapLocation location) {
        if(location!=null&&location.getErrorCode()!=0){
            //定位发生异常
            Log.e("LocationChangeError", "Location error code：" + location.getErrorCode());
//            tvLocation.setText("LocationChangeError:"+ location.getErrorCode()+"time:"+location.getTime());
            return ;
        }
        if(firstIn){
            mMapController.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(),location.getLongitude()), //112.813891,28.374132
                            18)
            );
            firstIn = false;
        }
        refreshLocMarker(location);
    }

    private void refreshLocMarker(SfMapLocation location) {
        if(locMarker == null ){
            mMapController.addMarker(new MarkerOptions().anchor(0.5f,0.5f).position(new LatLng(location.getLatitude(),location.getLongitude()))
                    .icon(getBitmapDescriptor(R.mipmap.navi_map_gps_locked)));
        }else {
            locMarker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
        }
    }

    private BitmapDescriptor getBitmapDescriptor(int resId) {
        return BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getContext().getResources(), resId));
    }
}
