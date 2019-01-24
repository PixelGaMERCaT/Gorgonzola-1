package frc.CheeseLog.SQLType;

/**
 * This is probably not going to be used by an end user, so validation was left... open
 * Matches datetime objects. This is intended to be used for the timestamp column
 */
public class DateTime implements SQLType {
    @Override public boolean validate(Object o){
        /**
         * TODO: determine if o can be a timestamp
         */
        return true;
    }
    @Override
	
    public String toString() {
    	return "timestamp";
    }
}
