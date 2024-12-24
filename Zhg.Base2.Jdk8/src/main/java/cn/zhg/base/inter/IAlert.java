/**
 * @author zhhaogen
 * 创建于 2017年2月15日 下午9:20:45
 */
package cn.zhg.base.inter;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;

/**
 *对话框工具
 */
public interface IAlert extends IContext {
    /**
     * 弹出对话框
     * @param title 标题
     * @param msg 内容
     */
    default void alert(CharSequence title, CharSequence msg) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
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
     * @param titleResId 标题资源
     * @param msgResId 内容资源
     */
    default void alert(int titleResId, int msgResId) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        if (titleResId != 0) {
            alert.setTitle(titleResId);
        }
        alert.setMessage(msgResId).setNegativeButton(android.R.string.ok, null)
                .create();
        alert.show();
    }

    /**
     * 弹出对话框
     * @param msg 内容
     */
    default void alert(CharSequence msg) {
        alert(null, msg);
    }

    /**
     * 弹出对话框
     */
    default void alert(int msgResId) {
        alert(0, msgResId);
    }
}
