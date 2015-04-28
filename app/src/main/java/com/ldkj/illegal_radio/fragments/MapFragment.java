package com.ldkj.illegal_radio.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.services.LocationService;
import com.ldkj.illegal_radio.utils.Attribute;
import com.ldkj.illegal_radio.utils.LogUtils;

/**
 * 地图Fragment
 */
public class MapFragment extends Fragment implements AMap.OnMarkerClickListener {

    private static final String MAPPREFERENCESNAME = "map_preferencesname";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";
    private OnFragmentInteractionListener listener;
    private Activity activity;
    private AMap aMap;
    private MapView mapView;
    private ImageButton btn_compass;
    private ImageButton btn_delete;
    private boolean isDeleteBtnVisibility = true;
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;

    private SharedPreferences sharedPreferences;
    private LatLng latLng;

    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = activity.getSharedPreferences(MAPPREFERENCESNAME, Context.MODE_APPEND);
        registerReceiver(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View _View = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = (MapView) _View.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        initMap();
        return _View;
    }


    private void initMap() {
        aMap = mapView.getMap();
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
        aMap.getUiSettings().setLogoPosition(
                AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));

        latLng = getLastLatlon();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.ic_launcher);
        markerOptions.icon(bitmap);

        aMap.addMarker(markerOptions);
        setCompass();
    }

    private void initView(View pView) {
        btn_compass = (ImageButton) pView.findViewById(R.id.map_icon_compass);
        btn_delete = (ImageButton) pView.findViewById(R.id.map_icon_delete);
        if (!isDeleteBtnVisibility) {
            btn_delete.setVisibility(View.GONE);
        }
    }

    private void addListener() {
        btn_compass.setOnClickListener(btnOnClickListener);
        btn_delete.setOnClickListener(btnOnClickListener);
    }


    /**
     * 获取最后一次保存的经纬度
     *
     * @return
     */
    private LatLng getLastLatlon() {
        String _Lat = sharedPreferences.getString(KEY_LATITUDE, "30.67").trim();
        String _lon = sharedPreferences.getString(KEY_LONGITUDE, "104.06").trim();
        LatLng _latLng = new LatLng(Double.parseDouble(_Lat), Double.parseDouble(_lon));
        return _latLng;
    }

    /**
     * 保存经纬度
     */
    private void saveLatlon() {
        SharedPreferences.Editor _Editor = sharedPreferences.edit();
        _Editor.putString(KEY_LATITUDE, String.valueOf(latLng.latitude));
        _Editor.putString(KEY_LONGITUDE, String.valueOf(latLng.longitude));
        _Editor.commit();
    }


    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int _ViewId = v.getId();
            switch (_ViewId) {
                case R.id.map_icon_compass:
                    setCompass();
                    break;
                case R.id.map_icon_delete:
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

        try {
            listener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void registerReceiver(Activity activity) {
        localBroadcastManager = LocalBroadcastManager.getInstance(activity);
        intentFilter = new IntentFilter(LocationService.ACTION_LOCATION_CHANGE);
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    private void unRegisterReceiver() {
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    private void setCompass() {
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    final class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            latLng = intent.getParcelableExtra(LocationService.KEY_LOCATION_CHANGE);
            saveLatlon();
            LogUtils.w(Attribute.TAG, String.format("当前的位置为： %s", latLng.toString()));

        }
    }

    //————————————————————————————————重写——————————————————————————————
    @Override
    public void onDetach() {
        super.onDetach();
        unRegisterReceiver();
        listener = null;
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }
}
