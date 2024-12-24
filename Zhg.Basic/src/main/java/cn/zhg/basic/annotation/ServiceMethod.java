/**
 * 
 * @author zhhaogen
 * 创建于 2018年4月23日 下午1:13:08
 */
package cn.zhg.basic.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
/**
 * 非ui方法，耗时方法，一般放在后台线程中
 */
@Retention(RUNTIME)
@Target(METHOD) 
public @interface ServiceMethod
{
	/**
	 * 唯一识别id
	 **/
	 int value();
}
