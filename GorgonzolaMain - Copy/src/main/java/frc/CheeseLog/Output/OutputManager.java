package frc.CheeseLog.Output;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import frc.CheeseLog.SQLType.Type;

/**
 * A go-between between tables and outputs. It is initialized with one
 * OutputMethod, but more can be added. This class is used internally and does
 * not need any user interaction.
 */
public class OutputManager {
	Type[] dataTypes;
	String[] columns;
	String tableName;
	PreparedStatement statement;
	DatabaseConnection connection;
	ArrayList<OutputMethod> outputs = new ArrayList<>();

	/**
	 * Creates an object that manages logging and outputs for a table.
	 * 
	 * @param outputMethod The method to output logs.
	 * @param tableName    The table to interface with.
	 * @param columns      The names of the columns in the table.
	 * @param dataTypes    The data types of the columns in the table.
	 */
	public OutputManager(DatabaseConnection outputMethod, String tableName, String[] columns, Type[] dataTypes) {
		this.tableName = tableName;
		this.columns = columns;
		this.dataTypes = dataTypes;
		// this.outputs.add(outputMethod);
		connection = outputMethod;
		outputMethod.init(tableName, columns, Stream.of(dataTypes).map(Type::toString).toArray(String[]::new));
		String query = "INSERT INTO " + tableName + " (" + Stream.of(columns).reduce((s1, s2) -> s1 + ", " + s2).get()
				+ ") VALUES (";
		for (int i = 0; i < columns.length; i++) {
			query += "?, ";
		}
		query = query.substring(0, query.length() - 2);
		query += ");";
		try {
			statement = connection.connection.prepareStatement(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		new Thread(() -> {
			while (!Thread.interrupted()) {
				try {
					if (batches > 0) {
						statement.executeBatch();
						batches = 0;
					} 
					Thread.sleep(1000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

	}

	/**
	 * Tries to prevent some of the simpler and/or accidental SQL injections...
	 * definitely beatable, don't SQL inject!
	 * 
	 * @param s The string to sanitize
	 * @return The sanitized string
	 */
	public String sanitize(String s) {

		return s.replaceAll("\\\\", "\\\\").replaceAll("\"", "\\\"").replaceAll("--", "\\--");

	}

	/**
	 * Logs the values in s. Assumes s[0] goes in columns[0], s[1] goes in
	 * columns[1], etc
	 * 
	 * @param s The values to log
	 */
	int batches = 0;

	public void log(String[] s) {
		outputs.forEach(o -> o.update(tableName, s, columns));
		try {
			for (int i = 1; i <= dataTypes.length; i++) {
				switch (dataTypes[i - 1].toString()) {
				case "bool":
					statement.setBoolean(i, Boolean.parseBoolean(s[i - 1]));
					break;
				case "int":
					statement.setInt(i, Integer.parseInt(s[i - 1]));
					break;
				case "DOUBLE PRECISION":
					statement.setDouble(i, Double.parseDouble(s[i - 1]));
					break;
				case "timestamp":
					statement.setTimestamp(i, Timestamp.valueOf(s[i - 1]));
					break;
				default:
					statement.setString(i, s[i - 1]);
				}
			}
			statement.addBatch();
			batches++;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns the datatype of a specified column
	 * 
	 * @param colNumber The index of the column
	 * @return The datatype of the column
	 */
	public Type getDataType(int colNumber) {
		return dataTypes[colNumber];
	}

	/**
	 * Returns the name of a specified column
	 * 
	 * @param colNumber The index of the column
	 * @return The name of the column
	 */
	public String getColumnName(int colNumber) {
		return columns[colNumber];
	}

	/**
	 * Gets the index of a specified column
	 * 
	 * @param column The name of the column
	 * @return The column's index. -1 if the name is not a column's name
	 */
	public int columnIndex(String column) {
		return Arrays.asList(columns).indexOf(column);
	}

	/**
	 * Returns the number of columns
	 * 
	 * @return the number of columns
	 */
	public int columnCount() {
		return dataTypes.length;
	}

	/**
	 * Adds and initializes a new output method
	 * 
	 * @param outputMethod The output method to add and initialize
	 */
	public void addOutputMethod(OutputMethod outputMethod) {
		outputMethod.init(tableName, columns, Stream.of(dataTypes).map(Type::toString).toArray(String[]::new));
		outputs.add(outputMethod);
	}
}
