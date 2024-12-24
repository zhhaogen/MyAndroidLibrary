package cn.zhg.basic.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import cn.zhg.basic.filter.FilterModel;
import cn.zhg.basic.sorter.SorterModel;
import cn.zhg.basic.sorter.SortersModel;

/**
 * 可进行排序过滤的列表适配器
 */
public abstract class SortFilterAdapter<DATA> extends BasicAdapter<DATA>
{
	/**
	 * 原始数据
	 */
	private List<DATA> orignalDatas;
	/**
	 * 过滤器
	 */
	private FilterModel<DATA> filter;
	/**
	 *排序器
	 */
	private SorterModel<DATA> sorter;
	public SortFilterAdapter(Context context, List<DATA> datas)
	{
		super(context, datas);
		orignalDatas=new ArrayList<>();
		if(datas!=null){
			orignalDatas.addAll(datas);
		}
	}
	/**
	 * 原始数据
	 */
	public List<DATA> getOrignalDatas()
	{
		return orignalDatas;
	} 
	/**
	 * 过滤器
	 */
	public void setFilter(FilterModel<DATA> filter)
	{
		this.filter = filter;
		this.sortFilter();
	}
	/**
	 *排序器
	 */
	public void setSorter(SorterModel<DATA> sorter)
	{
		this.sorter = sorter;
		this.sortFilter();
	}
	/**
	 * 排序和过滤器
	 */
	public void setSortFilter(SorterModel<DATA> sorter,FilterModel<DATA> filter)
	{
		this.sorter = sorter;
		this.filter = filter;
		this.sortFilter();
	}
	/**
	 * 进行排序和过滤
	 */
	public final void sortFilter(){
		filter();
		sort();
	}
	/**
	 * 原始数据内容发生变化
	 * @param item
	 */
	public final void notifyOrignalDataSetChanged(DATA item)
	{
		int id=this.orignalDatas.indexOf(item);
		if(id!=-1)
		{
			orignalDatas.set(id, item);
			id=this.datas.indexOf(item);
			if(id!=-1)
			{
				datas.set(id, item);
			}
			this.notifyDataSetChanged();
		}
	}
	/**
	 * 原始数据内容大小发生了变化
	 */
	public final void notifyOrignalDataSizeChange()
	{
		this.sortFilter(); 
		this.notifyDataSetChanged();
	}
	/**
	 * 原始数据内容发生了变化,大小未发送变化
	 */
	public final void notifyOrignalDataSetChanged()
	{ 
		// 双缓存
		if (!datas.isEmpty())
		{
//			Logger.d("更新数据");
			List<DATA> newDatas = new ArrayList<>();
			Iterator<DATA> itor = datas.iterator();
			while(itor.hasNext())
			{
				DATA item = itor.next();
				int id=this.orignalDatas.indexOf(item);
				if(id!=-1)
				{
					newDatas.add(this.orignalDatas.get(id));
				}  
			}
			this.datas.clear();
			this.datas.addAll(newDatas);
			newDatas.clear();
			this.notifyDataSetChanged();
		}
	}
	/**
	 * 进行过滤
	 */
	private void filter()
	{
		datas.clear();
//		Logger.d("进行过滤:"+filter);
		if (filter == null || !filter.enable)
		{
			datas.addAll(orignalDatas);
			return;
		} 
		for(DATA data:orignalDatas){
			if (filter.match(data))
			{
				datas.add(data);
			}
		} 
	}
	/**
	 * 进行排序,需要进行过滤后执行
	 */
	private  void sort()
	{
//		Logger.d("进行排序:"+sorter);
		if(this.sorter==null){
			return;
		}
		if(this.sorter instanceof SortersModel){
			SortersModel _sorts= (SortersModel) sorter;
			if(_sorts.isReverse&&_sorts.getSorters().isEmpty()){
				//默认倒叙
				Collections.reverse(datas);
				return;
			}
		}
		Collections.sort(this.datas, new Comparator<DATA>()
		{
			public int compare(DATA o1, DATA o2)
			{
				return sorter.compare(o1, o2);
			}
		}); 
	}
}
