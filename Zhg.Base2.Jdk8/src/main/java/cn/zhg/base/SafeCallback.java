/**
 * @author zhg
 * 创建于 2015年9月30日 上午11:00:31
 */
package cn.zhg.base;

/**
 * @author zhg
 *
 */
import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Message;

/** Callback that decouples the wrapped Callback via WeakReference */
public class SafeCallback implements Handler.Callback
{
	private final WeakReference<Handler.Callback> mCallback;

	public SafeCallback(Handler.Callback callback)
	{
		mCallback = new WeakReference<Handler.Callback>(callback);
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		Handler.Callback callback = mCallback.get();
		if (callback != null)
		{
			return callback.handleMessage(msg);
		}else
		{
			 
		}
		// else warn, return true, ..?
		return false;
	}

	public void clear()
	{
		mCallback.clear();
	}
}
