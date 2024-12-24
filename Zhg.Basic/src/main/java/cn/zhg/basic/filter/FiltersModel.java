package cn.zhg.basic.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * 多个过滤器
 */
public abstract class FiltersModel<T> extends FilterModel<T>
{ 
	private static final long serialVersionUID = 1L;
	protected List<FilterModel<T>> filters;
	public FiltersModel(String name)
	{
		super(name);
		filters=new ArrayList<>();
	}
	/**
	 * 返回过滤器列表
	 */
	public List<FilterModel<T>> getFilters()
	{
		return filters;
	}

	/**
	 * 返回启用的过滤器,不为null 
	 */
	public List<FilterModel<T>> getEnableFilters()
	{
		List<FilterModel<T>> enableFilters = new ArrayList<>();
		if (filters == null || filters.isEmpty())
		{
			return enableFilters;
		}
		for (FilterModel<T> filter : filters)
		{
			if (filter.enable)
			{
				enableFilters.add(filter);
			}
		}
		return enableFilters;
	}
	public void add(FilterModel<T> filter)
	{
		filters.add(filter);
	}
	public void addAll(List<FilterModel<T>> list)
	{
		this.filters.addAll(list);
	}
}
