package utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Class for creating logs.
 * <p>
 * For logging is used static method createLog().
 * If another logger is needed, every class, which extends
 * Logger, can replace default logger with method setLogger().
 * After that, logs will be written with the last logger set.
 * Severity level says which logs will be written.
 *
 * @author Jakub Tutko
 */
public class Logger {
	private static Logger staticLogger = new Logger();

	public static final int DEBUG_LOG   = 1;
	public static final int INFO_LOG    = 2;
	public static final int WARNING_LOG = 3;
	public static final int ERROR_LOG   = 4;

	private int severityLevel;

	/**
	 * Static method for creating logs. Method uses logging method
	 * of the last logger set with static setLogger method.
	 *
	 * @param severity severity level of the log
	 * @param message message, which will be logged
	 */
	public static void createLog(int severity, String message) {
		staticLogger.logMessage(severity, message);
	}

	/**
	 * Method changes logger which will be used to log messages.
	 *
	 * @param logger instance of the class which implements Logger class
	 */
	public static void setLogger(Logger logger) {
		staticLogger = logger;
	}

	/**
	 * Basic constructor, which set severity level to default value (DEBUG).
	 */
	public Logger() {
		severityLevel = 1;
	}

	/**
	 * Method prints log message into console. If severity level of the log
	 * is lower than level set in instance variable severityLevel, log will
	 * be not printed.
	 *
	 * @param severity severity level of the message
	 * @param message message to be logged
	 */
	public void logMessage(int severity, String message) {
		if(severity >= this.severityLevel) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.UK);
			LocalTime time = LocalTime.now();
			String f = formatter.format(time);
			System.out.println(f + " " + castSeverityLevelToInt(severity) + ": " + message);
		}
	}

	/**
	 * Setter for instance variable severityLevel. Severity level variable says, which logs
	 * will be logged and which not. Logs with the lower level as the level set in severity
	 * level instance variable will be not logged.
	 *
	 * @param severity severity level
	 */
	public void setSeverityLevel(int severity) {
		this.severityLevel = severity;
	}

	/**
	 * Method cast int value of the severity level into String value.
	 * If method gets value, which does not match with any of the severity
	 * levels, method returns string "UNKNOWN".
	 *
	 * @param severity int value of the severity level
	 * @return string value of the severity level
	 */
	private String castSeverityLevelToInt(int severity) {
		switch(severity) {
			case 1:
				return "DEBUG";
			case 2:
				return "INFO";
			case 3:
				return "WARNING";
			case 4:
				return "ERROR";
			default:
				return "UNKNOWN";
		}
	}
}
