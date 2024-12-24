/**
 * 
 * @author zhhaogen
 * 创建于 2018年5月2日 上午11:44:53
 */
package cn.zhg.basic.filter;

import cn.zhg.basic.inter.IMatcher;

/**
 * 
 * 过滤器
 */
public abstract class  FilterModel<T>  implements IMatcher<T>,java.io.Serializable,java.lang.Cloneable
{
	/** */
	private static final long serialVersionUID = 1L;
	/**
	 * 过滤器名称
	 */
	public String name;

	/**
	 * 启用过滤
	 */
	public boolean enable=true;
	public FilterModel(String name)
	{
		this.name = name;  
	}
	@Override
	public String toString()
	{
		return name+(enable?"":"(禁用)");
	} 
	public FilterModel<T> clone()  
	{
		try
		{
			return (FilterModel<T>) super.clone();
		} catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return this;
	}   
	
}
