package cn.zhg.base;

import android.view.View;

public class BaseViewHolder {
    public final View itemView;
    public BaseViewHolder(View itemView)
    {
        this.itemView=itemView;
    }
    @SuppressWarnings("unchecked")
    public final <T extends View> T getViewById(int id)
    {
        return (T) itemView.findViewById(id);
    }
}
