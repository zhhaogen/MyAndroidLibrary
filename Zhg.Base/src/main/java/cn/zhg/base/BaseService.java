package cn.zhg.base;

import android.app.ActivityManager;
import android.app.Service;
import android.content.*;
import android.net.NetworkInfo;
import android.os.*;
import android.support.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.zhg.base.wrapper.BaseContextWrapper;

/**
 * Service基础类
 */
public class BaseService extends Service { 
    /**
     * 记录所有广播,方便退出时一次性注销
     */
    private List<BroadcastReceiver> mReceivers;
    /**已注册的ServiceConnection*/
    private List<ServiceConnection> mServiceConnections;
    /**
     * 已注册的Local BroadcastReceiver
     */
    @Deprecated
    private List<BroadcastReceiver> mLocalReceivers;
    private BaseContextWrapper wrapper1;
    @CallSuper
    public void onCreate() {
        super.onCreate();
        mReceivers = new ArrayList<>();
        mLocalReceivers = new ArrayList<>();
        mServiceConnections=new ArrayList<>();
        wrapper1 =new BaseContextWrapper(this);
    }
    @CallSuper
    public void onDestroy() {
    	this.unbindServices();
    	this.unregisterReceivers();
        if(mLocalReceivers!=null){
            mLocalReceivers.clear();
        }
        super.onDestroy();
    }
    public IBinder onBind(Intent intent) {
        return null;
    }
    public <T extends BaseApplication> T getApp() {
        return wrapper1.getApp();
    }

    public boolean isServiceRuning(Class<? extends Service> clazz) {
        return wrapper1.isServiceRuning(clazz);
    }

    public ActivityManager.RunningServiceInfo getServiceInfo(Intent name) {
        return wrapper1.getServiceInfo(name);
    }

    public NetworkInfo getNetworkInfo() {
        return wrapper1.getNetworkInfo();
    }

    public boolean isNetWork() {
        return wrapper1.isNetWork();
    }

    public boolean isWIFI() {
        return wrapper1.isWIFI();
    }

    public void tip(CharSequence msg) {
        wrapper1.tip(msg);
    }

    public void tip(@StringRes int resId) {
        wrapper1.tip(resId);
    }

    public String getPreference(String key) {
        return wrapper1.getPreference(key);
    }

    public String getPreference(String key,@StringRes  int resId) {
        return wrapper1.getPreference(key, resId);
    }

    public String getPreference(String key, String defValue) {
        return wrapper1.getPreference(key, defValue);
    }

    public int getPreferenceInt(String key,@IntegerRes  int resId) {
        return wrapper1.getPreferenceInt(key, resId);
    }

    public int getPreferenceInt(String key) {
        return wrapper1.getPreferenceInt(key);
    }

    public boolean getPreferenceBoolean(String key,@BoolRes int resId) {
        return wrapper1.getPreferenceBoolean(key, resId);
    }

    public boolean getPreferenceBoolean(String key) {
        return wrapper1.getPreferenceBoolean(key);
    }

    public long getPreferenceLong(String key) {
        return wrapper1.getPreferenceLong(key);
    }

    public float getPreferenceFloat(String key) {
        return wrapper1.getPreferenceFloat(key);
    }

    public double getPreferenceDouble(String key, double defValue) {
        return wrapper1.getPreferenceDouble(key, defValue);
    }

    public Set<String> getPreferenceArray(String key) {
        return wrapper1.getPreferenceArray(key);
    }

    public boolean hasPreference(String key) {
        return wrapper1.hasPreference(key);
    }

    public void putPreference(String key, String value) {
        wrapper1.putPreference(key, value);
    }

    public void setPreference(String key, Object value) {
        wrapper1.setPreference(key, value);
    }

    @Deprecated
    public List<BroadcastReceiver> getLocalReceivers() {
        return mLocalReceivers;
    }
    public void sendLocalBroadcast(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            this.sendBroadcast(intent);
            return;
        }
        this.sendBroadcast(intent, wrapper1.INNER_PERMISSION);
    }
    @Deprecated
    public void unregisterLocalReceiver(BroadcastReceiver receiver) {
        if (receiver == null || mLocalReceivers.isEmpty())
        {
            return;
        }
        Iterator<BroadcastReceiver> itor = mLocalReceivers.iterator();
        while (itor.hasNext())
        {
            BroadcastReceiver item = itor.next();
            if (item.equals(receiver))
            {
                itor.remove();
            }
        }
        this.unregisterReceiver(receiver);
    }

    public void registerLocalReceiver(BroadcastReceiver receiver, String... actions) {
        if (actions == null || receiver == null)
        {
            return;
        }
        IntentFilter filter = new IntentFilter();
        for (String action : actions)
        {
            filter.addAction(action);
        }
        mLocalReceivers.add(receiver);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            this.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
            return;
        }
        this.registerReceiver(receiver, filter, wrapper1.INNER_PERMISSION, null);
    }

    /**
     * 
     */
    public Intent registerReceiver(BroadcastReceiver receiver, String... actions) {
        if (receiver == null || actions == null) {
            return null;
        }
        mReceivers.add(receiver);
        IntentFilter filter = new IntentFilter();
        for (String action : actions) {
            filter.addAction(action);
        }
        return super.registerReceiver(receiver, filter);
    }
    /**
     * 
     */
    @CallSuper
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler, int flags) {
        if (receiver != null) {
            mReceivers.add(receiver);
        }
        return super.registerReceiver(receiver, filter, broadcastPermission, scheduler, flags);
    }
    @CallSuper
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, int flags) {
        if (receiver != null) {
            mReceivers.add(receiver);
        }
        return super.registerReceiver(receiver, filter, flags);
    }
    @CallSuper
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        if (receiver != null) {
            mReceivers.add(receiver);
        }
        return super.registerReceiver(receiver, filter, broadcastPermission, scheduler);
    }
    @CallSuper
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (receiver != null) {
            mReceivers.add(receiver);
        }
        return super.registerReceiver(receiver, filter); 
    }
    @CallSuper
    public void unregisterReceiver(BroadcastReceiver receiver) {
        if (receiver == null || mReceivers.isEmpty()) {
            super.unregisterReceiver(receiver);
            return;
        }
        Iterator<BroadcastReceiver> itor = mReceivers.iterator();
        while (itor.hasNext()) {
            BroadcastReceiver item = itor.next();
            if (item.equals(receiver)) {
                itor.remove();
            }
        }
        super.unregisterReceiver(receiver);
    }

    /**
     * 注销所有广播
     */
    protected void unregisterReceivers() {
        if (mReceivers.isEmpty()) {
            return;
        }
        Iterator<BroadcastReceiver> itor = mReceivers.iterator();
        while (itor.hasNext()) {
            BroadcastReceiver item = itor.next();
            super.unregisterReceiver(item);
            itor.remove();
        }
    }

    /**
     * 自动解绑服务
     */
    @CallSuper
    public boolean bindService(Intent service, ServiceConnection conn,
                               int flags)
    {
        if(conn==null){
            return false;
        } 
        this.mServiceConnections.add(conn);
        return super.bindService(service,conn,flags);
    }
    /**
     * 主动解绑服务
     */
    @CallSuper
    public void unbindService(ServiceConnection conn)
    {
        if(conn==null){
            return;
        }
        Iterator<ServiceConnection> iterator = mServiceConnections.iterator();
        while(iterator.hasNext()){
        	ServiceConnection mConn = iterator.next();
            if(mConn.equals(conn)){
                super.unbindService(mConn);
                iterator.remove();
                return;
            }
        }
    }
    /**
     * 取消所有服务连接
     */
    protected void unbindServices()
    {
        if(this.mServiceConnections==null||mServiceConnections.isEmpty()){
            return;
        }
        Iterator<ServiceConnection> iterator = mServiceConnections.iterator();
        while(iterator.hasNext()){
            ServiceConnection proxyConn = iterator.next();
            super.unbindService(proxyConn);
            iterator.remove();
        }
    } 
}
