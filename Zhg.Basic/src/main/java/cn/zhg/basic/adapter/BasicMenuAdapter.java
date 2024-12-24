/**
 * @author zhhaogen
 * 创建于 2018年4月30日 下午12:09:12
 */
package cn.zhg.basic.adapter;

import android.content.Context;
import android.support.annotation.MenuRes;
import android.view.*;
import android.widget.*;
import android.widget.PopupMenu.*;

import java.util.*;

import cn.zhg.base.BaseViewHolder;
import cn.zhg.basic.R;

/**
 * 带菜单的列表适配器
 *
 * @param <DATA>
 */
public abstract class BasicMenuAdapter<DATA>
        extends SortFilterAdapter<DATA>
{
    private BasicItemMenuListener<DATA> menuListener;
    private BasicChoiceListener<DATA> choiceListener;
    /**
     * 是否进入选择模式
     */
    private boolean choiceMode;
    /**
     * 记录选中数据
     */
    private Set<DATA> checkDatas;

    public BasicMenuAdapter(Context context, List<DATA> datas)
    {
        super(context, datas);
        checkDatas = new HashSet<>();
    }
    /**
     * 进入选择模式
     */
    public void startChoice()
    {
        startChoice(false);
    }
    /**
     * 进入选择模式
     * @param checkAll 是否初始选中全部,初始化为默认没有选择数据
     */
    public void startChoice(boolean checkAll)
    {
        choiceMode = true;
        if(checkAll&&checkDatas.isEmpty()){
            checkDatas.addAll(datas);
        }
        this.notifyDataSetChanged();
        if(choiceListener!=null){
            choiceListener.onChoiceStart();
        }
    }
    /**
     * 停止选择模式
     */
    public void stopChoice()
    {
        if (datas.isEmpty())
        {
            return;
        }
        choiceMode = false;
        this.notifyDataSetChanged();
        if(choiceListener!=null){
            choiceListener.onChoiceStop();
        }
    }

    /**
     * 选中全部数据(原始数据)
     */
    public void choiceAllOrignalDatas(){
        setChoiceDatas(getOrignalDatas());
    }
    /**
     * 仅选中当前全部数据
     */
    public void choiceAll(){
        setChoiceDatas(datas);
    }
    /**
     * 选中这些数据数据
     */
    public void setChoiceDatas(Collection<DATA> datas){
        checkDatas.clear();
        checkDatas.addAll(datas);
        notifyDataSetChanged();
    }
    /**
     * 清空所有选择
     */
    public void choiceNone(){
        checkDatas.clear();
        notifyDataSetChanged();
    }
    /**
     * 取消选中数据
     */
    public void choiceNone(DATA item){
        if(checkDatas.remove(item)){
            notifyDataSetChanged();
        }
    }
    /**
     * 取消选中数据
     */
    public void choiceNone(Collection<DATA> datas){
        if(checkDatas.removeAll(datas)){
            notifyDataSetChanged();
        }
    }
    /**
     * 取消当前数据
     */
    public void choiceNoneAll(){
        choiceNone(datas);
    }
    /**
     * 追加选中这些数据数据
     */
    public void addChoiceDatas(Collection<DATA> datas){
        checkDatas.addAll(datas);
        notifyDataSetChanged();
    }
    /**
     * 追加选中数据
     */
    public void choice(DATA item){
        checkDatas.add(item);
        notifyDataSetChanged();
    }
    /**
     * 追加选中当前全部数据
     */
    public void addChoiceAll(){
        addChoiceDatas(datas);
    }
    /**
     * 返回选中的数据
     */
    public Set<DATA> getChoiceDatas()
    {
        return checkDatas;
    }

    /**
     * 返回数据选中数量
     */
    public int getChoiceCount()
    {
        return checkDatas.size();
    }

    /**
     * 获取数据视图
     *
     * @param position    数据位置,并不是视图位置
     * @param convertView
     * @param parent
     * @return
     */
    @SuppressWarnings("unchecked")
    protected final View getDataView(final int position, View convertView,
                                     ViewGroup parent)
    {
        final ItemMenuHolder holder;
        if (convertView == null)
        {
            convertView = this.inflater.inflate(R.layout.basic_item_menu, parent,
                    false);
            holder = new ItemMenuHolder(convertView);
            View itemView = onCreateView(holder.itemStubView);
            holder.itemStubView.addView(itemView);
            holder.itemHolder = onCreateViewHolder(itemView);
            convertView.setTag(holder);
        } else
        {
            holder = (ItemMenuHolder) convertView.getTag();
        }
        final DATA item = getDatas().get(position);
        holder.itemHolder.update(item, position);
        holder.selectCheck.setVisibility(choiceMode ? View.VISIBLE : View.GONE);

        holder.selectCheck.setChecked(checkDatas.contains(item));
        bindListener(holder.itemHolder, item, position);
        if (menuListener == null)
        {
            holder.menuView.setVisibility(View.GONE);
        }else{
            holder.menuView.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    PopupMenu pop = new PopupMenu(v.getContext(), v);
                    pop.inflate(onCreateItemMenu(item, position));
                    pop.setOnMenuItemClickListener(new OnMenuItemClickListener()
                    {
                        public boolean onMenuItemClick(
                                MenuItem menuItem)
                        {
                            menuListener.onMenuItemClick(item,
                                    position, menuItem.getItemId());
                            return true;
                        }
                    });
                    pop.show();
                }
            });
        }
       holder.selectCheck.setOnClickListener(new View.OnClickListener()
       {
           public void onClick(View v)
           {
               boolean isChecked=holder.selectCheck.isChecked();
               if(isChecked){
                  checkDatas.add(item);
               }else {
                   checkDatas.remove(item);
               }
               if(choiceListener==null){
                   return;
               }
               choiceListener.onChoiceChange(item,!isChecked);
           }
       });
        return convertView;
    }

    /**
     * @param position 视图位置,并不是数据位置
     */
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        return this.getDataView(position, convertView, parent);
    }

    public final void setMenuListener(BasicItemMenuListener<DATA> menuListener)
    {
        this.menuListener = menuListener;
    }
    public final void setChoiceListener(BasicChoiceListener<DATA> choiceListener){
        this.choiceListener=choiceListener;
    }
    /**
     * 创建元素菜单
     *
     * @param item
     * @param position
     * @return
     */
    protected abstract @MenuRes int onCreateItemMenu(DATA item, int position);

    private final class ItemMenuHolder extends BaseViewHolder
    {
        private BasicViewHolder<DATA> itemHolder;
        private ViewGroup itemStubView;
        private View menuView;
        private CheckBox selectCheck;

        /**
         * @param convertView
         */
        public ItemMenuHolder(View convertView)
        {
            super(convertView);
            itemStubView = getViewById(R.id.itemstub_view);
            menuView = getViewById(R.id.itemmore_btn);
            selectCheck = getViewById(R.id.itemselect_check);
        }
    }

    /**
     * 点击列表项菜单监听器
     *
     * @param <DATA>
     */
    public static interface BasicItemMenuListener<DATA>
    {
        /**
         * @param item
         * @param position
         * @param menuId
         */
        void onMenuItemClick(DATA item, int position, int menuId);
    }
    /**
     * 选择模式监听器
     */
    public static interface BasicChoiceListener<DATA>{
        /**
         * 进入选择模式
         */
        void onChoiceStart();
        /**
         * 退出选择模式
         */
        void onChoiceStop();
        /**
         * 选择发生变化
         */
        void onChoiceChange(DATA item, boolean checked);
    }
}
