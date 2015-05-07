package com.ldkj.illegal_radio.utils;

/**
 * Created by john on 15-4-10.
 */
public class Attribute {
    public static final boolean DEBUG = true;
    public static final String TAG = "com_ldkj_illegal_radio";

    public static final int MSG_UPDATE_LOCATION = 200010;


    public static final int MSG_WORK_NET_SUCCESS = 300010;
    public static final int MSG_WORK_NET_FAILED = 300011;
    public static final int MSG_WORK_SPCE_DATE = 300012;
    public static final int MSG_WORK_AUDIO_DATE = 300013;



    public static final String VERTION = "*idn?\n"; // 版本询问命令
    public static final String OPTION = "*opt?\n"; // 选件命令
    public static final String FORMATBINARYPACK = "FORM PACK;:FORM:BORD SWAP\n"; // 定义接收机数据以二进制格式发送,并设定网络字节顺序，及高字节在高位
    public static final String FORMATASCIIPACK = "FORM ASC\n";

    public static final String QUERYCENTERFREQ = "SENS:FREQ?\n"; // 询问中心频率
    public static final String QUERYFSCANSTART = "FREQ:START?\n"; // 询问FSACN频率扫描开始频率
    public static final String QUERYFSCANSTOP = "FREQ:STOP?\n"; // 询问FSACN频率扫描结束频率
    public static final String QUERYFSCANSTEP = "FREQ:STEP?\n"; // 询问FSCAN频率扫描结步进
    public static final String QUERYDSCANSTART = "FREQ:DSC:START?\n"; // 询问DSCAN频率扫描开始频率
    public static final String QUERYDSCANSTOP = "FREQ:DSC:STOP?\n"; // 询问DSCAN频率扫描结束频率
    public static final String QUERYDSCANSTEP = "FREQ:DSC:STEP?\n"; // 询问DSCAN频率扫描结步进

    public static final String QUERYLEVEL = "SENS:DATA?\n"; // 查询电平ITU
    public static final String SPECTRUMDATA = "TRACe:DATA? IFPAN\n"; // 获取频谱数据
    public static final String SCANDATA = "TRACE:DATA? MTRACE\n"; // 获取扫描数据
    public static final String[] IQMDATAS = {"TRACe:DATA? IF 0\n",
            "TRACe:DATA? IF 1\n", "TRACe:DATA? IF 2\n", "TRACe:DATA? IF 3\n"};
    public static final String IQMDATA1 = "TRACe:DATA? IF 0\n"; // 获取IQ数据
    public static final String IQMDATA2 = "TRACe:DATA? IF 1\n";
    public static final String IQMDATA3 = "TRACe:DATA? IF 2\n";
    public static final String IQMDATA4 = "TRACe:DATA? IF 3\n";

    public static final String DELETEUDP = "TRAC:UDP:DEL ALL\n"; // 删除所有UDP选项
    public static final String STARTSCAN = "INIT\n"; // 开始扫描
    public static final String STOPSCAN = "ABORT\n"; // 停止扫描

}
