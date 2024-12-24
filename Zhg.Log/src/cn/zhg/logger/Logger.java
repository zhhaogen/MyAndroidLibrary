package cn.zhg.logger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * 日志工具
 */
public class Logger {
	/**
	 * 全局是否开启,关闭之后整个都不可用
	 */
	public static boolean enable = true;
	/**
	 * 打印日志实现
	 */
	private static ILoggerPrinter printer;
	/**
	 * 起始耗时
	 */
	private static long t;
	static {
		init();
	}

	/**
	 * 添加日志
	 * 
	 * @param msg 日志消息内容
	 */
	public static void log(LoggerMessage msg) {
		if (!enable) {
			return;
		}
		printer.print(msg);
	}

	/**
	 * 调试级别,默认打印当前源码位置,用于定位当前调用位置
	 */
	public static void d() {
		log(new LoggerMessage().d().msg(""));
	}

	/**
	 * 调试级别
	 * 
	 * @param msg 消息内容
	 */
	public static void d(Object msg) {
		log(new LoggerMessage().d().msg(msg));
	}

	/**
	 * 调试级别
	 * 
	 * @param tag 标签
	 * @param msg 消息内容
	 */
	public static void d(Object tag, Object msg) {
		log(new LoggerMessage().d().tag(tag).msg(msg));
	}

	/**
	 * 信息级别,推荐使用{@link Logger#d()}
	 */
	public static void i() {
		log(new LoggerMessage().i().msg(""));
	}

	/**
	 * 信息级别
	 * 
	 * @param msg 消息内容
	 */
	public static void i(Object msg) {
		log(new LoggerMessage().i().msg(msg));
	}

	/**
	 * 信息级别
	 * 
	 * @param tag 标签
	 * @param msg 消息内容
	 */
	public static void i(Object tag, Object msg) {
		log(new LoggerMessage().i().tag(tag).msg(msg));
	}

	/**
	 * 警告级别
	 * 
	 * @param msg 消息内容
	 */
	public static void w(Object msg) {
		log(new LoggerMessage().w().msg(msg));
	}

	/**
	 * 警告级别
	 * 
	 * @param msg 消息内容
	 * @param ex  异常
	 */
	public static void w(Object msg, Throwable ex) {
		log(new LoggerMessage().w().msg(msg).ex(ex));
	}

	/**
	 * 异常级别
	 * 
	 * @param msg 消息内容
	 */
	public static void e(Object msg) {
		log(new LoggerMessage().e().msg(msg));
	}

	/**
	 * 异常级别
	 * 
	 * @param msg 消息内容
	 * @param ex  异常
	 */
	public static void e(Object msg, Throwable ex) {
		log(new LoggerMessage().e().msg(msg).ex(ex));
	}

	/**
	 * 列出对象的所有getter的内容
	 * 
	 * @param obj 指定对象
	 */
	public static void dGet(Object obj) {
		if (obj == null) {
			log(new LoggerMessage().d().msg("<null>"));
			return;
		}
		Method[] methods = obj.getClass().getMethods();
		if (methods == null || methods.length == 0) {
			log(new LoggerMessage().d().msg("<empty>"));
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (Method m : methods) {
			String name = m.getName();
			int mod = m.getModifiers();
			Class<?>[] ps = m.getParameterTypes();
			if (name.equals("getClass") || Modifier.isStatic(mod) || ps.length > 0) {
				continue;
			}
			try {
				if (name.startsWith("get")) {
					String getName = name.substring(3);
					sb.append("\n").append(getName).append(" :").append(m.invoke(obj));
					continue;
				}
				if (name.startsWith("is")) {
					String getName = name.substring(2);
					sb.append("\n").append(getName).append(" :").append(m.invoke(obj));
					continue;
				}
			} catch (Throwable igr) {
			}
		}
		if (sb.length() == 0) {
			log(new LoggerMessage().d().msg("<empty>"));
			return;
		} 
		log(new LoggerMessage().d().msg(sb.toString()));
	}

	/**
	 * 打印内存信息
	 */
	public static void menory() {
		Runtime rt = Runtime.getRuntime();
		log(new LoggerMessage().d().put("totalMemory", rt.totalMemory()).put("freeMemory", rt.freeMemory())
				.put("maxMemory", rt.maxMemory()));
	}

	/**
	 * 耗时计时开始
	 */
	public static long st() {
		t = System.nanoTime();
		return t;
	}

	/**
	 * 耗时计时结束
	 */
	public static void t() {
		t("");
	}

	/**
	 * 耗时计时结束
	 * 
	 * @param msg 消息
	 */
	public static void t(String msg) {
		log(new LoggerMessage().d().put("st", t).put("et", System.nanoTime()).msg(msg));
	}

	/**
	 * 初始化
	 */
	private static void init() {
		ILoggerPrinter _printer = getLoadLoggerPrinter();
		if (_printer == null) {
			printer = new SimpleLoggerPrinter();
		}
	}

	private static ILoggerPrinter getLoadLoggerPrinter() {
		try {
			ServiceLoader<ILoggerPrinter> loaders = ServiceLoader.load(ILoggerPrinter.class);
			if (loaders == null) {
				return null;
			}
			Iterator<ILoggerPrinter> itor = loaders.iterator();
			if (itor == null || !itor.hasNext()) {
				return null;
			}
			return itor.next();
		} catch (Throwable igr) {

		}
		return null;
	}
}
