package cn.zhg.base.inter;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.app.ActivityManager.RunningServiceInfo;
import java.util.List;

/**
 * 服务组件管理工具
 */
public interface IActivityManager extends IContext{
    default ActivityManager getActivityManager(){
        return (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
    }
    /**
     * 服务是否在运行
     * @param clazz
     * @return
     */
    default boolean isServiceRuning(Class<? extends Service> clazz){
        if(clazz==null){
            return false;
        }
        List<ActivityManager.RunningServiceInfo> services = getActivityManager().getRunningServices(Integer.MAX_VALUE);
        if(services==null||services.isEmpty()){
            return false;
        }
        for(ActivityManager.RunningServiceInfo service:services){
            if(clazz.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    /**
     * 返回服务信息
     * @param name 服务名
     * @return
     */
    default ActivityManager.RunningServiceInfo getServiceInfo(Intent name)
    {
        List<RunningServiceInfo> list = getActivityManager()
                .getRunningServices(java.lang.Integer.MAX_VALUE);
        for (RunningServiceInfo r : list)
        {
            if (r.service.equals(name.getComponent()))
            {
                return r;
            }
        }
        return null;
    }
}
