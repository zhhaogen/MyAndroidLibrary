package cn.zhg.basic;

import android.app.ActionBar;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.CallSuper;

import java.util.*;

import cn.zhg.basic.annotation.FragmentClass;
import cn.zhg.basic.util.ReflectUtil;

/**
 * 处理注解的Activity
 */
public class BasicActivity extends cn.zhg.base.BasicActivity {
    /**
     * fragment列表
     */
    private List<FragmentHolder> fragmentClasses;
    /**
     * 当前Fragment
     */
    private BasicFragment currentFragement;
    private BasicMessageWrapper wrapper;
    @SuppressWarnings("unchecked")
    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentClasses = new LinkedList<>();
        wrapper =new BasicMessageWrapper(this,
                BasicActivity.class);
        List<String> clazzes = ReflectUtil.scanAll(this);
        for (final String clazz : clazzes) {
            try {
                Class<?> cls = Class.forName(clazz, false, getClassLoader());
                if(!BasicFragment.class.isAssignableFrom(cls)){
                    continue;
                }
                FragmentClass fragmentAn = cls.getAnnotation(FragmentClass.class);
                if (fragmentAn != null) {
                    FragmentHolder item=new FragmentHolder();
                    item.id=fragmentAn.value();
                    item.cls= (Class<? extends BasicFragment>) cls;
                    item.titleRes=fragmentAn.titleRes();
                    fragmentClasses.add(item);
                    continue;
                }
                BasicFragment f = (BasicFragment) cls.newInstance();
                int fragmentId = f.getFragmentId();
                if(fragmentId==0){
                    continue;
                }
                int fragmentTitle = f.getTitleRes();
                FragmentHolder item=new FragmentHolder();
                item.id=fragmentId;
                item.titleRes=fragmentTitle;
                item.cls= (Class<? extends BasicFragment>) cls;
                fragmentClasses.add(item);
            } catch (Throwable igr) {
//                igr.printStackTrace();
//                Logger.d(clazz);
            }
        }
    }

    /**
     * 打开fragment
     *
     * @param what Fragement的id,@Fragment的value
     */
    public void showFragment(int what) {
        this.showFragment(what, null);
    }

    /**
     * 打开fragment
     *
     * @param what Fragement的id,@Fragment的value
     * @param args 参数
     */
    public void showFragment(int what, Bundle args) {
        FragmentHolder holder =null;
        for(FragmentHolder h:fragmentClasses){
            if(h.id==what){
                holder=h;
                break;
            }
        }
        if (holder == null) {
//			Logger.d("未找到Fragment,what="+what);
            return;
        }
        if(holder.titleRes!=0){
            this.setTitle(holder.titleRes);
        }
        showFragment(holder.cls,String.valueOf(what),args);
    }
    /**
     * 打开fragment
     */
    public void showFragment(Class<? extends BasicFragment> clazz){
        showFragment(clazz,null,null);
    }
    /**
     * 打开fragment
     */
    public void showFragment(Class<? extends BasicFragment> clazz,String tag,Bundle args){
        try {
            BasicFragment baseFragment = (BasicFragment) clazz.newInstance();
            for(FragmentHolder h:fragmentClasses){
                if(h.cls.equals(clazz)){
                    if(h.titleRes!=0){
                        setTitle(h.titleRes);
                    }
                    break;
                }
            }
            showFragment(baseFragment,tag,args);
        } catch (Exception igr) {
            igr.printStackTrace();
        }
    }
    /**
     * 打开fragment
     */
    protected void showFragment(BasicFragment baseFragment, String tag, Bundle args) {
        if(baseFragment==currentFragement){
            return;
        }
        if (args != null) {
            baseFragment.setArguments(args);
        }
        baseFragment.setHasOptionsMenu(true);
        replaceFragment(R.id.container,baseFragment,tag);
        currentFragement=baseFragment;
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
     *
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

    public BasicFragment getCurrentFragement() {
        return currentFragement;
    }
    @CallSuper
    @Override
    public void onBackPressed() {
        if (currentFragement != null && currentFragement.onBackPressed()) {
            return;
        }
        this.finish();
    }
    public void setSubtitle(CharSequence subtitle)
	{
		ActionBar bar = this.getActionBar();
		if (bar != null)
		{
			bar.setSubtitle(subtitle);
		}
	}
    private class FragmentHolder{
        Class<? extends BasicFragment> cls;
        int id;
        int titleRes;
    }
}
