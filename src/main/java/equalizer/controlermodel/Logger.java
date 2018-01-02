package equalizer.controlermodel;

import org.apache.commons.logging.LogFactory;

public class Logger {
	
	public Logger() {}

	public void log(Class<?> className, String functionName) {
		String text = "[" + functionName + "]";
		switch (Constants.loglevel) {
			case INFO:
				LogFactory.getLog(className).info(text);
				break;
			case DEBUG:
				LogFactory.getLog(className).debug(text);
				break;
			case ERROR:
				LogFactory.getLog(className).error(text);
				break;
			default:
				break;
		}
	}
	
	public void log(Class<?> className, String functionName, String extra) {
		String text = "[" + functionName + "]: " + extra;
		switch (Constants.loglevel) {
			case INFO:
				LogFactory.getLog(className).info(text);
				break;
			case DEBUG:
				LogFactory.getLog(className).debug(text);
				break;
			case ERROR:
				LogFactory.getLog(className).error(text);
				break;
			default:
				break;
		}
	}
	
}
