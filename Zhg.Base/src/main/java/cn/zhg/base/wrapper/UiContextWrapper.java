package cn.zhg.base.wrapper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.support.annotation.StringRes;

/**
 * ui相关扩展,由于tip能在服务上使用,不在此类中<br>
 * 需要调用onDestroy
 */
public class UiContextWrapper extends ContextWrapper {

    /**
     * 进度对话框
     */
    protected ProgressDialog progressDialog;
    public UiContextWrapper(Context base) {
        super(base);
    }

    /**
     * @needcall
     */
    public void onDestroy() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
    /**
     * 显示进度对话框,不确定进度值,可取消
     *
     * @param msg    消息内容
     */
    public void showProcess(CharSequence msg) {
        showProcess(msg, true, null);
    }
    /**
     * 显示进度对话框,不确定进度值
     *
     * @param msg    消息内容
     * @param cancle 是否可以取消
     */
    public void showProcess(CharSequence msg, boolean cancle) {
        showProcess(msg, cancle, null);
    }

    /**
     * 显示进度对话框,不确定进度值
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(listener);
        progressDialog.setCancelable(cancle);
        progressDialog.show();
    }
    /**
     * 显示进度对话框,确定进度值,不可取消
     *
     * @param msg    消息内容
     * @param max    最大进度值
     */
    public void showProcess(CharSequence msg,int max) {
        showProcess(msg,max, (DialogInterface.OnDismissListener)null);
    }

	/**
	 * 显示进度对话框,确定进度值,不可取消
	 * 
	 * @param msg
	 *            消息内容
	 * @param max
	 *            最大进度值
	 * @param listener
	 *            进度完成时监听器
	 */
	public void showProcess(CharSequence msg, int max,
			DialogInterface.OnDismissListener listener)
	{
		if (progressDialog != null)
		{
			progressDialog.dismiss();
		}
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(msg);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setOnDismissListener(listener);
		progressDialog.setCancelable(false);
		progressDialog.setMax(max);
		progressDialog.show();
	}
	/**
	 * 显示进度对话框,确定进度值,可取消
	 * 
	 * @param msg
	 *            消息内容
	 * @param max
	 *            最大进度值
	 * @param listener
	 *            进度完成时监听器
	 */
	public void showProcess(CharSequence msg, int max,
			DialogInterface.OnCancelListener listener)
	{
		if (progressDialog != null)
		{
			progressDialog.dismiss();
		}
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(msg);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setOnCancelListener(listener);
		progressDialog.setCancelable(true);
		progressDialog.setMax(max);
		progressDialog.show();
	}
	/**
	 * 更新进度值
	 */
	public void updateProcess(int current)
	{
		if (progressDialog != null&&progressDialog.isShowing())
		{ 
			progressDialog.setProgress(current );
		}
	}
    /**
     * 取消进度对话框
     */
    public void hideProcess() {
        if (progressDialog != null) {
            //不要使用hide()否则被遮住无效
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
    /**
     * 弹出对话框
     *
     * @param title 标题
     * @param msg   内容
     */
    public void alert(CharSequence title, CharSequence msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        if (title != null) {
            alert.setTitle(title);
        }
        alert.setTitle(title)
                .setMessage(msg).setNegativeButton(android.R.string.ok, null)
                .create();
        alert.show();
    }

    /**
     * 弹出对话框
     *
     * @param titleResId 标题资源
     * @param msgResId   内容资源
     */
    public void alert(@StringRes int titleResId,@StringRes int msgResId) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        if (titleResId != 0) {
            alert.setTitle(titleResId);
        }
        alert.setMessage(msgResId).setNegativeButton(android.R.string.ok, null)
                .create();
        alert.show();
    }

    /**
     * 弹出对话框
     *
     * @param msg 内容
     */
    public void alert(CharSequence msg) {
        alert(null, msg);
    }

    /**
     * 弹出对话框
     */
    public void alert(@StringRes int msgResId) {
        alert(0, msgResId);
    }
}
