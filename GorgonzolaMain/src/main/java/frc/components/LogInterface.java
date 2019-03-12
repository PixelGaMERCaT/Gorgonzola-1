package frc.components;

import java.util.Arrays;

import frc.CheeseLog.*;

import frc.CheeseLog.Output.*;
import frc.CheeseLog.SQLType.*;

/**
 * Wraps logger, allows for specific access of logging functionality.
 */
public class LogInterface implements Component {
    public Logger logger;
    private static DatabaseConnection sqlConnection;
    public Table magicDrive, turnController, drivetrain, inputManager, motionProfiling, gyro, networkTables, shoulder, wrist;

    /**
    * Initializes a logInterface
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
    public void init(){
            for (Table t : Arrays.asList(inputManager, wrist, shoulder, drivetrain, motionProfiling)){
                try {
                    System.out.println(t);
                    System.out.println(t.getName());
                    logger.addTable(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
		
    }
    public static Table table(String tableName, String[] columnNames, Type[] dataTypes, Loggable[] loggables) {
        try {
            Table t = new Table(sqlConnection, tableName, LoggingMode.AUTOMATIC, columnNames, dataTypes);
            t.setLoggers(loggables);
            return t;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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