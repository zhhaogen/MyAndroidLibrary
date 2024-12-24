package cn.zhg.basic;

import android.os.*;
import android.support.annotation.CallSuper;

import cn.zhg.base.BaseFragment;

public class BasicFragment extends BaseFragment  {
    private BasicMessageWrapper wrapper;
    @CallSuper
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        wrapper =new BasicMessageWrapper(this,
                BasicFragment.class);
    }

    /**
     * 调用后台线程
     */
    public void callThread(final int what, final Object... params)
    {
        wrapper.callThread(what,params);
    }
    /**
     * 停止线程，不再发送消息
     * 参见shouldStopThread、sendSameMessage
     */
    protected void stopThread(final int what)
    {
        wrapper.stopThread(what);
    }
    /**
     * 调用同一后台线程，替换sendSameMessage
     */
    protected void callSameThread(final int what, final Object... params)
    {
        wrapper.callSameThread(what,params);
    }
    /**
     * 当前线程是否应该被停止
     */
    protected boolean shouldStopThread()
    {
        return  wrapper.shouldStopThread();
    }
    /**
     * 同一任务类型<br>
     * 假设流程，doTask->new Thread->sendMessage(1)<br>
     *那么多次执行doTask，将导致不同线程都在sendMessage(1)，将会引起handleMessage的混乱<br>
     * 解决方案一，就是执行doTask时，应该等待终止前面的thread结束，才能继续执行。
     * 另外解决方案，就是使用sendSameMessage，只会发送最新线程的消息，旧的线程消息将被抛弃
     */
    protected void sendSameMessage(int what, Object ...objs)
    {
        if(wrapper.shouldSkipMessage(what)){
            return;
        }
        this.sendMessage(what,objs);
    }
    protected void sendMessage(int what, Object ...objs)
    {
        if(objs==null||objs.length==0){
            super.sendEmptyMessage(what);
            return;
        }
        if(objs.length==1){
            super.sendMessage(what,objs[0]);
            return;
        }
        super.sendMessage(what,objs);
    }


    @CallSuper
    @Override
    public boolean handleMessage(Message msg)
    {
        wrapper.handleMessage(msg);
        return super.handleMessage(msg);
    }
    /**
     * @return 是否执行返回动作
     */
    public boolean onBackPressed()
    {
        return false;
    }
    /**
     * 返回初始化
     */
    protected int getTitleRes(){
        return 0;
    }
    /**
     * 返回当前页id,0则不标记
     */
    protected int getFragmentId(){
        return 0;
    }
}
