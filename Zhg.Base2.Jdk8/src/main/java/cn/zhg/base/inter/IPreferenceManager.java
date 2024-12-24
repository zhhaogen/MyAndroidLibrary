package cn.zhg.base.inter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import org.json.*;

import java.util.*;

/**
 * 储存管理
 */
public interface IPreferenceManager extends IContext {
    default SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    /**
     * 从储存配置中读取字符串
     *
     * @param key
     * @return
     */
    default String getPreference(String key) {
        return getPreference(key, (String) null);
    }

    /**
     * 从储存配置中读取字符串
     *
     * @param key
     * @param resId 默认字符资源 {@link Context#getString}
     * @return
     */
    default String getPreference(String key, int resId) {
        String defaultValue;
        if (resId == 0) {
            defaultValue = null;
        } else {
            defaultValue = getContext().getString(resId);
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
    default String getPreference(String key, String defValue) {
        return getSharedPreferences().getString(key, defValue);
    }

    /**
     * 从储存配置中读取数字
     *
     * @param key
     * @param resId 默认值资源 {@link Resources#getInteger}
     * @return
     */
    default int getPreferenceInt(String key, int resId) {
        int defValue = 0;
        if (resId != 0) {
            defValue = getContext().getResources().getInteger(resId);
        }
        return getPreferenceInteger(key, defValue);
    }

    /**
     * 从储存配置中读取数字
     *
     * @param key
     * @return 默认返回0
     */
    default int getPreferenceInt(String key) {
        return getPreferenceInteger(key, 0);
    }

    /**
     * 从储存配置中读取数字
     *
     * @param key
     * @param defValue 默认值
     * @return
     */
    default Integer getPreferenceInteger(String key, Integer defValue) {
        SharedPreferences sh = getSharedPreferences();
        if (sh.contains(key)) {
            return sh.getInt(key, 0);
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
    default boolean getPreferenceBoolean(String key, int resId) {
        return getPreferenceBoolean(key, getContext().getResources().getBoolean(resId));
    }

    /**
     * 从储存配置中读取布尔值
     *
     * @param key
     * @return 默认返回false
     */
    default boolean getPreferenceBoolean(String key) {
        return getPreferenceBoolean(key, false);
    }
    /**
     * 从储存配置中读取布尔值
     *
     * @param key
     * @param defValue 默认值
     */
    default boolean getPreferenceBoolean(String key, boolean defValue) {
        SharedPreferences sh = getSharedPreferences();
        return sh.getBoolean(key,defValue);
    }
    /**
     * 从储存配置中读取数值
     *
     * @param key
     * @return 默认返回0
     */
    default long getPreferenceLong(String key) {
        SharedPreferences sh = getSharedPreferences();
        return sh.getLong(key, 0);
    }
    /**
     * 从储存配置中读取数值
     *
     * @param key
     * @return 默认返回0
     */
    default float getPreferenceFloat(String key) {
        return getSharedPreferences().getFloat(key, 0);
    }

    /**
     * 从储存配置中读取数值
     *
     * @param key
     * @return
     */
    default double getPreferenceLong(String key, double defValue) {
        String s = getSharedPreferences().getString(key, null);
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
    default Set<String> getPreferenceArray(String key) {
        SharedPreferences mSh = getSharedPreferences();
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
    default boolean hasPreference(String key) {
        return getSharedPreferences().contains(key);
    }

    /**
     * 添加默认储存配置
     *
     * @param key
     * @return
     */
    default void putPreference(String key, String value) {
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
    default void setPreference(String key, Object value) {
        SharedPreferences.Editor ed = getSharedPreferences().edit();
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
