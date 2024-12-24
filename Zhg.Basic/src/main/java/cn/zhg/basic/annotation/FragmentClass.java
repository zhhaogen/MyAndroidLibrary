/**
 * 
 * @author zhhaogen
 * 创建于 2018年4月30日 上午12:50:48
 */
package cn.zhg.basic.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 标记在Fragment类上,标识Fragment的一些属性
 * @deprecated 使用R.id作为标识，但R已经不被final修饰了,此类将被删除
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface FragmentClass
{
	/**
	 * 唯一识别id
	 **/
	 int value();

	/**
	 * 默认标题
	 */
	 int titleRes() default 0;
}
