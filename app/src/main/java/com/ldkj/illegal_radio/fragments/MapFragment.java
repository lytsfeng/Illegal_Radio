package com.ldkj.illegal_radio.fragments;

import android.app.Activity;
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
import com.ldkj.illegal_radio.activitys.MainActivity;
import com.ldkj.illegal_radio.events.DrawMarkerEvent;
import com.ldkj.illegal_radio.events.NetEvent;
import com.ldkj.illegal_radio.fragments.base.FragmentBase;
import com.ldkj.illegal_radio.fragments.base.OnFragmentInteractionListener;
import com.ldkj.illegal_radio.models.IllegalRadioModel;
import com.ldkj.illegal_radio.services.LocationService;
import com.ldkj.illegal_radio.utils.Attribute;
import com.ldkj.illegal_radio.utils.DateTools;
import com.ldkj.illegal_radio.views.dialogs.IllegalInfoDialog;
import com.ldkj.illegal_radio.views.dialogs.base.DialogBase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * 地图Fragment
 */
public class MapFragment extends FragmentBase implements AMap.OnMarkerClickListener, AMap.OnMapLongClickListener, DialogBase.DialogCallBack<IllegalRadioModel> {


    private OnFragmentInteractionListener listener;
    private Activity activity;
    private AMap aMap;
    private MapView mapView;
    private ImageButton btn_compass;
    private ImageButton btn_delete;
    private boolean isDeleteBtnVisibility = true;
    private SharedPreferences sharedPreferences;
    private boolean isConnDevice = false;
    private Marker positionMarker = null;
    private Map<IllegalRadioModel, Marker> Illegal_Marker_map = new HashMap<>();
    private Set<IllegalRadioModel> illegalRadioModelSet = new HashSet<>();
    private int positionMarkerIconId = -1;
    private boolean isFollow = false;

    private LatLng latLng;
    private View.OnClickListener btnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int _ViewId = v.getId();
            switch (_ViewId) {
                case R.id.map_icon_compass:
                    setCompass();
                    break;
                case R.id.map_icon_maker:
                    isFollow = !isFollow;
                    break;
                default:
                    break;
            }
        }
    };
    private boolean is = false;

    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        sharedPreferences = activity.getSharedPreferences(LocationService.MAPPREFERENCESNAME, Context.MODE_APPEND);
        latLng = getLastLatlon();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pr("onCreateView");
        View _View = inflater.inflate(R.layout.fragment_map, container, false);
        initView(_View);
        addListener();
        mapView = (MapView) _View.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        initMap();
        return _View;
    }

    @Override
    public void updateIllegal(IllegalRadioModel pModel, boolean isAdd) {
        pr("//////updateIllegal");
        synchronized (illegalRadioModelSet) {
            pr("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            if (isAdd && illegalRadioModelSet.add(pModel)) {
                pr("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                drawMark(new LatLng(pModel.lat, pModel.lon), R.mipmap.ic_maker, false, pModel);
                is = false;
                if (listener != null) {
                    listener.updateIllegal(pModel, true);
                }
            } else if (!isAdd) {
                Marker _marker = Illegal_Marker_map.get(pModel);
                if (_marker != null) {
                    _marker.remove();
                    Illegal_Marker_map.remove(pModel);
                    illegalRadioModelSet.remove(pModel);
                    listener.updateIllegal(pModel, false);
                    is = true;
                }
            }
        }
    }

    private void initMap() {
        pr("initMap");
        aMap = mapView.getMap();
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
        aMap.getUiSettings().setLogoPosition(
                AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setOnMapLongClickListener(this);
        aMap.setOnMarkerClickListener(this);
        drawMark(getLastLatlon(), ((MainActivity) activity).isConn() ? R.mipmap.runenter : R.mipmap.error, true, null);
        illegalRadioModelSet = ((MainActivity) activity).getIllegalRadioModelSet();
        initMarker();
        setCompass();
    }

    private void initView(View pView) {
        pr("initView");
        btn_compass = (ImageButton) pView.findViewById(R.id.map_icon_compass);
        btn_delete = (ImageButton) pView.findViewById(R.id.map_icon_delete);
        if (!isDeleteBtnVisibility) {
            btn_delete.setVisibility(View.GONE);
        }

    }

    private void initMarker() {
        pr("initMarker");
        for (IllegalRadioModel illegalRadioModel : illegalRadioModelSet) {
            drawMark(new LatLng(illegalRadioModel.lat, illegalRadioModel.lon), R.mipmap.ic_maker, false, illegalRadioModel);
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
        pr("onMarkerClick");
        IllegalRadioModel _Model = (IllegalRadioModel) marker.getObject();

        if (_Model != null) {
            new IllegalInfoDialog(getActivity(), _Model, this).show();
        }

        return true;
    }

    /**
     * 获取最后一次保存的经纬度
     *
     * @return
     */
    private LatLng getLastLatlon() {
        pr("getLastLatlon");
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
        pr("onEventMainThread(NetEvent netEvent)");
        Boolean _isconn = netEvent.isConn();

        if (positionMarkerIconId == (isConnDevice || _isconn ? R.mipmap.runenter : R.mipmap.error)) {
            return;
        }
        if (_isconn) {
            isConnDevice = true;
            drawMark(latLng, R.mipmap.runenter, true, null);
        } else {
            isConnDevice = false;
            if (positionMarker != null) {
            }
            drawMark(latLng, R.mipmap.error, true, null);
        }
    }

    public void onEventMainThread(LatLng latLng) {
        pr("onEventMainThread(LatLng latLng)");
        this.latLng = latLng;
        if (isConnDevice) {
            drawMark(latLng, R.mipmap.runenter, true, null);
        } else {
            drawMark(latLng, R.mipmap.error, true, null);
        }
    }

    public void onEventMainThread(DrawMarkerEvent event) {
        pr("onEventMainThread(DrawMarkerEvent event)");
        IllegalRadioModel _mode = event.getRadioModel();
        if (Illegal_Marker_map.get(_mode) == null)
            drawMark(new LatLng(_mode.lat, _mode.lon), R.mipmap.ic_maker, false, _mode);
        illegalRadioModelSet.add(_mode);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        pr("onMapLongClick");
        IllegalRadioModel _Model = new IllegalRadioModel();
        _Model.lat = latLng.latitude;
        _Model.lon = latLng.longitude;
        _Model.appeartime = DateTools.getCurrentDateString("yyyy-MM-dd HH:mm:ss");
        new IllegalInfoDialog(getActivity(), _Model, this).show();

    }

    private void drawMark(LatLng latLng, int markerID, boolean isPosition, IllegalRadioModel markerTag) {
        pr("drawMark");
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(markerID);
        markerOptions.icon(bitmap);
        if (isPosition) {
            if (positionMarker != null) {
                positionMarker.remove();
            }
            positionMarker = aMap.addMarker(markerOptions);
            positionMarker.setObject(markerTag);
            positionMarkerIconId = markerID;
            if (isFollow) {
                setCompass();
            }
        } else {
            Marker _Marker = aMap.addMarker(markerOptions);
            _Marker.setObject(markerTag);
            Illegal_Marker_map.put(markerTag, _Marker);
        }
    }

    @Override
    public void DialogBack(IllegalRadioModel radioModel, int p_ResId, Boolean isInit) {
        updateIllegal(radioModel, radioModel.handle <= 0 ? true : false);
        pr("DialogBack");
    }

    @Override
    protected void stopSelfTask() {
    }

    @Override
    protected void startNewTask() {
    }

    @Override
    public void updateData(float[] data) {
    }

    private void pr(String plog) {
        if (is) {
            Log.i(Attribute.TAG, "+++++   " + plog + "  ++++++");
        }
    }
}
