package com.ldkj.illegal_radio.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.ldkj.illegal_radio.events.NetEvent;
import com.ldkj.illegal_radio.services.LocationService;
import com.ldkj.illegal_radio.utils.Attribute;
import com.ldkj.illegal_radio.utils.LogUtils;

import de.greenrobot.event.EventBus;

/**
 * 地图Fragment
 */
public class MapFragment extends Fragment implements AMap.OnMarkerClickListener,AMap.OnMapLongClickListener {


    private OnFragmentInteractionListener listener;
    private Activity activity;
    private AMap aMap;
    private MapView mapView;
    private ImageButton btn_compass;
    private ImageButton btn_delete;
    private boolean isDeleteBtnVisibility = true;
    private SharedPreferences sharedPreferences;


    private LatLng latLng;
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

    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = activity.getSharedPreferences(LocationService.MAPPREFERENCESNAME, Context.MODE_APPEND);
        latLng = getLastLatlon();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View _View = inflater.inflate(R.layout.fragment_map, container, false);
        EventBus.getDefault().register(this);
        initView(_View);
        addListener();
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
        aMap.getUiSettings().setZoomControlsEnabled(false);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.runenter);
        markerOptions.icon(bitmap);
        aMap.addMarker(markerOptions);
        aMap.setOnMapLongClickListener(this);
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

    private void setCompass() {
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    /**
     * 获取最后一次保存的经纬度
     *
     * @return
     */
    private LatLng getLastLatlon() {
        String _Lat = sharedPreferences.getString(LocationService.KEY_LATITUDE, "30.67").trim();
        String _lon = sharedPreferences.getString(LocationService.KEY_LONGITUDE, "104.06").trim();
        LatLng _latLng = new LatLng(Double.parseDouble(_Lat), Double.parseDouble(_lon));
        return _latLng;
    }


    //————————————————————————————————重写——————————————————————————————
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        EventBus.getDefault().unregister(this);
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

    public void onEventMainThread(NetEvent netEvent) {
        if(netEvent.isConn()){
            Log.i(Attribute.TAG,"-------------");
        }else {

        }

        Log.i(Attribute.TAG, "-------------" + netEvent.isConn());
    }


    public void onEventMainThread(LatLng latLng){
        LogUtils.w(Attribute.TAG, String.format("MapFragment当前的位置为： %s", latLng.toString()));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.error);
        markerOptions.icon(bitmap);
        aMap.addMarker(markerOptions);
    }

}
