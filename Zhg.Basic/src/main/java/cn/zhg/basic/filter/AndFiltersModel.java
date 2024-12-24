package cn.zhg.basic.filter;

import java.util.List;

/**
 * 多个过滤器,并关系
 */
public class AndFiltersModel<T> extends FiltersModel<T>
{  
	private static final long serialVersionUID = 1L; 
	public AndFiltersModel()
	{
		this("AndFilters");
	}
	public AndFiltersModel(String name)
	{
		super(name); 
	}  
	public AndFiltersModel(List<FilterModel<T>> list)
	{
		super("AndFilters"); 
		if(list!=null){
			filters.addAll(list);
		}
	} 
	public boolean match(T data)
	{
		if (filters == null ||filters.isEmpty())
		{
			return true;
		}
		List<FilterModel<T>> enableFilters =this.getEnableFilters();
		if (enableFilters == null || enableFilters.isEmpty())
		{ 
			return false;
		}
		for(FilterModel<T> enableFilter:enableFilters){
			if(!enableFilter.match(data)){
				return false;
			}
		}
		return true;
	}
	public final String toString()
	{
		StringBuilder sb=new StringBuilder();
		sb.append("[");
		if (filters != null )
		{
			for(int i=0,size=filters.size();i<size;i++){
				if(i!=0){
					sb.append(" & ");
				}
				sb.append(filters.get(i));
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
