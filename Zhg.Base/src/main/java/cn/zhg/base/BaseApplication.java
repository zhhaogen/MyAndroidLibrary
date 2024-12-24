package cn.zhg.base;

import android.app.Application;
import android.support.annotation.*;

import java.util.Set;
import cn.zhg.base.wrapper.BaseContextWrapper;
/**
 * Appliction基础类
 */
public class BaseApplication extends Application {
    private BaseContextWrapper baseContextWarper;

    public void onCreate() {
        super.onCreate();
        baseContextWarper = new BaseContextWrapper(this);
    }

    public String getPreference(String key) {
        return baseContextWarper.getPreference(key);
    }

    public String getPreference(String key,@StringRes int resId) {
        return baseContextWarper.getPreference(key, resId);
    }

    public String getPreference(String key, String defValue) {
        return baseContextWarper.getPreference(key, defValue);
    }

    public int getPreferenceInt(String key,@IntegerRes int resId) {
        return baseContextWarper.getPreferenceInt(key, resId);
    }

    public int getPreferenceInt(String key) {
        return baseContextWarper.getPreferenceInt(key);
    }

    public boolean getPreferenceBoolean(String key, @BoolRes int resId) {
        return baseContextWarper.getPreferenceBoolean(key, resId);
    }

    public boolean getPreferenceBoolean(String key) {
        return baseContextWarper.getPreferenceBoolean(key);
    }

    public long getPreferenceLong(String key) {
        return baseContextWarper.getPreferenceLong(key);
    }

    public float getPreferenceFloat(String key) {
        return baseContextWarper.getPreferenceFloat(key);
    }

    public double getPreferenceDouble(String key, double defValue) {
        return baseContextWarper.getPreferenceDouble(key, defValue);
    }

    public Set<String> getPreferenceArray(String key) {
        return baseContextWarper.getPreferenceArray(key);
    }

    public boolean hasPreference(String key) {
        return baseContextWarper.hasPreference(key);
    }

    public void putPreference(String key, String value) {
        baseContextWarper.putPreference(key, value);
    }

    public void setPreference(String key, Object value) {
        baseContextWarper.setPreference(key, value);
    }
}
