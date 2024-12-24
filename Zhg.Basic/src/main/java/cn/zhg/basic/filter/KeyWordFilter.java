package cn.zhg.basic.filter;
/**
 * 关键词过滤器 
 */
public abstract class KeyWordFilter<T> extends  FilterModel<T> 
{ 
	private static final long serialVersionUID = 1L;
	/**
	 * 输入值,关键词
	 */
	public String value;
	public KeyWordFilter()
	{
		this("KeyWord");
	}
	public KeyWordFilter(String name)
	{
		super(name);
	} 
	/**
	 * 需要搜索的字段,在这些值中搜索
	 */
	public abstract CharSequence[] getSearchTexts(T data);
	public  boolean match(T data)
	{
		if(this.value==null||value.length()==0){
			return true;
		}
		CharSequence[] texts = getSearchTexts(data);
		if(texts==null||texts.length==0){
			return false;
		}
		for(CharSequence text:texts){
			if(text!=null&&text.toString().contains(value)){
				return true;
			}
		}
		return false;
	}

}
