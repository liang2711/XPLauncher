package com.zhuoho.xplauncher.S;

public class WifiConstant {

    /**
     * 通道频率
     */
    public static final int CHANNEL_WIDTH_160MHZ = 3;
    public static final int CHANNEL_WIDTH_20MHZ = 0;
    public static final int CHANNEL_WIDTH_40MHZ = 1;
    public static final int CHANNEL_WIDTH_80MHZ = 2;
    public static final int CHANNEL_WIDTH_80MHZ_PLUS_MHZ = 4;

    /**
     * WiFi频段
     */
    public static final int UNSPECIFIED = -1;
    public static final int WIFI_BAND_24_GHZ = 1;
    public static final int WIFI_BAND_5_GHZ = 2;
    public static final int WIFI_BAND_60_GHZ = 16;
    public static final int WIFI_BAND_6_GHZ = 8;

    //访问点的Mac地址
    public String BSSID;
    //WiFi名称
    public String SSID;
    //描述了身份验证、密钥管理和访问点支持的加密方案
    public String capabilities;

    //带宽
    public int channelWidth;
    //主20 MHz的频率(MHz)的渠道客户交流访问点
    public int frequency;
    //信号强度，也称RSSI
    public int level;


}
