/**
 * 
 * @author zhhaogen
 * 创建于 2018年4月30日 下午7:38:43
 */
package cn.zhg.basic.sorter;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 *多个排序器集合
 */
public class SortersModel<T> extends SorterModel<T>
{

	/** */
	private static final long serialVersionUID = 1L;
	private List<SorterModel<T>> sorters;
	public SortersModel()
	{
		this("Sorters");
	} 
	/**
	 * 
	 */
	public SortersModel(String name)
	{
		super(name);
		sorters=new ArrayList<>();
	} 
	public SortersModel(List<SorterModel<T>> list)
	{
		super("Sorters");
		sorters=new ArrayList<>();
		if(list!=null){
			sorters.addAll(list);
		}
	} 
	public List<SorterModel<T>> getSorters()
	{
		return sorters;
	}
	public void add(SorterModel<T> item){
		sorters.add(item);
	}
	public void addAll(List<SorterModel<T>> list){
		sorters.addAll(list);
	}
	@Override
	public int compareNotNull(T o1, T o2)
	{
		// 多条件排序
		int ret = 0;
		for(SorterModel<T> sorter:sorters){
			ret = sorter.compareNotNull(o1, o2);
			ret=sorter.isReverse?ret:-ret;
			if (ret != 0)
			{
				break;
			}
		} 
		return ret; 
	}
	 
	public String toString()
	{
		StringBuilder sb=new StringBuilder();
		if(this.isReverse){
			sb.append("-");
		}else{
			sb.append("+");
		}
		sb.append("[");
		if (sorters != null )
		{
			for(int i=0,size=sorters.size();i<size;i++){
				if(i!=0){
					sb.append(" & ");
				}
				sb.append(sorters.get(i));
			}
		}
		sb.append("]");
		return sb.toString(); 
	}
}
