package cn.zhg.basic;

import android.os.Message;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;

import cn.zhg.basic.annotation.ServiceMethod;
import cn.zhg.basic.annotation.UiMethod;
import cn.zhg.basic.util.ReflectUtil;

final class BasicMessageWrapper {
    /**
     * 后台方法列表
     */
    private Map<Integer, Method> serviceMethods;
    /**
     * ui方法列表
     */
    private Map<Integer, Method> uiMethods;
    /**
     * 所有线程what对应的taskid
     */
    private Map<Integer, Long> taskMap;
    /**
     * 所有线程what对应的Semaphore
     */
    private Map<Integer, Semaphore> semaphoreMap;

    /**
     * 记录当前线程的what-taskid
     */
    private static ThreadLocal<ThreadWhat> threadWhat = new ThreadLocal<>();
    /**
     * 被代理的对象,BasicFragment|BasicActivity
     */
    private Object target;

    public BasicMessageWrapper(Object target, Class<?> superClass) {
        this.target = target;
        serviceMethods = new HashMap<>();
        uiMethods = new HashMap<>();
        taskMap = new HashMap<>();
        semaphoreMap = new HashMap<>();
        // Method[] mths = this.getClass().getDeclaredMethods();
        List<Method> mths = ReflectUtil.getAllMethods(target.getClass(), BasicFragment.class);
        for (Method m : mths) {
            // Logger.d(m.getName());
            UiMethod uiMethodAn = m.getAnnotation(UiMethod.class);
            ServiceMethod serviceMethodAn = m.getAnnotation(ServiceMethod.class);
            if (uiMethodAn != null) {
                int key = uiMethodAn.value();
                putMethod(uiMethods, key, m);
            }
            if (serviceMethodAn != null) {
                int key = serviceMethodAn.value();
                putMethod(serviceMethods, key, m);
            }
        }
    }

    /**
     * 启动线程，调用后台方法
     */
    public void callThread(final int what, final Object... params) {
        final Method mth = serviceMethods.get(what);
        if (mth == null) {
            Log.w("callThread", "未找到该方法,what=" + what);
            return;
        }
        long taskId = System.nanoTime();
        taskMap.put(what, taskId);
        final ThreadWhat tw = new ThreadWhat();
        tw.id = taskId;
        tw.what = what;
        new Thread() {
            public void run() {
                threadWhat.set(tw);
                try {
                    boolean b = mth.isAccessible();
                    if (!b) {
                        mth.setAccessible(true);
                    }
                    mth.invoke(target, params);
                    if (!b) {
                        mth.setAccessible(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 启动线程，调用后台方法，该方法只能同时被一个线程执行
     */
    public void callSameThread(final int what,final Object[] params) {
        final Method mth = serviceMethods.get(what);
        if (mth == null) {
            Log.w("callThread", "未找到该方法,what=" + what);
            return;
        }
        long taskId = System.nanoTime();
        taskMap.put(what, taskId);
        final Semaphore semaphore;
        if (semaphoreMap.containsKey(what)) {
            semaphore = semaphoreMap.get(what);
        } else {
            semaphore = new Semaphore(1);
            semaphoreMap.put(what, semaphore);
        }
        final ThreadWhat tw = new ThreadWhat();
        tw.id = taskId;
        tw.what = what;
        tw.semaphore = semaphore;
        new Thread() {
            public void run() {
                threadWhat.set(tw);
                try {
//                    Log.d("callSameThread", "等待其他线程结束");
                    threadWhat.get().semaphore.acquire();
//                    Log.d("callSameThread", "其他线程结束,availablePermits="+threadWhat.get().semaphore.availablePermits());
                } catch (InterruptedException igr) {
                }
                try {
                    boolean b = mth.isAccessible();
                    if (!b) {
                        mth.setAccessible(true);
                    }
                    mth.invoke(target, params);
                    if (!b) {
                        mth.setAccessible(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    //可能在shouldStopThread已经被释放了
                    ThreadWhat   tw=threadWhat.get();
                    Semaphore s =tw .semaphore;
                    if(s!=null){
                        s.release();
                        tw.semaphore=null;
                    }else{
//                        Log.d("callSameThread", "shouldStopThread已经被释放了" );
                    }
                }
            }
        }.start();
    }
    /**
     * 停止线程，不再发送消息
     * 参见shouldStopThread、shouldSkipMessage
     */
    public void stopThread(int what) {
        taskMap.put(what,0L);
    }
    /**
     * 当前线程是否应该被停止
     */
    public boolean shouldStopThread() {
        ThreadWhat tWhat = threadWhat.get();
        if (tWhat == null) {
            //no happend
            Log.w("shouldStopThread", "no happend!");
            return false;
        }
        Long currentId = taskMap.get(tWhat.what);
        if (tWhat.id != currentId) {
//            Log.w("shouldStopThread", "任务已经过时,threadWhat=" + threadWhat + ",myId=" + tWhat.id + ",newId=" + currentId);
            if( tWhat.semaphore!=null){
                tWhat.semaphore.release();
                tWhat.semaphore=null;
            }
            return true;
        }
        return false;
    }

    /**
     * 消息是否应该被跳过
     */
    @WorkerThread
    public boolean shouldSkipMessage(int what) {
        ThreadWhat tWhat = threadWhat.get();
        if (tWhat == null) {
            //no happend
            Log.w("shouldSkipMessage", "no happend!");
            return false;
        }
        Long currentId = taskMap.get(tWhat.what);
        if (tWhat.id != currentId) {
//				Logger.d("任务已经过时,threadWhat=" + threadWhat + ",currentId="
//						+ currentId);
            return true;
        }
        return false;
    }

    public boolean handleMessage(Message msg) {
        Method mth = this.uiMethods.get(msg.what);
        if (mth == null) {
            Log.w("handleMessage", "未找到该方法,what=" + msg.what);
            return false;
        }
        try {
            boolean b = mth.isAccessible();
            if (!b) {
                mth.setAccessible(true);
            }
            Class<?>[] parameterTypes = mth.getParameterTypes();
            if (parameterTypes.length == 0) {
                mth.invoke(target);
            } else if (parameterTypes.length == 1) {
                mth.invoke(target, msg.obj);
            } else {
                mth.invoke(target, (Object[]) msg.obj);
            }
            if (!b) {
                mth.setAccessible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 添加方法到map，如果方法重复则替换旧的
     */
    private void putMethod(Map<Integer, Method> map, int key, Method m) {
        if (!map.containsKey(key)) {
            map.put(key, m);
            return;
        }
        Method mth = map.get(key);
        if (mth.getDeclaringClass()
                .isAssignableFrom(m.getDeclaringClass())) {
            map.put(key, m);
        }
    }

    private static class ThreadWhat {
        public Semaphore semaphore;
        int what;
        long id;

        public String toString() {
            return "ThreadWhat{what=" + what + ",id=" + id + "}";
        }
    }

}
