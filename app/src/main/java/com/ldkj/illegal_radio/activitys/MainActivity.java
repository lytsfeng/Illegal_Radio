package com.ldkj.illegal_radio.activitys;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.ldkj.illegal_radio.R;
import com.ldkj.illegal_radio.activitys.base.ActivityFrame;
import com.ldkj.illegal_radio.events.DrawMarkerEvent;
import com.ldkj.illegal_radio.events.NetEvent;
import com.ldkj.illegal_radio.events.ThresholdEvent;
import com.ldkj.illegal_radio.fragments.ScanFragment;
import com.ldkj.illegal_radio.fragments.SingleFragment2;
import com.ldkj.illegal_radio.fragments.base.FragmentBase;
import com.ldkj.illegal_radio.fragments.base.MenuLeftFragment;
import com.ldkj.illegal_radio.fragments.base.OnFragmentInteractionListener;
import com.ldkj.illegal_radio.models.IllegalRadioModel;
import com.ldkj.illegal_radio.models.Single;
import com.ldkj.illegal_radio.services.LocationService;
import com.ldkj.illegal_radio.services.WorkService;
import com.ldkj.illegal_radio.utils.Attribute;
import com.ldkj.illegal_radio.utils.DateTools;
import com.ldkj.illegal_radio.utils.Utils;
import com.ldkj.illegal_radio.utils.databases.base.SQLiteDALBase;
import com.ldkj.illegal_radio.utils.databases.sqlitedal.SQLiteDALIllegalRadio;
import com.ldkj.illegal_radio.views.dialogs.DownLoadAndInstallApkDialog;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

public class MainActivity extends ActivityFrame implements OnFragmentInteractionListener, MenuLeftFragment.MenuOnItemClickListener, TextToSpeech.OnInitListener {

    private static final String CONFIG_KEY_FRAGMENT = "fragment";
    protected TextToSpeech speech;
    private int currentFragmentID = 0;
    private FragmentBase currentFragment = null;
    private WorkService workService;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private SharedPreferences preferences;
    private boolean isConn = false;
    private TaskThread taskThread = null;
    private SQLiteDALBase<IllegalRadioModel> illegalRadioModelSQLiteDALBase;
    private Set<IllegalRadioModel> mainIllegalRadioModelSet = new HashSet<>();
    private int threshold = 30;
    private boolean isTaskRuning = false;
    private Attribute.TASKTYPE tasktype = Attribute.TASKTYPE.NO;
    private Attribute.SOUNDTYPE soundtype = Attribute.SOUNDTYPE.DEVICE;



    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            workService = ((WorkService.LocalBinder) service).getService();
            workService.bindActivity(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            workService = null;
        }
    };
    private LatLng oldLatlng = null;

    @Override
    protected FragmentBase getFragment(int position) {
        return super.getFragment(position);
    }

    private Queue<Double> levels = new ArrayDeque<Double>();
    private int avgCount = 1;
    private double levelSum = 0;
    private Timer singleTimer = null;

    public Set<IllegalRadioModel> getMainIllegalRadioModelSet() {
        return mainIllegalRadioModelSet;
    }

    public Boolean isConn() {
        return isConn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentFragmentID = preferences.getInt(CONFIG_KEY_FRAGMENT, 0);
        if (savedInstanceState != null) {
            currentFragmentID = savedInstanceState.getInt(CONFIG_KEY_FRAGMENT);
        }
        initView();
        bindData();
        openFragment(currentFragmentID, false);
    }

    private void initView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.id_drawerlayout);
        toolbar = (Toolbar) findViewById(R.id.id_toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        actionBarDrawerToggle.syncState();
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        illegalRadioModelSQLiteDALBase = new SQLiteDALIllegalRadio(this);
        startTTS();
    }

    private void bindData() {
        List<IllegalRadioModel> _date = illegalRadioModelSQLiteDALBase.queryAll("and handle < 1");
        mainIllegalRadioModelSet.clear();
        mainIllegalRadioModelSet.addAll(_date);
    }

    @Override
    protected void onResume() {
        openFragment(currentFragmentID, false);
        bindService(new Intent(this, WorkService.class), connection, BIND_AUTO_CREATE);
        startService(LocationService.class);
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopService(LocationService.class);
        unbindService(connection);
        removeFreagment(R.id.main_fragment);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        workService.stopWork();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CONFIG_KEY_FRAGMENT, currentFragmentID);
    }

    // 事件接收
    public void onupdate(float[] data, Attribute.DATATYPE datatype) {
        synchronized (currentFragment) {
            try {
                if (currentFragment != null) {
                    if (currentFragment instanceof ScanFragment) {
                        currentFragment.updateData(data, datatype);
                    }
                    if (datatype == Attribute.DATATYPE.IQDATA) {
                        double _leve = Utils.calculateLevelfromIQ(data);
                        EventBus.getDefault().post(getLevelAvg(_leve));
                    }
                }
            } catch (Exception e) {
                Log.i("sssss", "sssssssssssssssssssssssssssssss" + e.toString());
            }
        }
    }

    public void onEventMainThread(String _leve) {
        if (speech != null && !speech.isSpeaking() && soundtype == Attribute.SOUNDTYPE.VOICE) {
            speech.setPitch(1f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
            speech.speak(_leve,
                    TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private String getLevelAvg(double plevel) {

        if (levelSum == 0) {
            levels.clear();
        }
        if (avgCount == 1) {
            return String.format("%.0f", plevel);
        }
        double _avg = 0;
        int _listSize = levels.size();
        if (_listSize >= avgCount) {
            double _tmp = levels.poll();
            levelSum -= _tmp;
            _listSize--;
        }
        levels.add(plevel);
        levelSum += plevel;
        _listSize += 1;
        return String.format("%.0f", levelSum / _listSize);
    }

    public void onEventMainThread(Integer avgCount) {
        this.avgCount = avgCount;
        levelSum = 0;
    }

    public void onEventMainThread(DrawMarkerEvent event) {
        IllegalRadioModel _mode = updateIllegal(event.getRadioModel(), Attribute.OPERATION_TYPE.INSTER);
        if (_mode == null) {
            return;
        }
        currentFragment.updateIllegal(_mode, true);
        final String _freq = _mode.freq;
        if (workService != null) {
            workService.startReco(_mode);
        }
//        singleTimer = new Timer();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Single _Single = Single.setSingle(_freq);
                stopTask(Attribute.TASKTYPE.SCAN);
                startTask(Attribute.TASKTYPE.SINGLE);
                setDemodulation(_Single.demodulationMode);
                singleTimer = new Timer();
                singleTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        stopTask(Attribute.TASKTYPE.SINGLE);
                        if (currentFragment instanceof ScanFragment)
                            startTask(Attribute.TASKTYPE.SCAN);
                        if (workService != null) {
                            workService.stopReco();
                        }
                    }
                }, 1 * 1000 * 60);
            }
        }, 5);
    }

    private void setDemodulation(String pValue) {
        if (pValue.equalsIgnoreCase(getResources().getStringArray(
                R.array.array_demodulation_mode)[0])) {
            workService.sendCMD("SYSTEM:CHAnnel:AUDio 0_");
        } else {
            workService.sendCMD("SENS:DEM " + pValue);
            workService.sendCMD("SYSTEM:CHAnnel:AUDio 1_");
        }
    }

    public void onEventMainThread(NetEvent netEvent) {
        isConn = netEvent.isConn();
        if(isConn && this.tasktype == Attribute.TASKTYPE.SCAN){
            startTask(Attribute.TASKTYPE.SCAN);
        }
    }

    public void onEventMainThread(LatLng latLng) {
        oldLatlng = latLng;
    }

    public void onEventMainThread(ThresholdEvent threshold) {
        this.threshold = threshold.getThreshold();
    }

    @Override
    public boolean setCommand(String pCommand) {
        if (workService != null && isConn) {
            return workService.sendCMD(pCommand);
        }
        return false;
    }

    @Override
    public float[] getMeasureValue(Attribute.DATATYPE datatype) {
        if (workService != null) {
            return workService.readCMD(datatype);
        }
        return null;
    }

    @Override
    public void stopTask(Attribute.TASKTYPE tasktype) {

        if(!(currentFragment instanceof ScanFragment)){
            if(singleTimer != null){
                singleTimer.cancel();
                singleTimer = null;
                if (workService != null) {
                    workService.stopReco();
                }
            }
        }
        if (taskThread != null) {

            if(tasktype == Attribute.TASKTYPE.NO){
                taskThread.setTaskStatus(false);
                setCommand(Attribute.DELETEUDP);
                taskThread = null;
            }else if (taskThread.getTasktype() == tasktype) {
                taskThread.setTaskStatus(false);
                taskThread = null;
            }
        }
    }

    @Override
    public IllegalRadioModel updateIllegal(IllegalRadioModel model, Attribute.OPERATION_TYPE pType) {
        IllegalRadioModel _Model1 = null;
        switch (pType) {
            case UPDATE:
                if (illegalRadioModelSQLiteDALBase != null) {
                    illegalRadioModelSQLiteDALBase.Update("and uid = " + model.uid, model);
                    mainIllegalRadioModelSet.remove(model);
                    if(model.handle <= 0)
                        mainIllegalRadioModelSet.add(model);
                    _Model1 = model;
                }
                break;
            case DELETE:
                if (mainIllegalRadioModelSet.remove(model)) {
                    if (illegalRadioModelSQLiteDALBase != null) {
                        illegalRadioModelSQLiteDALBase.Delete("and uid =" + model.uid);
                    }
                    _Model1 = model;
                }
                break;
            case INSTER:
                if (mainIllegalRadioModelSet.add(model)) {
                    if (illegalRadioModelSQLiteDALBase != null) {
                        _Model1 = illegalRadioModelSQLiteDALBase.install(model);
                    }
                }
                break;
            default:
                break;
        }
        return _Model1;
    }

    @Override
    public void setWorkStatus(boolean isruning) {
        if (isruning) {
            if (workService != null) {
                workService.startWork();
            }
        } else {
            if (workService != null) {
                if (taskThread != null)
                    stopTask(taskThread.getTasktype());
                workService.stopWork();
            }
        }
    }

    @Override
    public Attribute.TASKTYPE getTaskType() {
        if (taskThread != null) {
            return taskThread.getTasktype();
        }
        return Attribute.TASKTYPE.NO;
    }

    @Override
    public boolean startTask(Attribute.TASKTYPE tasktype) {

        if(tasktype == Attribute.TASKTYPE.NO){
            tasktype = this.tasktype;
        }
        if (taskThread != null) {
            if (tasktype == taskThread.getTasktype()) {
                return true;
            }
        }
        this.tasktype = tasktype;
        if (workService != null) {
            if (workService.startTask(tasktype)) {
                if (taskThread != null) {
                    if (taskThread.isTaskRuning) {
                        if (tasktype == taskThread.getTasktype()) {
                            return true;
                        } else {
                            taskThread.setTaskStatus(false);
                            taskThread = null;
                        }
                    } else {
                        taskThread = null;
                    }
                }
                taskThread = new TaskThread(tasktype);
                taskThread.start();
            }
        }
        return false;
    }

    @Override
    public void menuOnItemClickListener(int position) {
        openFragment(position, true);
        //关闭
        drawerLayout.closeDrawer(Gravity.LEFT);
    }

    private void openFragment(int position, boolean isOnclick) {
        if (position == 2 && mainIllegalRadioModelSet.size() <= 0) {
            showMsg("可疑广播列表为空");
            return;
        }
        if (currentFragmentID == position && isOnclick) {
            return;
        }
        FragmentBase _Fragment = getFragment(position);
        if (_Fragment == null) {
            return;
        }
        currentFragment = _Fragment;
        currentFragmentID = position;
        showFreagment(R.id.main_fragment, currentFragment);
        preferences.edit().putInt(CONFIG_KEY_FRAGMENT, currentFragmentID);
    }

    public void startSingle(int index, Boolean isOpenFragment) {
        if (isOpenFragment) {
            currentFragment = SingleFragment2.newInstance(index);
            currentFragmentID = 2;
            showFreagment(R.id.main_fragment, currentFragment);
        } else {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = speech.setLanguage(Locale.CHINA);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                DownLoadAndInstallApkDialog dialog = new DownLoadAndInstallApkDialog(this);
                dialog.setCallback(new DownLoadAndInstallApkDialog.DialogCallback() {

                    @Override
                    public void doCancel(boolean isCancel) {
                        if (isCancel) {
                            if (speech != null) {
                                speech.shutdown();
                                speech = null;
                                Toast.makeText(getApplication(), R.string.error, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                dialog.show();
            }
        }
    }

    protected void startTTS() {
        if (speech == null) {
            speech = new TextToSpeech(this, this);
        }
    }

    class TaskThread extends Thread {
        int count = 0;
        private Attribute.TASKTYPE tasktype;
        private boolean isTaskRuning = false;

        public TaskThread(Attribute.TASKTYPE tasktype) {
            this.tasktype = tasktype;
        }

        public Attribute.TASKTYPE getTasktype() {
            return tasktype;
        }

        public boolean isTaskRuning() {
            return isTaskRuning;
        }

        public void setTaskStatus(boolean isTaskRuning) {
            this.isTaskRuning = isTaskRuning;
        }

        private void verification(float[] date) {
            if (date == null)
                return;
            int _date = date.length;

            for (int i = 0; i < _date; i++) {
                if (i > 0 & i < _date - 1) {
                    if (date[i] >= threshold) {
                        if (date[i] > date[i - 1] && date[i] > date[i + 1]) {
                            float _freq = (float) ((i) * 0.025 + 88.0);
                            if (Float.compare(_freq, (float) 101.7) == 0) {
                                if (count > 100) {
                                    IllegalRadioModel _Model = new IllegalRadioModel();
                                    _Model.freq = String.format("%.2fMHz", _freq);
                                    _Model.handle = 0;
                                    _Model.appeartime = DateTools.getCurrentDateString("yyyy-MM-dd HH:mm:ss");
                                    _Model.lat = 30.5843;
                                    _Model.lon = (104.0558);
                                    EventBus.getDefault().post(new DrawMarkerEvent(_Model));
                                }
                                count++;
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void run() {
            isTaskRuning = true;
            float[] _date = null;
            while (isTaskRuning) {
                switch (tasktype) {
                    case SCAN:
                        _date = MainActivity.this.getMeasureValue(Attribute.DATATYPE.SCANDATA);
                        verification(_date);
                        if (_date != null) {
                            MainActivity.this.onupdate(_date, Attribute.DATATYPE.SCANDATA);
                        }
                        break;
                    case SINGLE:
                        _date = MainActivity.this.getMeasureValue(Attribute.DATATYPE.IQDATA);
                        if (_date != null) {
                            MainActivity.this.onupdate(_date, Attribute.DATATYPE.IQDATA);
                        }
                        break;
                    default:
                        isTaskRuning = false;
                        break;
                }
            }

        }
    }
    public Attribute.SOUNDTYPE getSoundtype() {
        return soundtype;
    }
    public void onEventMainThread(Attribute.SOUNDTYPE soundtype){
        this.soundtype = soundtype;
        if(soundtype == Attribute.SOUNDTYPE.DEVICE){
            if(workService != null){
                workService.setSound(true);
            }
        }else {
            if(workService != null){
                workService.setSound(false);
            }
        }
    }

}
