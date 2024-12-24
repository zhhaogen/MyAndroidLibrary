/**
 * 
 * @author zhhaogen
 * 创建于 2020年2月28日 下午12:46:20
 */
package cn.zhg.basic.sorter;

/**
 * 
 *
 */
public  abstract  class ComparableSorterModel<T> extends SorterModel<T>
{ 
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * @param name
	 */
	public ComparableSorterModel(String name)
	{
		super(name); 
	}

	/**
	 * 获取属性值
	 * @param data 不会为null
	 * @return 可以返回null
	 */
	@SuppressWarnings("rawtypes")
	public abstract Comparable getDataValue(T data) ;
	@SuppressWarnings("unchecked")
	@Override
	public int compareNotNull(T o1, T o2)
	{
		Comparable v1 = getDataValue(o1);
		Comparable v2=getDataValue(o2);
		if(v1==null||v2==null){
			return compareNull(v1,v2);
		}
		  return v1.compareTo(v2);
	}

	 

}
