/**
 * 
 * @author zhhaogen
 * 创建于 2018年9月17日 下午7:39:48
 */
package cn.zhg.basic.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 这个数据字段被延迟加载
 */
@Retention(RUNTIME)
@Target(FIELD) 
public @interface LaterData
{

}
