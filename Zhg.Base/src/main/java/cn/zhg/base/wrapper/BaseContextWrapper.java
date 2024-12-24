package cn.zhg.base.wrapper;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.*;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.zhg.base.BaseApplication;

/**
 * Context扩展
 */
public class BaseContextWrapper extends ContextWrapper {
    private ActivityManager mActivityManager;
    private ConnectivityManager mConnectivityManager;
    private SharedPreferences mSh;
    private Toast mTip;
    /**
     * ${applicationId}.permission.INNER
     */
    public final    String INNER_PERMISSION;
    public BaseContextWrapper(Context base) {
        super(base);
        INNER_PERMISSION = base.getPackageName() + ".permission.INNER";
        init();
    }

    private void init() {
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        mConnectivityManager = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);
        mSh = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public <T extends BaseApplication> T getApp() {
        return (T) getApplicationContext();
    }

    /**
     * 服务是否在运行
     *
     * @param clazz
     */
    public boolean isServiceRuning(Class<? extends Service> clazz) {
        if (clazz == null) {
            return false;
        }
        List<ActivityManager.RunningServiceInfo> services = mActivityManager.getRunningServices(Integer.MAX_VALUE);
        if (services == null || services.isEmpty()) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo service : services) {
            if (clazz.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回服务信息
     *
     * @param name 服务名
     * @return
     */
    public ActivityManager.RunningServiceInfo getServiceInfo(Intent name) {
        List<ActivityManager.RunningServiceInfo> list = mActivityManager.getRunningServices(java.lang.Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo r : list) {
            if (r.service.equals(name.getComponent())) {
                return r;
            }
        }
        return null;
    }


    /**
     * 获取当前网络类型
     */
    public NetworkInfo getNetworkInfo() {
        try {
            @SuppressLint("MissingPermission") NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            return mNetworkInfo;
        } catch (Throwable e) {

        }
        return null;
    }

    /**
     * 网络是否可用
     */
    public boolean isNetWork() {
        NetworkInfo mNetworkInfo = getNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
        }
        return false;
    }

    /**
     * 网络是否WiFi连接
     */
    public boolean isWIFI() {
        NetworkInfo mNetworkInfo = getNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected()
                    && mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }

    /**
     * 提示内容
     */
    public void tip(CharSequence msg) {
        if (mTip != null) {
            mTip.cancel();
            mTip = null;
        }
        mTip = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        mTip.show();
    }

    /**
     * 提示内容
     */
    public void tip(@StringRes int resId) {
        if (mTip != null) {
            mTip.cancel();
            mTip = null;
        }
        mTip = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
        mTip.show();
    }

    /**
     * 从储存配置中读取字符串
     *
     * @param key
     * @return
     */
    public String getPreference(String key) {
        return getPreference(key, null);
    }

    /**
     * 从储存配置中读取字符串
     *
     * @param key
     * @param resId 默认字符资源 {@link Context#getString}
     * @return
     */
    public String getPreference(String key,@StringRes  int resId) {
        String defaultValue;
        if (resId == 0) {
            defaultValue = null;
        } else {
            defaultValue = getString(resId);
        }
        return getPreference(key, defaultValue);
    }

    /**
     * 从储存配置中读取字符串
     *
     * @param key
     * @param defValue 默认字符
     * @return
     */
    public String getPreference(String key, String defValue) {
        return mSh.getString(key, defValue);
    }

    /**
     * 从储存配置中读取数字
     *
     * @param key
     * @param resId 默认值资源 {@link Resources#getInteger}
     * @return
     */
    public int getPreferenceInt(String key, @IntegerRes  int resId) {
        int defValue = 0;
        if (resId != 0) {
            defValue =getResources().getInteger(resId);
        }
        return getPreferenceInteger(key, defValue);
    }

    /**
     * 从储存配置中读取数字
     *
     * @param key
     * @return 默认返回0
     */
    public int getPreferenceInt(String key) {
        return getPreferenceInteger(key, 0);
    }
	/**
     * 从储存配置中读取数字
     *
     * @param key
     * @param defValue
     *            默认值
     * @return
     */
    public Integer getPreferenceInteger(String key, Integer defValue)
    {
       if( mSh.contains(key)){
           return mSh.getInt(key,0);
       }
       return defValue;
    }

    /**
     * 从储存配置中读取布尔值
     *
     * @param key
     * @param resId 默认值资源 {@link Resources#getBoolean}
     * @return
     */
    public boolean getPreferenceBoolean(String key,@BoolRes int resId) {
        return getPreferenceBoolean(key, getResources().getBoolean(resId));
    }

    /**
     * 从储存配置中读取布尔值
     *
     * @param key
     * @return 默认返回false
     */
    public boolean getPreferenceBoolean(String key) {
        return getPreferenceBoolean(key, false);
    }
    /**
     * 从储存配置中读取布尔值
     *
     * @param key
     * @param defValue 默认值
     */
    public boolean getPreferenceBoolean(String key, boolean defValue) {
        return mSh.getBoolean(key,defValue);
    }
    /**
     * 从储存配置中读取数值
     *
     * @param key
     * @return 默认返回0
     */
    public long getPreferenceLong(String key) {
        return mSh.getLong(key, 0);
    }

    /**
     * 从储存配置中读取数值
     *
     * @param key
     * @return 默认返回0
     */
    public float getPreferenceFloat(String key) {
        return mSh.getFloat(key, 0);
    }

    /**
     * 从储存配置中读取数值
     *
     * @param key
     * @return
     */
    public double getPreferenceDouble(String key, double defValue) {
        String s = mSh.getString(key, null);
        if (s == null) {
            return defValue;
        }
        try {
            return Double.parseDouble(s);
        } catch (Exception ex) {
        }
        return defValue;
    }

    /**
     * 从储存配置中读取字符串组
     *
     * @param key
     * @return 默认返回空Set
     */
    public Set<String> getPreferenceArray(String key) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            String js = mSh.getString(key, null);
            if (js != null) {
                try {
                    JSONArray array = new JSONArray(js);
                    Set<String> set = new HashSet<String>();
                    for (int i = 0, length = array.length(); i < length; i++) {
                        set.add(array.getString(i));
                    }
                } catch (JSONException e) {
                }
            }
            return new HashSet<String>();
        } else {
            return mSh.getStringSet(key, new HashSet<String>());
        }
    }

    /**
     * 默认储存配置是否包含该配置
     *
     * @param key
     * @return
     */
    public boolean hasPreference(String key) {
        return mSh.contains(key);
    }

    /**
     * 添加默认储存配置
     *
     * @param key
     * @return
     */
    public void putPreference(String key, String value) {
        Set<String> sets = getPreferenceArray(key);
        sets.add(value);
        setPreference(key, sets);
    }

    /**
     * 设置默认储存配置
     *
     * @param key
     * @return
     */
    public void setPreference(String key, Object value) {
        SharedPreferences.Editor ed = mSh.edit();
        if (value == null) {
            ed.remove(key);
        } else {
            if (value instanceof Boolean) {
                ed.putBoolean(key, (Boolean) value);
            } else if (value instanceof Float) {
                ed.putFloat(key, (Float) value);
            } else if (value instanceof java.lang.Integer) {
                ed.putInt(key, (Integer) value);
            } else if (value instanceof Long) {
                ed.putLong(key, (Long) value);
            } else if (value instanceof String) {
                ed.putString(key, (String) value);
            } else if (value instanceof Set) {
                @SuppressWarnings("unchecked")
                Set<String> sets = (Set<String>) value;
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    JSONArray array = new JSONArray();
                    for (String s : sets) {
                        array.put(s);
                    }
                    ed.putString(key, array.toString());
                } else {
                    ed.putStringSet(key, sets);
                }
            } else {
                ed.putString(key, String.valueOf(value));
            }
        }
        ed.commit();
    }

}
