/**
 * 
 * @author zhhaogen
 * 创建于 2018年4月30日 下午12:09:12
 */
package cn.zhg.basic.adapter;

import java.util.List;

import android.content.Context; 
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @param <DATA>
 */
public abstract class BasicAdapter<DATA> extends BaseAdapter
{ 
	protected Context context;
	protected LayoutInflater inflater;
	protected List<DATA> datas;   
	protected BasicItemClickListener<DATA> itemClickListener;
	protected BasicItemLongClickListener<DATA> itemLongClickListener;
	public BasicAdapter(Context context, List<DATA> datas)
	{
		this.context = context;
		this.inflater = LayoutInflater.from(context); 
		this.datas=datas;
	} 
	/**
	 * 列表数据
	 */
	public  List<DATA> getDatas()
	{
		return datas;
	}
	 
	public BasicItemClickListener<DATA> getItemClickListener()
	{
		return itemClickListener;
	} 
	public void setItemClickListener(BasicItemClickListener<DATA> itemClickListener)
	{
		this.itemClickListener = itemClickListener;
	} 
	public BasicItemLongClickListener<DATA> getItemLongClickListener()
	{
		return itemLongClickListener;
	}
	/**
	 * @param itemLongClickListener  itemLongClickListener
	 */
	public void setItemLongClickListener(
			BasicItemLongClickListener<DATA> itemLongClickListener)
	{
		this.itemLongClickListener = itemLongClickListener;
	}
	public   int getCount()
	{
		if(datas==null){
			return 0;
		}
		return datas.size()  ;
	}
	public   DATA getItem(int position)
	{
		if(datas==null){
			return null;
		}
		return datas.get(position);
	}

	public   long getItemId(int position)
	{
		return position;
	} 
	@SuppressWarnings("unchecked")
	public   View getView(final int position, View itemView, ViewGroup parent)
	{ 
		final BasicViewHolder<DATA> holder;
		if (itemView == null)
		{
			itemView = onCreateView(parent);
			holder = onCreateViewHolder(itemView);
			itemView.setTag(holder);
		} else
		{
			holder = (BasicViewHolder<DATA>) itemView.getTag();
		}
		DATA item = this.getItem(position);
		bindListener(holder,item, position);
		holder.update(item, position ); 
		return itemView;
	}

	/**
	 * 绑定监听器
	 * 
	 * @param holder
	 * @param item
	 * @param position
	 */
	protected void bindListener(final BasicViewHolder<DATA> holder,
			final DATA item, final int position)
	{
		if(itemClickListener!=null){
			holder.itemView.setOnClickListener(new View.OnClickListener()
			{ 
				public void onClick(View v)
				{
					if(itemClickListener==null){
						return;
					}
					itemClickListener.onItemClick(holder, item, position);
				}
			});
		}
		if(itemLongClickListener!=null){
			holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
			{ 
				public boolean onLongClick(View v)
				{
					if(itemLongClickListener==null){
						return false;
					}
					return itemLongClickListener.onItemLongClick(holder, item, position);
				}
			});
		}
	}
	/**
	 * 创建视图
	 * 
	 * @param parent
	 * @return
	 */
	protected   abstract View onCreateView(ViewGroup parent);

	/**
	 * 创建视图缓存 
	 */
	protected abstract  BasicViewHolder<DATA> onCreateViewHolder(View itemView);
	/**
	 * 视图缓存
	 */
	public static abstract class  BasicViewHolder<DATA> extends cn.zhg.base.BaseViewHolder
	{ 
		public BasicViewHolder(View itemView)
		{
			super(itemView);
		}

		public abstract void update(DATA item, int position);
	}
	/**
	 *点击列表项监听器  
	 */
	public static interface   BasicItemClickListener<DATA>{ 
		/**
		 * @param holder
		 * @param item
		 * @param position
		 */
		void onItemClick(BasicViewHolder<DATA> holder, DATA item, int position);
	}
	/**
	 *长按点击列表项监听器  
	 */
	public static interface   BasicItemLongClickListener<DATA>{ 
		/**
		 * @param holder
		 * @param item
		 * @param position
		 * @return 参考{@link View.OnLongClickListener#onLongClick}
		 */
		boolean onItemLongClick(BasicViewHolder<DATA> holder, DATA item, int position);
	}
}
