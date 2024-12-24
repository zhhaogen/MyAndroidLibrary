package cn.zhg.logger;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class SimpleLoggerPrinter implements ILoggerPrinter {
	/**
	 * 当前包名
	 */
	private String packageName;
	/**
	 * 显示项目名
	 */
	private boolean showProject = true;
	/**
	 * 显示时间
	 */
	private boolean showTime = false;
	/**
	 * 显示项目名
	 */
	private boolean showLevel = false;
	/**
	 * 显示源码位置
	 */
	private boolean showSource = true;
	/**
	 * 级别
	 */
	private int level = LoggerLevel.DEBUG;
	/**
	 * 过滤的项目名
	 */
	private Set<String> filterProjectNames;
	/**
	 * 过滤的类名+方法名
	 */
	private Set<String> filterMethodNames;
	/**
	 * 过滤的标签
	 */
	private Set<String> filterTags;
	private static final ThreadLocal<SimpleDateFormat> formatter = new ThreadLocal<SimpleDateFormat>() {
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
		}
	};

	public SimpleLoggerPrinter() {
		packageName = this.getClass().getPackage().getName();
		filterProjectNames = new HashSet<>();
		filterMethodNames = new HashSet<>();
		filterTags = new HashSet<>();
		readConfigure();
	}

	public void print(LoggerMessage msg) {
		if (msg == null) {
			return;
		}
		fillLocaltion(msg);
		if (!enable(msg)) {
			return;
		}
		System.out.println(format(msg));
	}

	/**
	 * 是否启用该日志内容
	 */
	protected boolean enable(LoggerMessage msg) {
		if (msg.level < level) {
			return false;
		}
		if (!filterProject(msg)) {
			return false;
		}
		if (!filterMethod(msg)) {
			return false;
		}
		if (!filterTag(msg)) {
			return false;
		}
		return true;
	}

	/**
	 * 格式化内容
	 */
	protected String format(LoggerMessage msg) {
		StringBuilder sb = new StringBuilder();
		if (showProject && msg.projectName != null) {
			sb.append("[").append(msg.projectName).append("] ");
		}
		if (showTime) {
			sb.append("[").append(formatter.get().format(new Date(msg.time))).append("] ");
		}
		if (showLevel) {
			sb.append(levelToStr(level)).append(" ");
		}
		if (msg.tag != null) {
			sb.append(msg.tag).append(" :");
		}
		sb.append(msg.message);
		if (msg.ex != null) {
			sb.append("\n");
			try (StringWriter writer = new StringWriter(); PrintWriter pw = new PrintWriter(writer)) {
				msg.ex.printStackTrace(pw);
				sb.append(writer.getBuffer());
			} catch (Exception igr) {
				sb.append(msg.ex.getMessage());
			}
		}
		if (showSource && msg.className != null) {
			sb.append("\n");
			sb.append("\tat ").append(msg.className).append(".").append(msg.methodName).append("(").append(msg.fileName)
					.append(":").append(msg.lineNo).append(")");
		}
		return sb.toString();
	}

	/**
	 * 填充源码位置信息
	 */
	protected void fillLocaltion(LoggerMessage msg) {
		if (msg.className != null) {
			return;
		}
		Thread th = Thread.currentThread();
		StackTraceElement[] trs = th.getStackTrace();
		StackTraceElement tr = null;
//		StackTraceElement tr=trs[5];  
		for (int i = 1; i < trs.length; i++) {
			tr = trs[i];
			String className = tr.getClassName();
			if (className.startsWith(packageName + ".") || className.startsWith("java.")|| className.startsWith("jdk.")|| className.startsWith("sun.")) {
				continue;
			}
			break;
		}
		if (tr == null) {
			return;
		}
		msg.fileName = tr.getFileName();
		msg.className = tr.getClassName();
		msg.methodName = tr.getMethodName();
		msg.lineNo = tr.getLineNumber();
		try {
			String classPath = "/" + msg.className.replace('.', '/') + ".class";
			URL url = this.getClass().getResource(classPath);
			if (url == null) {
				return;
			}
			msg.filePath = msg.className.replace('.', '/') + ".java";
			String protocol = url.getProtocol();
			if ("file".equals(protocol)) {
				URI uri = url.toURI();
				String classFilePath = uri.getPath();
				String classParentPath = classFilePath.substring(0, classFilePath.length() - classPath.length() + 1);
				if (classParentPath.endsWith("/bin/test/") || classParentPath.endsWith("/bin/main/")) {
					msg.projectName = new File(classParentPath).getParentFile().getParentFile().getName();
				} else if (classParentPath.endsWith("/bin/")) {
					msg.projectName = new File(classParentPath).getParentFile().getName();

				} else {
					msg.projectName = new File(classParentPath).getName();
				}
				return;
			}
			if ("jar".equals(protocol)) {
				String classFilePath = url.getPath();
				String jarFilePath = classFilePath.substring(0, classFilePath.lastIndexOf(".jar!"));
				msg.projectName = jarFilePath.substring(jarFilePath.lastIndexOf('/') + 1);
				return;
			}
		} catch (Throwable igr) {

		}
	}

	/**
	 * 是否匹配项目名称
	 */
	private boolean filterMethod(LoggerMessage msg) {
		if (filterProjectNames.isEmpty()) {
			return true;
		}
		if (msg.projectName == null) {
			return false;
		}
		return filterProjectNames.contains(msg.projectName);
	}

	/**
	 * 是否匹配方法
	 */
	private boolean filterProject(LoggerMessage msg) {
		if (filterMethodNames.isEmpty()) {
			return true;
		}
		if (msg.methodName == null) {
			return false;
		}
		String fullMethodName = msg.className + "#" + msg.methodName;
		for (String filterMethodName : filterMethodNames) {
			if (filterMethodName.equals(fullMethodName) || filterMethodName.equals(msg.className)
					|| filterMethodName.equals(msg.methodName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否匹配标签
	 */
	private boolean filterTag(LoggerMessage msg) {
		if (filterTags.isEmpty()) {
			return true;
		}
		if (msg.tag == null) {
			return false;
		}
		String tags = msg.tag.toString();
		return filterTags.contains(tags);
	}

	/**
	 * 读取配置
	 */
	private void readConfigure() {
		Properties ps = new Properties();
		try (InputStream is = this.getClass().getResourceAsStream("/logger.properties");) {
			if (is == null) {
				return;
			}
			ps.load(is);
		} catch (Exception igr) {
		}
		// 合并Properties
		ps.putAll(System.getProperties());
		showProject = ps.getProperty("logger.showProject", String.valueOf(showProject)).equals("true");
		showTime = ps.getProperty("logger.showTime", String.valueOf(showTime)).equals("true");
		showLevel = ps.getProperty("logger.showLevel", String.valueOf(showLevel)).equals("true");
		showSource = ps.getProperty("logger.showSource", String.valueOf(showSource)).equals("true");
		String v = ps.getProperty("logger.level", "").trim().toUpperCase();
		if (!v.isEmpty()) {
			level = toLevel(v);
		}
		v = ps.getProperty("logger.filterProjectNames", "");
		if (!v.isEmpty()) {
			filterProjectNames.clear();
			String[] arr = v.split(",");
			for (String a : arr) {
				filterProjectNames.add(a);
			}
		}
		v = ps.getProperty("logger.filterMethodNames", "");
		if (!v.isEmpty()) {
			filterMethodNames.clear();
			String[] arr = v.split(",");
			for (String a : arr) {
				filterMethodNames.add(a);
			}
		}
		v = ps.getProperty("logger.filterTags", "");
		if (!v.isEmpty()) {
			filterTags.clear();
			String[] arr = v.split(",");
			for (String a : arr) {
				filterTags.add(a);
			}
		}
	}

	private int toLevel(String v) {
		if ("DEBUG".equals(v) || "D".equals(v)) {
			return LoggerLevel.DEBUG;
		}
		if ("INFO".equals(v) || "I".equals(v)) {
			return LoggerLevel.INFO;
		}
		if ("WARN".equals(v) || "W".equals(v)) {
			return LoggerLevel.WARN;
		}
		if ("ERROR".equals(v) || "E".equals(v)) {
			return LoggerLevel.ERROR;
		}
		return LoggerLevel.DEBUG;
	}

	private String levelToStr(int level) {
		switch (level) {
		case LoggerLevel.DEBUG:
			return "D";
		case LoggerLevel.INFO:
			return "I";
		case LoggerLevel.WARN:
			return "W";
		case LoggerLevel.ERROR:
			return "E";
		}
		return level + "";
	}
}
