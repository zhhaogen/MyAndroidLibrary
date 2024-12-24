/**
 * @author zhhaogen
 * 创建于 2018年4月30日 下午12:09:12
 */
package cn.zhg.basic.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.*;

import cn.zhg.basic.R;


/**
 * 带向上按钮和菜单
 * @param <DATA>
 */
public abstract class BasicMenuUpAdapter<DATA>
        extends BasicMenuAdapter<DATA> {
    /**
     * 隐藏上一级
     */
    private boolean isHideUp;
    /**
     * 上一级点击监听器
     */
    private BasicUpListener upListener;

    public BasicMenuUpAdapter(Context context, List<DATA> datas) {
        super(context, datas);
    }

    @Override
    public int getCount() {
        if (isHideUp) {
            return super.getCount();
        }
        return super.getCount() + 1;
    }

    public DATA getItem(int position) {
        if (isHideUp) {
            return super.getItem(position);
        }
        if (position == 0) {
            return null;
        }
        return super.getItem(position - 1);
    }

    /**
     * 返回值必须是IGNORE_ITEM_VIEW_TYPE,0~getViewTypeCount()-1
     * @see android.widget.BaseAdapter#getItemViewType(int)
     */
    public int getItemViewType(int position) {
        if (isHideUp) {
            return super.getItemViewType(position);
        }
        if (position == 0) {
            return 0;
        }
        return 1;
    }

    public int getViewTypeCount() {
        if (isHideUp) {
            return super.getViewTypeCount();
        }
        return 2;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (isHideUp) {
            return super.getDataView(position, convertView, parent);
        }
        if (position == 0) {
            if (convertView == null) {
                convertView = this.inflater.inflate(R.layout.basic_item_up, parent,
                        false);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (upListener != null) {
                        upListener.onUp();
                    }
                }
            });
            return convertView;
        }
        convertView = this.getDataView(position - 1, convertView, parent);
        return convertView;
    }

    /**
     * @param upListener 设置 upListener
     */
    public final void setUpListener(BasicUpListener upListener) {
        this.upListener = upListener;
    }

    /**
     * 隐藏上一级
     */
    public boolean isHideUp() {
        return isHideUp;
    }

    /**
     * 隐藏上一级
     */
    public void setHideUp(boolean isHideUp) {
        this.isHideUp = isHideUp;
    }

    /**
     * 点击向上一级监听器
     *
     */
    public static interface BasicUpListener {
        void onUp();
    }
}
