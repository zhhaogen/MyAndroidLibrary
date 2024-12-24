package cn.zhg.base.inter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具
 */
@SuppressLint("MissingPermission")
public interface IConnectivityManager extends IContext{
    default ConnectivityManager getConnectivityManager(){
        return (ConnectivityManager) getContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);
    }
    /**
     * 获取当前网络类型
     */
    default NetworkInfo getNetworkInfo()
    {
        try
        {
           NetworkInfo mNetworkInfo = getConnectivityManager().getActiveNetworkInfo();
            return mNetworkInfo;
        } catch (Throwable e)
        {

        }
        return null;
    }
    /**
     * 网络是否可用
     */
    default boolean isNetWork()
    {
        NetworkInfo mNetworkInfo = getNetworkInfo();
        if (mNetworkInfo != null)
        {
            return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
        }
        return false;
    }
    /**
     * 网络是否WiFi连接
     */
    default boolean isWIFI()
    {
        NetworkInfo mNetworkInfo = getNetworkInfo();
        if (mNetworkInfo != null)
        {
            return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected()
                    && mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }
}
