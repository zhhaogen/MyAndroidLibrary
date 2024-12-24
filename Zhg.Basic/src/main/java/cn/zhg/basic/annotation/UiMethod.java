/**
 * 
 * @author zhhaogen
 * 创建于 2018年4月23日 下午1:13:51
 */
package cn.zhg.basic.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * 标记处理ui的方法,这个方法需要在ui线程中执行
 */
@Retention(RUNTIME)
@Target(METHOD) 
public @interface UiMethod
{
	/**
	 * 唯一识别id
	 **/
	int value();
}
