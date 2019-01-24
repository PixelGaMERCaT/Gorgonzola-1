package frc.CheeseLog;

import frc.CheeseLog.SQLType.Bool;
import frc.CheeseLog.SQLType.DateTime;
import frc.CheeseLog.SQLType.Decimal;
import frc.CheeseLog.SQLType.Int;
import frc.CheeseLog.SQLType.SQLType;
import frc.CheeseLog.SQLType.Text;

public class Loggable implements LogFunction {
	public LogFunction log;
	public boolean logToSQL, logToFile, logToConsole;
	public SQLType sqlType; // used for internal methods
	public Type type; // used for replication and switching -- for control
	public LogCondition condition;

	/**
	 * Init Method for Loggable
	 * 
	 * @param log          A Logging Function (Lambda) that returns something
	 *                     matching type
	 * @param type         The type that the logging function returns (EX:
	 *                     Type.BOOL)
	 * @param logToSQL     Whether or not this should be logged to SQL
	 * @param logToFile    Whether or not this should be logged to a file
	 * @param logToConsole Whether or not this should be logged to the console
	 * @param condition    A LogCondition (Lambda) that returns whether this value
	 *                     should be logged at any time
	 */
	public void initLogging(LogFunction log, Type type, boolean logToSQL, boolean logToFile, boolean logToConsole,
			LogCondition condition) {
		this.log = log;
		switch (type) {
		case BOOL:
			this.sqlType = new Bool();
		case DATETIME:
			this.sqlType = new DateTime();
		case DECIMAL:
			this.sqlType = new Decimal();
		case INT:
			this.sqlType = new Int();
		case TEXT:
			this.sqlType = new Text();
		}
		this.logToSQL = logToSQL;
		this.logToFile = logToFile;
		this.logToConsole = logToConsole;
		this.condition=condition;
		this.type=type;
	}

	/**
	 * Implicit init method for Loggable, sets logging condition to always return
	 * true
	 * 
	 * @param log  A Logging Function (Lambda) that returns something matching type
	 * @param type The type that the logging function returns; based on SQL types.
	 */
	public void initLogging(LogFunction log, Type type, boolean logToSQL, boolean logToFile, boolean logToConsole) {
		initLogging(log, type, logToSQL, logToFile, logToConsole, () -> true);
	}
	/**
	 * Returns whether this loggable should log to the console
	 * @return true if the loggable should be logged to the console, false otherwise
	 */
	public boolean consoleCheck() {
		return logToConsole && condition.getCondition();
	}
	/**
	 * Returns whether this loggable should log to SQL
	 * @return true if the loggable should be logged to SQL, false otherwise
	 */
	public boolean sqlCheck() {
		return logToSQL && condition.getCondition();
	}
	/**
	 * Returns whether this loggable should log to a file
	 * @return true if the loggable should be logged to a file, false otherwise
	 */
	public boolean fileCheck() {
		return logToFile && condition.getCondition();
	}
	@Override
	public Object getLogValue() {
		return log.getLogValue();
	}
}
