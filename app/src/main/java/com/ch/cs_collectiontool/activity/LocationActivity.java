package com.ch.cs_collectiontool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ch.cs_collectiontool.AppPreferences;
import com.ch.cs_collectiontool.R;
import com.sfmap.api.maps.CameraUpdate;
import com.sfmap.api.maps.CameraUpdateFactory;
import com.sfmap.api.maps.MapController;
import com.sfmap.api.maps.MapView;
import com.sfmap.api.maps.model.CameraPosition;
import com.sfmap.api.maps.model.LatLng;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationActivity extends AppCompatActivity implements MapController.OnMapLoadedListener, MapController.OnCameraChangeListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.tv_close)
    TextView tvClose;
    @BindView(R.id.map)
    MapView mapView;

    private String TAG = LocationActivity.class.getSimpleName();
    private MapController mMapController;
    private LatLng location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);// 此方法必须调用
        init();
    }

    /**
     * 初始化地图控制器
     */
    private void init() {
        tvSave.setText("确定");
        tvTitle.setText("选取位置");

        mMapController = mapView.getMap();
        //设置比例尺绝对位置， 第一个参数表示横坐标，第二个表示纵坐标
        mMapController.getUiSettings().setScaleControlsEnabled(true);
        mMapController.getUiSettings().setZoomControlsEnabled(false);
        mMapController.setOnMapLoadedListener(this);
        mMapController.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        new LatLng(34.748404, 113.670972), //113.670972,34.748404
                        18)
        );
        mMapController.setOnCameraChangeListener(this);

        LatLng latLng = (LatLng) AppPreferences.instance().get("location",null);
        if(null != latLng){
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
                AppPreferences.instance().put("location",location);
                Intent i = new Intent();
                i.putExtra("result", location.latitude+","+location.longitude);
                setResult(3, i);
                finish();
                break;
            case R.id.tv_close:
                break;
        }
    }

}
