package frc.CheeseLog.SQLType;

/**
 * Represents a boolean value. Can accept boolean objects, strings matching true or false, or 0 or 1
 */
public class Bool implements SQLType {
    /**
     * Determines if s is a valid boolean. Returns valid if s is a java boolean, "true", "false", 0, or 1
     * @param s The object to check
     * @return Whether or not it is a valid Bool
     */
    @Override public boolean validate(Object s){
        if(s instanceof Boolean)
            return true;
        try {
            int i = Integer.parseInt(s.toString());
            if(i == 0 || i == 1)
                return true;
        } catch (Exception e){
            return s.toString().toLowerCase().contains("true") || s.toString().toLowerCase().contains("false");
        }
        return false;
    }

    /**
     * Formats an object to a boolean
     * @param o the object to format
     * @return "true" if o is true, "true", or 1, and returns false otherwise
     */
    @Override public String reformat(Object o){
        try {
            int i = Integer.parseInt(o.toString());
            if(i == 0 || i == 1)
                return Boolean.toString(i == 1);
        } catch (Exception e){
            return Boolean.toString(o.toString().toLowerCase().contains("true"));
        }
        return "false";
    }

    @Override public String toString(){
        return "boolean";
    }

}
