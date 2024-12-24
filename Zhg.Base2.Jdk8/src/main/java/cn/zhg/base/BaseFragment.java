package cn.zhg.base;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class BaseFragment extends Fragment implements  Handler.Callback {
    /**
     * 消息处理
     */
    private Handler mBaseHandler;
    /**
     * 储存发送消息code
     */
    private HashSet<Integer> mWhats;
    /**
     * 消息处理回调
     */
    private SafeCallback mCallBack;
    /**
     * 所属activity
     */
    private BaseActivity mBaseActivity;
    protected ProgressDialog progressDialog;
    /**
     * 记录所有广播,方便退出时一次性注销
     */
    private List<BroadcastReceiver> receivers;
    public void onAttach(Context context) {
        this.mBaseActivity = (BaseActivity) context;
        super.onAttach(context);
    }
    public void onCreate(Bundle savedInstanceState) {
        mWhats = new HashSet<Integer>();
        mCallBack = new SafeCallback(this);
        mBaseHandler = new Handler(mCallBack);
        receivers=new ArrayList<>();
        super.onCreate(savedInstanceState);
    }
    public void onDestroy() {
        if(mWhats!=null){
            for (int what : mWhats) {
                mBaseHandler.removeMessages(what);
            }
        }
        if (mCallBack != null) {
            mCallBack.clear();
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        unregisterReceivers();
        super.onDestroy();
    }

    /**
     * 返回所属activity
     */
    public <T extends BaseActivity> T getBaseActivity()
    {
        if (mBaseActivity == null&&android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
        {
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
     * @see @link{#handleMessage(Message) }
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
     * 显示进度对话框
     *
     * @param msg    消息内容
     * @param cancle 是否可以取消
     */
    public void showProcess(CharSequence msg, boolean cancle) {
        showProcess(msg, cancle, null);
    }

    /**
     * 显示进度对话框
     *
     * @param msg      消息内容
     * @param cancle   是否可以取消
     * @param listener 取消监听器
     */
    public void showProcess(CharSequence msg, boolean cancle,
                            DialogInterface.OnCancelListener listener) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(this.getBaseActivity());
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(listener);
        progressDialog.setCancelable(cancle);
        progressDialog.show();
    }

    public void hideProcess() {
        if (progressDialog != null) {
            //不要使用hide()否则被遮住无效
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
    public Intent registerReceiver(BroadcastReceiver receiver, String... actions) {
        if (receiver == null || actions == null) {
            return null;
        }
        receivers.add(receiver);
        IntentFilter filter = new IntentFilter();
        for(String action:actions){
            filter.addAction(action);
        }
        return getBaseActivity().registerReceiver(receiver, filter);
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler, int flags) {
        if (receiver != null) {
            receivers.add(receiver);
        }
        return getBaseActivity().registerReceiver(receiver, filter, broadcastPermission, scheduler, flags);
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, int flags) {
        if (receiver != null) {
            receivers.add(receiver);
        }
        return getBaseActivity().registerReceiver(receiver, filter, flags);
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        if (receiver != null) {
            receivers.add(receiver);
        }
        return getBaseActivity().registerReceiver(receiver, filter, broadcastPermission, scheduler);
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (receiver != null) {
            receivers.add(receiver);
        }
        return getBaseActivity().registerReceiver(receiver, filter);
    }

    public void unregisterReceiver(BroadcastReceiver receiver) {
        if (receiver == null || receivers.isEmpty()) {
            getBaseActivity().unregisterReceiver(receiver);
            return;
        }
        Iterator<BroadcastReceiver> itor = receivers.iterator();
        while (itor.hasNext()) {
            BroadcastReceiver item = itor.next();
            if (item.equals(receiver)) {
                itor.remove();
            }
        }
        getBaseActivity().unregisterReceiver(receiver);
    }

    /**
     * 注销所有广播
     */
    protected void unregisterReceivers() {
        if (receivers.isEmpty()) {
            return;
        }
        Iterator<BroadcastReceiver> itor = receivers.iterator();
        BaseActivity that = getBaseActivity();
        while (itor.hasNext()) {
            BroadcastReceiver item = itor.next();
            that.unregisterReceiver(item);
            itor.remove();
        }
    }
    /**
     * 处理消息内容
     * @see android.os.Handler.Callback#handleMessage(android.os.Message)
     */
    public boolean handleMessage(Message msg)
    {
        // TODO
        return false;
    }
}
