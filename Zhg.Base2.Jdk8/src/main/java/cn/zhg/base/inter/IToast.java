package cn.zhg.base.inter;

import android.widget.Toast;

/**
 * 提示工具
 */
public interface IToast extends IContext {
    /**
     * 提示内容
     */
    default void tip(CharSequence msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }
    /**
     * 提示内容
     */
    default void tip(int resId){
        Toast.makeText(getContext(),resId,Toast.LENGTH_SHORT).show();
    }
}
