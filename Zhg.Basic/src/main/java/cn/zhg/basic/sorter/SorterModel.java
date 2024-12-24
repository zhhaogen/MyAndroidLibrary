/**
 *
 * @author zhhaogen
 * 创建于 2018年4月30日 下午7:38:43
 */
package cn.zhg.basic.sorter;

import java.util.*;

/**
 *
 *排序器
 */
public abstract class SorterModel<T> implements Comparator<T> ,java.io.Serializable,java.lang.Cloneable
{

	/** */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public SorterModel(String name)
	{
		this.name=name;
	}
	/**
	 * 排序器名称
	 */
	public String name;
	/**
	 * 是否反转排序
	 */
	public boolean isReverse;

	public int compare(T o1, T o2)
	{
		int ret=0;
		if(o1==null||o2==null)
        {
			ret=compareNull(o1,o2);
		}else
		{
			  ret = compareNotNull(o1,o2);
		}
		return isReverse?ret:-ret;
	}

	/**
	 * 比较null,参数中至少有一个为null
	 */
	protected int compareNull(Object o1,Object o2){
		if(o1==null&&o2!=null)
        {
			return -1;
		}
		if(o1!=null&&o2==null){
			return 1;
		}
		return 0;
	}
	/**
	 * 比较非null值
	 * @param o1 不为null
	 * @param o2 不为null
	 * @return o1-o2值
	 */
	public abstract int compareNotNull(T o1, T o2) ;

	public SorterModel<T> clone()
	{
		try
		{
			return (SorterModel<T>) super.clone();
		} catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return this;
	}

	public String toString()
	{
		return (this.isReverse?"-":"+")+name;
	}

}
