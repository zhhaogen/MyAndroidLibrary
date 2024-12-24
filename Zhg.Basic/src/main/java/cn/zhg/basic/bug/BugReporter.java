package cn.zhg.basic.bug;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import java.io.*;
import java.util.*;

/**
 * 异常报告工具
 */
@SuppressWarnings("deprecation")
public final class BugReporter implements Thread.UncaughtExceptionHandler {
    public static final String TAG=BugReporter.class.getSimpleName();
    private static BugReporter ins;
    private final Context context;
    private final Thread.UncaughtExceptionHandler defaultHandler;
    private Activity lastActivityCreated;
    /**
     * bug提交网址
     */
    private  String url;
    /**
     * 是否自动提交bug
     */
    private  boolean auto;
    /**
     * 额外记录的东西
     */
    private final Map<String,Object> extras;
    private BugReporter(Context context){
        this.context=context;
        extras=new LinkedHashMap<>();
        listenLastActivity();
        defaultHandler=Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private void listenLastActivity() {
        if (getSdkInt() < 14) {
            return;
        }
        Application app;
        if(context instanceof Application)
        {
            app = (Application) context;
        }else if(context instanceof Activity)
        {
            app=((Activity)context).getApplication();
        }else if(context instanceof Service)
        {
            app=((Service)context).getApplication();
        }else
        {
            Log.w(TAG, "不支持的Context类型");
            return;
        }
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks()
        {

            public void onActivityCreated(Activity activity,
                                          Bundle savedInstanceState)
            {

                if (!(activity instanceof BugReporterActivity))
                {
                    lastActivityCreated = activity;
                }
            }

            public void onActivityStarted(Activity activity)
            {
            }

            public void onActivityResumed(Activity activity)
            {
            }

            public void onActivityPaused(Activity activity)
            {
            }

            public void onActivityStopped(Activity activity)
            {
            }

            public void onActivitySaveInstanceState(Activity activity,
                                                    Bundle outState)
            {
            }

            public void onActivityDestroyed(Activity activity)
            {
            }
        });
    }

    /**
     * 初始化,放在Application类中
     * @param context
     * @return
     */
    public static BugReporter init(Context context)
    {
        if(ins==null){
            ins=new BugReporter(context);
        }
        return ins;
    }
    /**
     * bug提交网址
     */
    public String getUrl() {
        return url;
    }
    /**
     * bug提交网址
     */
    public BugReporter setUrl(String url) {
        this.url = url;
        return this;
    }
    /**
     * 是否自动提交bug
     */
    public boolean isAuto() {
        return auto;
    }
    /**
     * 是否自动提交bug
     */
    public BugReporter setAuto(boolean auto) {
        this.auto = auto;
        return this;
    }
    /**
     * 添加额外记录的东西
     */
    public BugReporter addExtra(String key,Object value) {
        this.extras.put(key,value);
        return this;
    }
    /**
     * 获取SDK版本
     */
    private static int getSdkInt()
    {
        try
        {
             return Build.VERSION.SDK_INT;
        } catch (Throwable e)
        {
           return Integer.parseInt(Build.VERSION.SDK);
        }
    }

    public void uncaughtException(final Thread t,final Throwable e) {
        Log.i(TAG, "获取异常信息");
        try
        {
            new Thread()
            {
                public void run()
                {
                    //保存异常内容到文件
                    String filePath=writeCrashFile(t,e);
                    showDialog(filePath);
                    killApplication();
                }
            }.start();
        } catch (Throwable igr)
        {
            if (defaultHandler != null)
            {
                defaultHandler.uncaughtException(t, e);
            }
        }
    }

    /**
     * 弹窗
     */
    private void showDialog(String filePath) {
        try
        {
            Intent dialogIntent = new Intent(context, BugReporterActivity.class);
            dialogIntent.putExtra(BugReporterActivity.EXTRA_PATH, filePath);
            dialogIntent.putExtra(BugReporterActivity.EXTRA_AUTO, isAuto());
            dialogIntent.putExtra(BugReporterActivity.EXTRA_URL, getUrl());
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(dialogIntent);
        }catch(Throwable ex)
        {
            Log.w(TAG, BugReporterActivity.class.getCanonicalName()+"是否已经在AndroidManifest.xml注册?");
        }
    }
    /**
     * 关闭应用程序
     */
    private void killApplication()
    {
        Log.d(TAG, "结束应用程序 ");
        if (lastActivityCreated != null)
        {
            Log.i(TAG,
                    "杀死最后一个Activity" + lastActivityCreated.getClass());
            lastActivityCreated.finish();
            lastActivityCreated = null;
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(8);

    }
    private String writeCrashFile(Thread t, Throwable e) {
        try {
            long now=System.currentTimeMillis();
            File file = File.createTempFile("crash-" + now, ".txt");
            Log.d(TAG,"写入到"+file);
            PrintWriter writer=new PrintWriter(new FileWriter(file));
            // 设备信息收集
            writer.println("Time:" + now);
            writeAppInfo(writer);
            if(t==null){
                writer.println("Thread:null");
            }else{
                writer.println("ThreadName:"+t.getName());
                writer.println("ThreadId:"+t.getId());
            }
            if(!extras.isEmpty()){
                Set<String> keys = extras.keySet();
                for (String key:keys){
                    writer.println(key+":"+extras.get(key));
                }
            }
            writeStackTrace(writer,e);
            writer.close();
            return file.getPath();
        } catch(Throwable igr) {
            Log.e(TAG,"收集异常发生错误,"+igr.getMessage());
        }
        return null;
    }

    private void writeStackTrace(PrintWriter writer, Throwable e) {
        e.printStackTrace();
        writer.println();
        e.printStackTrace(writer);
    }

    private void writeAppInfo(PrintWriter writer) {
        PackageManager pm = context.getPackageManager();
        String packageName=context.getPackageName();
        writer.println("PackageName:" + packageName);
        try{
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            writer.println("VersionCode:" + packageInfo.versionCode);
            writer.println("VersionName:" + packageInfo.versionName);
        }catch (Throwable igr){

        }
        try{
            writer.println("DeviceName:" + Build.DEVICE);
        }catch (Throwable igr){

        }
        writer.println("DeviceSdk:" + getSdkInt());
        try{
            writer.println("AndroidId:" + Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID));
        }catch (Throwable igr){

        }
    }
}
