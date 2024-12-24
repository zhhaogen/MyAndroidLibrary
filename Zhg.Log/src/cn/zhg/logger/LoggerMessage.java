package cn.zhg.logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志消息内容
 */
public class LoggerMessage { 
	/**
	 * 创建时间
	 */
	public long time;
	/**
	 * 级别
	 */
	public int level=LoggerLevel.DEBUG;
	/**
	 * 文件路径
	 */
	public String filePath;
	/**
	 * 文件名称
	 */
	public String fileName;
	/**
	 * 项目名称
	 */
	public String projectName;
	/**
	 * 类名
	 */
	public String className;
	/**
	 * 方法名
	 */
	public String methodName;
	/**
	 * 行号
	 */
	public int lineNo=-1;
	/**
	 * 标签
	 */
	public Object tag;
	/**
	 * 日志消息内容
	 */
	public Object message;
	/**
	 * 异常内容
	 */
	public Throwable ex;
	/**
	 * 额外内容
	 */
	public Map<String,Object> extras; 
	public LoggerMessage() {
		time=System.currentTimeMillis();
	}
	public LoggerMessage put(String key,Object value) {
		if(extras==null) {
			extras=new HashMap<>();
		}
		this.extras.put(key, value);
		return this;
	} 
	public LoggerMessage d() {
		this.level = LoggerLevel.DEBUG;
		return this;
	} 
	public LoggerMessage i() {
		this.level = LoggerLevel.INFO;
		return this;
	} 
	public LoggerMessage w() {
		this.level = LoggerLevel.WARN;
		return this;
	} 
	public LoggerMessage e() {
		this.level = LoggerLevel.ERROR;
		return this;
	} 
	public LoggerMessage msg(Object message) {
		this.message = message;
		return this;
	}
	public LoggerMessage tag(Object tag) {
		this.tag = tag;
		return this;
	}
	public LoggerMessage ex(Throwable ex) {
		this.ex = ex;
		return this;
	}
}
