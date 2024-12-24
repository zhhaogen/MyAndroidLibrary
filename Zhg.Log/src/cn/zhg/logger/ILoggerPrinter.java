package cn.zhg.logger;
/**
 * 日志打印器
 */
public interface ILoggerPrinter {
	/**
	 * 打印日志
	 * @param msg 日志消息内容,可能为null
	 */
	void print(LoggerMessage msg);
}
