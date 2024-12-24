package cn.zhg.base;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnDismissListener;
import android.os.*;
import android.support.annotation.*;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import cn.zhg.base.wrapper.BaseContextWrapper;
import cn.zhg.base.wrapper.UiContextWrapper;

/**
 * Fragment基础类<br>
 * 必须在BaseActivity下使用<br>
 */
public class BaseFragment extends _Fragment implements Handler.Callback {
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
    /**
     * 所属activity
     */
    private BaseActivity mBaseActivity;
    private View mContentView; 
	private boolean isStop;
    private boolean isDestroyView;
    private BaseContextWrapper wrapper1;
    private UiContextWrapper wrapper2;
    @CallSuper
    public void onAttach(Context context) {
        this.mBaseActivity = (BaseActivity) context;
        mReceivers = new ArrayList<>();
        mLocalReceivers = new ArrayList<>();
        mServiceConnections=new ArrayList<>();
        wrapper1 =new BaseContextWrapper(context);
        wrapper2 =new UiContextWrapper(context);
        super.onAttach(context);
    }
    @CallSuper
    public void onCreate(Bundle savedInstanceState) {
        mWhats = new HashSet<Integer>();
        mCallBack = new SafeCallback(this);
        mBaseHandler = new Handler(mCallBack);
        super.onCreate(savedInstanceState);
    }
    @CallSuper
    public void onDestroy() {
        if (mWhats != null) {
            for (int what : mWhats) {
                mBaseHandler.removeMessages(what);
            }
        }
        if (mCallBack != null) {
            mCallBack.clear();
        }
        this.unregisterReceivers();
        if(mLocalReceivers!=null){
           mLocalReceivers.clear();
        }
        wrapper2.onDestroy();
        super.onDestroy();
    }
    @CallSuper
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mContentView = view;
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getViewById(@IdRes int id) {
        if (mContentView == null) {
            tryGetContentView();
        }
        if (mContentView == null) {
            return null;
        }
        return (T) mContentView.findViewById(id);
    }

    protected void showProcess(CharSequence msg) {
        wrapper2.showProcess(msg);
    }
    protected void showProcess(CharSequence msg, boolean cancle) {
        wrapper2.showProcess(msg, cancle);
    }

    protected void showProcess(CharSequence msg, boolean cancle, DialogInterface.OnCancelListener listener) {
        wrapper2.showProcess(msg, cancle, listener);
    }

    /** 
	 * @see UiContextWrapper#showProcess(java.lang.CharSequence, int)
	 */
    protected void showProcess(CharSequence msg, int max)
	{
		wrapper2.showProcess(msg, max);
	}
	/** 
	 * @see UiContextWrapper#showProcess(java.lang.CharSequence, int, android.content.DialogInterface.OnDismissListener)
	 */
    protected void showProcess(CharSequence msg, int max,
			OnDismissListener listener)
	{
		wrapper2.showProcess(msg, max, listener);
	}
	/** 
	 * @see UiContextWrapper#updateProcess(int)
	 */
    protected void updateProcess(int current)
	{
		wrapper2.updateProcess(current);
	}
	protected void hideProcess() {
        wrapper2.hideProcess();
    }

    protected void alert(String title, String msg) {
        wrapper2.alert(title, msg);
    }

    protected void alert(@StringRes int titleResId,@StringRes int msgResId) {
        wrapper2.alert(titleResId, msgResId);
    }

    protected void alert(String msg) {
        wrapper2.alert(msg);
    }

    protected void alert(@StringRes int msgResId) {
        wrapper2.alert(msgResId);
    }
    protected void tip(CharSequence msg) {
    	mBaseActivity.tip(msg);
    }

    protected void tip(@StringRes int resId) {
    	mBaseActivity.tip(resId);
    }
    /**
     * 返回所属activity
     */
    public <T extends BaseActivity> T getBaseActivity() {
        if (mBaseActivity == null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            mBaseActivity = (BaseActivity) this.getActivity();
        }
        return (T) this.mBaseActivity;
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
     * @see  #handleMessage(Message)
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
     *
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
        // TODO
        return false;
    }

    @Deprecated
    public List<BroadcastReceiver> getLocalReceivers() {
        return mLocalReceivers;
    }

    public void sendLocalBroadcast(Intent intent) {
        mBaseActivity.sendLocalBroadcast(intent);
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
        return mBaseActivity.registerReceiver(receiver, filter);
    }
    /**
     * 
     */
    @TargetApi(26)
	public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler, int flags) {
        if (receiver != null) {
            mReceivers.add(receiver);
        }
        return mBaseActivity.registerReceiver(receiver, filter, broadcastPermission, scheduler, flags);
    }

    @TargetApi(26)
	public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, int flags) {
        if (receiver != null) {
            mReceivers.add(receiver);
        }
        return mBaseActivity.registerReceiver(receiver, filter, flags);
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        if (receiver != null) {
            mReceivers.add(receiver);
        }
        return mBaseActivity.registerReceiver(receiver, filter, broadcastPermission, scheduler);
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (receiver != null) {
            mReceivers.add(receiver);
        }
        return mBaseActivity.registerReceiver(receiver, filter); 
    }

    public void unregisterReceiver(BroadcastReceiver receiver) {
        if (receiver == null || mReceivers.isEmpty()) {
        	mBaseActivity.unregisterReceiver(receiver);
            return;
        }
        Iterator<BroadcastReceiver> itor = mReceivers.iterator();
        while (itor.hasNext()) {
            BroadcastReceiver item = itor.next();
            if (item.equals(receiver)) {
                itor.remove();
            }
        }
        mBaseActivity.unregisterReceiver(receiver);
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
            mBaseActivity.unregisterReceiver(item);
            itor.remove();
        }
    }

    /**
     * 自动解绑服务
     */
    public boolean bindService(Intent service, ServiceConnection conn,
                               int flags)
    {
        if(conn==null){
            return false;
        } 
        this.mServiceConnections.add(conn);
        return mBaseActivity.bindService(service,conn,flags);
    }
    /**
     * 主动解绑服务
     */
    public void unbindService(ServiceConnection conn)
    {
        if(conn==null){
            return;
        }
        Iterator<ServiceConnection> iterator = mServiceConnections.iterator();
        while(iterator.hasNext()){
        	ServiceConnection mConn = iterator.next();
            if(mConn.equals(conn)){
            	mBaseActivity.unbindService(mConn);
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
            mBaseActivity.unbindService(proxyConn);
            iterator.remove();
        }
    }
    /** 
	 * @see android.app.Fragment#onStop()
	 */
	@Override
    @CallSuper
	public void onStop()
	{ 
		super.onStop(); 
		this.isStop=true;
	}
    @Override
    @CallSuper
    public void onStart() {
        super.onStart();
        this.isStop=false;
    }
    @Override
    @CallSuper
    public void onDestroyView() {
        super.onDestroyView();
        isDestroyView=true;
    }

    /**
     * DestroyView之后是Destroy
     */
    public boolean isDestroyView(){
        return isDestroyView;
    }
    /**
     * @deprecated  不要使用isStop判断Fragment是否已经结束,因为onStop之后可能调用onStart,请使用isDestroyView
     */
    public boolean isStop()
	{
		return this.isStop;
	}
    private void tryGetContentView() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            mContentView = this.getView();
        } else {
            try {
                mContentView = (View) this.getClass().getDeclaredField("mView").get(this);
            } catch (Exception igr) {
            }
        }
    }
}
