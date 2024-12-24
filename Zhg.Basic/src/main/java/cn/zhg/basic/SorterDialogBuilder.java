/**
 * @author zhhaogen
 * 创建于 2020年2月28日 下午8:22:29
 */
package cn.zhg.basic;

import java.util.*;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.*;
import android.widget.*;

import cn.zhg.basic.adapter.SorterModelAdapter;
import cn.zhg.basic.bind.DragableListViewBind;
import cn.zhg.basic.inter.EConsumer;
import cn.zhg.basic.sorter.SorterModel;

/**
 *
 */
public class SorterDialogBuilder<T> {

    private List<SorterModel<T>> sorters;
    private List<SorterModel<T>> orignalSorters;
    private Context context;
    private EConsumer<List<SorterModel<T>>> okListener;
    private Runnable cancelListener;

    /**
     * @param context
     */
    public SorterDialogBuilder(Context context) {
        this.context = context;
    }

    /**
     */
    public SorterDialogBuilder<T> setSorters(List<SorterModel<T>> sorters) {
        this.sorters = sorters;
        if (sorters == null) {
            orignalSorters = null;
        } else {
            orignalSorters = new ArrayList<>();
            for (SorterModel<T> sorter : sorters) {
                orignalSorters.add(sorter.clone());
            }
        }
        return this;
    }

    /**
     * 确认回调监听器
     */
    public SorterDialogBuilder<T> setOkListener(EConsumer<List<SorterModel<T>>> listener) {
        okListener = listener;
        return this;
    }

    /**
     * 取消回调监听器
     */
    public SorterDialogBuilder<T> setCancelListener(Runnable listener) {
        cancelListener = listener;
        return this;
    }

    @SuppressLint("InflateParams")
    public AlertDialog create() {
        View view = LayoutInflater.from(context).inflate(R.layout.basic_sort_listview_layout, null, false);
        final ListView listView = view.findViewById(R.id.list_view);
        ViewGroup listViewLayout = view.findViewById(R.id.list_view_layout);
        //设置数据
        SorterModelAdapter<T> adapte = new SorterModelAdapter<>(this.context, sorters);
        listView.setAdapter(adapte);
        //绑定拖动
        new DragableListViewBind(listView,listViewLayout).setDragViewId(R.id.move_btn).enable()
                .setOnDragListener(new DragableListViewBind.OnDragListener() {
                    public void onDragEnd(int startPosition, int endPosition) {
                        //
                        SorterModel<T> item = sorters.get(startPosition);
                        sorters.remove(startPosition);
                        sorters.add(endPosition,item);
                        //
                        listView.setAdapter(
                                new SorterModelAdapter<>(context,
                                        sorters));
                    }
                })
        ;
        final AlertDialog alert = new AlertDialog.Builder(this.context)
                .setView(view)
                .setTitle(R.string.basic_action_sort).setCancelable(false)
                .setNegativeButton(R.string.basic_action_reset, null)
                .setNeutralButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, null)
                .create();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                // setNegativeButton会导致点击完后关闭,使用getButton,
                alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (sorters != null) {
                                    sorters.clear();
                                    for (SorterModel<T> sorter : orignalSorters) {
                                        sorters.add(sorter.clone());
                                    }
                                }
                                listView.setAdapter(
                                        new SorterModelAdapter<>(context,
                                                sorters));
                            }
                        });
                alert.getButton(DialogInterface.BUTTON_NEUTRAL)
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (cancelListener != null) {
                                    cancelListener.run();
                                }
                                alert.dismiss();
                            }
                        });
                alert.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (okListener != null) {
                                    okListener.accept(sorters);
                                }
                                alert.dismiss();
                            }
                        });
            }
        });
        return alert;
    }

    /**
     *
     */
    public void show() {
        create().show();
    }

}
