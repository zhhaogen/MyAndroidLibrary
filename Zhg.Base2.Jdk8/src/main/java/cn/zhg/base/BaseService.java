package cn.zhg.base;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BaseService extends Service {
    /**
     * 记录所有广播,方便退出时一次性注销
     */
    private final List<BroadcastReceiver> receivers = new ArrayList<>();
    public IBinder onBind(Intent intent) {
        return null;
    }
    public <T extends BaseApplication>T  getApp()
    {
        return (T) super.getApplication();
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
        return super.registerReceiver(receiver, filter);
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler, int flags) {
        if (receiver != null) {
            receivers.add(receiver);
        }
        return super.registerReceiver(receiver, filter, broadcastPermission, scheduler, flags);
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, int flags) {
        if (receiver != null) {
            receivers.add(receiver);
        }
        return super.registerReceiver(receiver, filter, flags);
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        if (receiver != null) {
            receivers.add(receiver);
        }
        return super.registerReceiver(receiver, filter, broadcastPermission, scheduler);
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (receiver != null) {
            receivers.add(receiver);
        }
        return super.registerReceiver(receiver, filter);
    }

    public void unregisterReceiver(BroadcastReceiver receiver) {
        if (receiver == null || receivers.isEmpty()) {
            super.unregisterReceiver(receiver);
            return;
        }
        Iterator<BroadcastReceiver> itor = receivers.iterator();
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
        if (receivers.isEmpty()) {
            return;
        }
        Iterator<BroadcastReceiver> itor = receivers.iterator();
        while (itor.hasNext()) {
            BroadcastReceiver item = itor.next();
            super.unregisterReceiver(item);
            itor.remove();
        }
    }
    public void onDestroy() {
        unregisterReceivers();
        super.onDestroy();
    }
}
