package frc.robot;
import frc.CheeseLog.*;

/**
 * Wraps logger, allows for specific access of logging functionality.
 */
public class LogInterface implements Component {
    public Logger logger;

    public LogInterface() {
        logger = new Logger();
    }

   

    public void tick() {
        logger.logToSQL();
    }

}