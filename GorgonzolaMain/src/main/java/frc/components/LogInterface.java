package frc.components;

import java.util.Arrays;

import frc.CheeseLog.Loggable;
import frc.CheeseLog.Logger;
import frc.CheeseLog.LoggingCriteria;
import frc.CheeseLog.LoggingMode;
import frc.CheeseLog.Table;
import frc.CheeseLog.Output.DatabaseConnection;
import frc.CheeseLog.SQLType.Type;

/**
 * Wraps Logger from Cheese Log, allows for SQL logging of relevant data during robot operation
 * 
 * @author Jeff
 */
public class LogInterface implements Component {
    //See Cheese Log package for more documentation:
    public Logger logger;
    private static DatabaseConnection sqlConnection;

    public Table turnController, drivetrain, inputManager, gyro, shoulder, wrist, shooter; //All SQL Tables that we might log to

    /**
    * Initializes a LogInterface
    */
    public LogInterface() {
        try {
            sqlConnection = new DatabaseConnection("10.10.86.135", "postgres", "postgres", "Hypercam");
            logger = new Logger();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds all initialized tables to Logger.
     */
    public void init() {
        //Used to add whatever tables are useful at any given time. If automatic tables aren't here,
        //they are not logged.
        for (Table t : Arrays.asList(inputManager, wrist, shoulder, drivetrain, gyro, turnController)) {
            try {
                logger.addTable(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Creates and returns a table from the specified information
     * @param tableName The name of the SQL table. Must only contain letters, numbers, and underscores.
     * @param columnNames The names of the columns in the SQL table. Must only contain letters, numbers, and underscores
     * @param dataTypes The datatypes of the columns in the SQL table. Available types:
     *                        new Decimal()                    -- Floating point numbers
     *                        new Int()                        -- Integers
     *                        new VarChar(length [defalt 255]) -- Strings
     *                        new Bool()                       -- True/False
     *                        new DateTime()                   -- Timestamp          
     * @param loggables A list of lambdas. The nth loggable should return a value that matches the nth dataType. 
     * @return the resulting table.
     */
    public static Table createTable(String tableName, String[] columnNames, Type[] dataTypes, Loggable[] loggables) {
        try {
            Table t = new Table(sqlConnection, tableName, LoggingMode.AUTOMATIC, columnNames, dataTypes);
            t.setLoggers(loggables);
            return t;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates and returns a conditional table from the specified information. The table will only log
     * if the condition you specify evaluates to true.
     * @param tableName The name of the SQL table. Must only contain letters, numbers, and underscores.
     * @param condition A lambda that returns a boolean, true if the table should log, false otherwise.
     * @param columnNames The names of the columns in the SQL table. Must only contain letters, numbers, and underscores
     * @param dataTypes The datatypes of the columns in the SQL table. Available types:
     *                        new Decimal()                    -- Floating point numbers
     *                        new Int()                        -- Integers
     *                        new VarChar(length [defalt 255]) -- Strings
     *                        new Bool()                       -- True/False
     *                        new DateTime()                   -- Timestamp          
     * @param loggables A list of lambdas. The nth loggable should return a value that matches the nth dataType. 
     * @return the resulting table.
     */

    public static Table conditionalTable(String tableName, LoggingCriteria condition, String[] columnNames,
            Type[] dataTypes, Loggable[] loggables) {
        try {
            Table t = new Table(sqlConnection, tableName, LoggingMode.CRITERIA, columnNames, dataTypes);
            t.addLoggingCriteria(condition);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates and returns a manually-logged table from the specified information. The table will not 
     * log automatically, and table.log() MUST be called on it in order for it to save its data.
     * @param tableName The Name of the SQL table. Must only contain letters, numbers, and underscores.
     * @param columnNames The Names of the columns in the SQL table. Must only contain letters, numbers, and underscores
     * @param dataTypes The datatypes of the columns in the SQL table. Available types:
     *                        new Decimal()                    -- Floating point numbers
     *                        new Int()                        -- Integers
     *                        new VarChar(length [defalt 255]) -- Strings
     *                        new Bool()                       -- True/False
     *                        new DateTime()                   -- Timestamp          
     * @param loggables A list of lambdas. The nth loggable should return a value that matches the nth dataType. 
     * @return the resulting table.
     */

    public static Table manualTable(String tableName, String[] columnNames, Type[] dataTypes, Loggable[] loggables) {
        try {
            Table t = new Table(sqlConnection, tableName, LoggingMode.AUTOMATIC, columnNames, dataTypes);
            t.setLoggers(loggables);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the number of ticks the logger has gone through.
     * @return an int, 0 for the first tick, 1 for the second, and so on
     */
    public int getTick() {
        return logger.getTick();
    }

    /**
     * Ticks the logger (logs values)
     */
    public void tick() {
        try {
            logger.tick();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}