package frc.CheeseLog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Grant Matteo A multi-functional Logger designed to output to the
 *         Console, a File, a SQL database, or all 3.
 */
public class Logger {

	private HashMap<String, HashMap<String, Loggable>> sqlLoggables; // table -> {name-> Log}

	private HashMap<String, BufferedFileWriter> fileWriters;

	private boolean sqlInitialized = false, fileIntialized = false;
	private long sqlCounter = 0, consoleCounter = 0, fileCounter = 0;
	private Connection connection; // holds the connection to the SQL server
	private String username, password, url; // holds SQL server data

	private String folderPath; // File stored by default in this folder/tablename

	/**
	 * Initializes a Logger object.
	 */
	public Logger() {
		sqlLoggables = new HashMap<String, HashMap<String, Loggable>>();
		fileWriters = new HashMap<String, BufferedFileWriter>();
	}

	private void addTable(String name) throws Exception {
		sqlLoggables.put(name, new HashMap<String, Loggable>());
		Statement init = connection.createStatement();
		init.executeUpdate("DROP TABLE IF EXISTS " + name + ";");
		init.executeUpdate("CREATE TABLE " + name + "(iteration int NOT NULL, PRIMARY KEY (iteration));");

	}

	public void updateStatement(String table, String name, boolean logToSQL, boolean logToFile, boolean logToConsole)
			throws Exception {
		for (char c : name.toCharArray()) {
			if (c < 48 || c > 122 || (c > 90 && c < 97) || (c > 58 && c < 65)) { // if it isn't a letter or number
				System.err.println(
						"ERROR IN addStatement(); function name is not valid. Please use only letters and numbers");
				throw (new Exception());
			}
		}
		for (char c : table.toCharArray()) {
			if (c < 48 || c > 122 || (c > 90 && c < 97) || (c > 58 && c < 65)) { // if it isn't a letter or number
				System.err.println(
						"ERROR IN addStatement(); function name is not valid. Please use only letters and numbers");
				throw (new Exception());
			}
		}
		if (sqlLoggables.containsKey(table) && sqlLoggables.get(table).containsKey(name)) {
			Loggable oldLog = sqlLoggables.get(table).get(name);
			Loggable newLog = new Loggable();
			newLog.initLogging(oldLog.log, oldLog.type, logToSQL, logToFile, logToConsole, oldLog.condition);
			if (!oldLog.logToSQL && logToSQL) {
				if (sqlInitialized) {

					String command = "ALTER TABLE " + table + " ADD " + name + " " + newLog.sqlType + ";";
					try {
						Statement add = connection.createStatement();
						add.executeUpdate(command);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					System.err.println("Error in updateStatement(); initSQL() method either wasn't called or failed");
				}
			}
			if (logToFile) {
				if (fileIntialized) {
					if (!fileWriters.containsKey(table)) {
						fileWriters.put(table, new BufferedFileWriter(table));
					}

				} else {
					System.err.println("Error in updateStatement(); initFile() method either wasn't called or failed");
					throw new Exception();
				}
			}
			fileWriters.get(table).add(name, newLog);

			sqlLoggables.get(table).put(name, newLog);
		} else {
			System.err.println("Table and name combination not found:\r\nFound Table: "
					+ sqlLoggables.containsKey(table) + "\r\nFound Name In Table: "
					+ (sqlLoggables.containsKey(table) && sqlLoggables.get(table).containsKey(name)));
			throw new Exception();
		}
	}

	/**
	 * Adds an anonymous function to a list of functions that will be called when
	 * logging. <i>Example: addStatement("Time", ()->{return
	 * ""+System.nanoTime();}); </i>
	 * 
	 * @param name     The label that will be associated with this function's return
	 *                 value
	 * @param function A function with <i>NO PARAMETERS</i> that <i>RETURNS A
	 *                 STRING</i> to be logged by logTo*() methods.
	 */
	public void addStatement(String table, String name, Loggable log) throws Exception {
		for (char c : name.toCharArray()) {
			if (c < 48 || c > 122 || (c > 90 && c < 97) || (c > 58 && c < 65)) { // if it isn't a letter or number
				System.err.println(
						"ERROR IN addStatement(); function name is not valid. Please use only letters and numbers");
				throw (new Exception());
			}
		}
		for (char c : table.toCharArray()) {
			if (c < 48 || c > 122 || (c > 90 && c < 97) || (c > 58 && c < 65)) { // if it isn't a letter or number
				System.err.println(
						"ERROR IN addStatement(); function name is not valid. Please use only letters and numbers");
				throw (new Exception());
			}
		}
		if (!sqlLoggables.keySet().contains(table)) {
			this.addTable(table);
		} else if (sqlLoggables.get(table).containsKey(name)) {
			System.err.println("Error! Used addStatement() on a pre-existing value! Use updateStatement() instead!");
			throw new Exception();
		}
		sqlLoggables.get(table).put(name, log);
		if (log.logToSQL) {
			if (sqlInitialized) {
				String command = "ALTER TABLE " + table + " ADD " + name + " " + log.sqlType + ";";
				try {
					Statement add = connection.createStatement();
					add.executeUpdate(command);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.err.println("Error in addStatement(); initSQL() method either wasn't called or failed");
				throw new Exception();
			}
		}
		if (log.logToFile) {
			if (fileIntialized) {
				if (!fileWriters.containsKey(table)) {
					fileWriters.put(table, new BufferedFileWriter(table));
				}
				fileWriters.get(table).add(name, log);
			} else {
				System.err.println("Error in addStatement(); initFile() method either wasn't called or failed");
				throw new Exception();
			}
		}
	}

	public void addStatement(String table, String name, Type type, LogFunction log, boolean logToSQL, boolean logToFile,
			boolean logToConsole, LogCondition condition) throws Exception {
		Loggable assembledLoggable = new Loggable();
		assembledLoggable.initLogging(log, type, logToSQL, logToFile, logToConsole, condition);
		addStatement(table, name, assembledLoggable);

	}

	public void addStatement(String table, String name, Type type, LogFunction log, boolean logToSQL, boolean logToFile,
			boolean logToConsole) throws Exception {
		Loggable assembledLoggable = new Loggable();
		assembledLoggable.initLogging(log, type, logToSQL, logToFile, logToConsole, () -> true);
		addStatement(table, name, assembledLoggable);
	}

	/**
	 * Prepares the Logger for file output. <b>THIS FUNCTION MUST BE CALLED BEFORE
	 * CALLING logToFile()!!!!!</b>
	 * 
	 * @param filepath  The path to the file to which you would like to output.
	 * @param delimeter The String that will be used to separate values (EX: a comma
	 *                  in a CSV)
	 */
	public void initFile(String folderPath, String delimeter) {
		BufferedFileWriter.delimiter = delimeter;
		folderPath = folderPath.replace('\\', '/');
		if (folderPath.length() > 0 && folderPath.charAt(folderPath.length() - 1) != '/') {
			folderPath += "/"; // only add a slash on the end if the path doesn't contain one on the end
		}
		BufferedFileWriter.folderPath = folderPath;
		fileIntialized = true;
	}

	/**
	 * Prepares the Logger for SQL output. <b>THIS FUNCTION MUST BE CALLED BEFORE
	 * CALLING logToSQL()!!!!!</b>
	 * 
	 * @param url      the url of your SQL server
	 *                 (EX:jdbc:postgresql://localhost/postgres) See
	 *                 <i>https://dzone.com/articles/connecting-sql-server-java</i>
	 *                 and the associated articles for more information
	 * @param username the username you wish to use for your SQL server. This should
	 *                 be the same as the one used in postgresql setup
	 * @param password the password you wish to use for your SQL server. This should
	 *                 be the same as the one used in postgresql setup
	 */
	public void initSQL(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
		try {
			connection = DriverManager.getConnection(url, username, password);
			System.out.print("Successfully connected to Server at " + url + "\r\n");
			sqlInitialized = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Puts the return values of functions passed into addStatement() into a SQL
	 * database <b>DO NOT USE UNTIL initSQL() HAS RUN!!!! WARNING: Using
	 * addStatement() after calling this method can cause unintended behavior</b>
	 */
	public void logToSQL() {
		if (sqlInitialized) { // table -> {name-> Loggable}
			for (String table : sqlLoggables.keySet()) {
				String sqlCommandStart = "INSERT INTO " + table + " (iteration";
				String sqlAddCommand = " VALUES (?";
				for (String name : sqlLoggables.get(table).keySet()) {
					if (sqlLoggables.get(table).get(name).sqlCheck()) {
						sqlCommandStart += ", " + name;
						sqlAddCommand += ", ?";
					}
				}
				sqlCommandStart += ")";
				sqlAddCommand = sqlCommandStart + sqlAddCommand + ");";
				try {
					PreparedStatement add = connection.prepareStatement(sqlAddCommand);
					add.setLong(1, sqlCounter);
					int j = 2;
					for (String name : sqlLoggables.get(table).keySet()) {
						if (sqlLoggables.get(table).get(name).sqlCheck()) {
							Loggable current = sqlLoggables.get(table).get(name);
							Object currentOut = current.getLogValue();
							if (current.sqlType.validate(currentOut)) {
								switch (current.type) {
								case BOOL:
									add.setBoolean(j, current.sqlType.reformat(currentOut).equals("true"));
									break;
								case DECIMAL:
									add.setDouble(j, Double.parseDouble(current.sqlType.reformat(currentOut)));
									break;
								case INT:
									add.setInt(j, Integer.parseInt(current.sqlType.reformat(currentOut)));
									break;
								case TEXT:
									add.setString(j, current.sqlType.reformat(currentOut));
									break;
								default:
									break;
								}
								j++;
							}
						}
					}

					add.executeUpdate();
				} catch (SQLException e) {
					System.err.println("Error in logToSQL(); initSQL() method either wasn't called or failed");
					e.printStackTrace();
				}

			}
		} else {
			System.err.println("Error in logToSQL(); initSQL() method either wasn't called or failed");
		}
		sqlCounter++;

	}

	/**
	 * Puts the return values of functions passed into addStatement() into StdOut
	 */
	public void logToConsole() {
		System.out.println("---------------------------------------\r\n" + "\t\tITERATION " + consoleCounter + ":\r\n");
		for (String table : sqlLoggables.keySet()) {
			System.out.println("------------- " + table);
			for (String name : sqlLoggables.get(table).keySet()) {
				if (sqlLoggables.get(table).get(name).consoleCheck())
					System.out.println(name + "\t" + sqlLoggables.get(table).get(name).getLogValue());
			}
			System.out.println("------------- END\r\n");

		}
		consoleCounter++;
		System.out.print("---------------------------------------\r\n\r\n");
	}

	/**
	 * Puts the return values of functions passed into addStatement() into a File
	 * <b>DO NOT USE UNTIL initFile() HAS RUN!!!! WARNING: Using addStatement()
	 * after calling this method can cause unintended behavior</b>
	 */
	public void logToFile() {
		for (String table : fileWriters.keySet()) {
			try {
				fileWriters.get(table).log(fileCounter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		fileCounter++;

	}

	/**
	 * Closes all associated objects and ends logging.
	 */
	public void close() {
		if (fileIntialized) {
			for (String key : fileWriters.keySet()) {
				try {
					fileWriters.get(key).close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		try {
			if (sqlInitialized)
				connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
