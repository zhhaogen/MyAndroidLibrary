package cn.zhg.base;

import android.app.ActivityManager;
import android.app.Service;
import android.content.*;
import android.content.DialogInterface.OnDismissListener;
import android.net.NetworkInfo;
import android.os.*;
import android.support.annotation.*;
import android.view.View;

import java.util.*;
import cn.zhg.base.wrapper.*;
/**
 * Activity基础类
 */
public class BaseActivity extends _Activity implements Handler.Callback {
    /**
     * 消息处理
     */
    protected Handler mBaseHandler;
    /**
     * 储存发送消息code
     */
    protected HashSet<Integer> mWhats;
    /**
     * 消息处理回调
     */
    private SafeCallback mCallBack;
    /**
     * 记录所有广播,方便退出时一次性注销
     */
    private List<BroadcastReceiver> mReceivers;
    /**
     * 已注册的Local BroadcastReceiver
     */
    @Deprecated
    private List<BroadcastReceiver> mLocalReceivers;
    /**已注册的ServiceConnection*/
    private List<ServiceConnection> mServiceConnections;
    private BaseContextWrapper wrapper1;
    private UiContextWrapper wrapper2;

    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWhats = new HashSet<Integer>();
        mCallBack = new SafeCallback(this);
        mBaseHandler = new Handler(mCallBack);
        mReceivers = new ArrayList<>();
        mLocalReceivers = new ArrayList<>();
        mServiceConnections=new ArrayList<>();
        wrapper1 =new BaseContextWrapper(this);
        wrapper2 =new UiContextWrapper(this);
    }
    @SuppressWarnings("unchecked")
    public <T extends View> T getViewById(int id)
    {
        return (T) findViewById(id);
    }
    /**
     * 发送消息<br>
     *
     * @see @link{#handleMessage(Message) }
     */
    protected void sendMessage(Message msg) {
        mWhats.add(msg.what);
        mBaseHandler.sendMessage(msg);
    }

    /**
     * 发送消息<br>
     *
     * @see @link{#handleMessage(Message)}
     */
    protected void sendMessage(int what, Object obj) {
        Message msg = Message.obtain(this.mBaseHandler);
        msg.what = what;
        msg.obj = obj;
        mWhats.add(what);
        mBaseHandler.sendMessage(msg);
    }

    /**
     * 发送消息<br>
     *
     * @see @link{#handleMessage(Message) }
     */
    protected void sendMessageDelayed(Message msg, long delayMillis) {
        mWhats.add(msg.what);
        mBaseHandler.sendMessageDelayed(msg, delayMillis);
    }

    /**
     * 发送消息<br>
     * @see @link{#handleMessage(Message) }
     */
    protected void sendEmptyMessage(int what) {
        mWhats.add(what);
        mBaseHandler.sendEmptyMessage(what);
    }

    /**
     * 删除消息
     */
    protected void removeMessage(int what) {
        mWhats.remove(what);
        mBaseHandler.removeMessages(what);
    }

    /**
     * 处理消息内容
     *
     * @see android.os.Handler.Callback#handleMessage(android.os.Message)
     */
    public boolean handleMessage(Message msg) {
        return false;
    }

    protected <T extends BaseApplication> T getApp() {
        return wrapper1.getApp();
    }

    public boolean isServiceRuning(Class<? extends Service> clazz) {
        return wrapper1.isServiceRuning(clazz);
    }

    public ActivityManager.RunningServiceInfo getServiceInfo(Intent name) {
        return wrapper1.getServiceInfo(name);
    }
    public void showProcess(CharSequence msg) {
        wrapper2.showProcess(msg);
    }
    public void showProcess(CharSequence msg, boolean cancle) {
        wrapper2.showProcess(msg, cancle);
    }

    public void showProcess(CharSequence msg, boolean cancle, DialogInterface.OnCancelListener listener) {
        wrapper2.showProcess(msg, cancle, listener);
    }

    /** 
	 * @see UiContextWrapper#showProcess(java.lang.CharSequence, int)
	 */
    public void showProcess(CharSequence msg, int max)
	{
		wrapper2.showProcess(msg, max);
	}
	/** 
	 * @see UiContextWrapper#showProcess(java.lang.CharSequence, int, android.content.DialogInterface.OnDismissListener)
	 */
    public void showProcess(CharSequence msg, int max,
			OnDismissListener listener)
	{
		wrapper2.showProcess(msg, max, listener);
	}
    public void showProcess(CharSequence msg, int max,
			DialogInterface.OnCancelListener listener)
	{
    	wrapper2.showProcess(msg, max, listener);
	}
	/** 
	 * @see UiContextWrapper#updateProcess(int)
	 */
    public void updateProcess(int current)
	{
		wrapper2.updateProcess(current);
	}
    public void hideProcess() {
        wrapper2.hideProcess();
    }

	public void alert(CharSequence title, CharSequence msg) {
        wrapper2.alert(title, msg);
    }

	public void alert(@StringRes int titleResId, @StringRes int msgResId) {
        wrapper2.alert(titleResId, msgResId);
    }

    public void alert(CharSequence msg) {
        wrapper2.alert(msg);
    }

    public void alert(@StringRes int msgResId) {
        wrapper2.alert(msgResId);
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

    public int getPreferenceInt(String key,@IntegerRes int resId) {
        return wrapper1.getPreferenceInt(key, resId);
    }

    public int getPreferenceInt(String key) {
        return wrapper1.getPreferenceInt(key);
    }

    public boolean getPreferenceBoolean(String key, @BoolRes int resId) {
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
    @CallSuper
    protected void onDestroy() {
        if (mWhats != null) {
            for (int what : mWhats) {
                mBaseHandler.removeMessages(what);
            }
        }
        if (mCallBack != null) {
            mCallBack.clear();
        }
        //无需手动注销Receivers
        unregisterReceivers();
        unbindServices();
        if(mLocalReceivers!=null){
            mLocalReceivers.clear();
        }
        wrapper2.onDestroy();
        super.onDestroy();
    }  
 
}
