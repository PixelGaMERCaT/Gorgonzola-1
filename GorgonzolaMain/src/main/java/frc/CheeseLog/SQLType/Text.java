package frc.CheeseLog.SQLType;

/**
 * Represents a string of up to a maximum length. By default this is 256 characters, but this can be changed
 */
public class Text implements SQLType {

    int length;
    public Text(){
        length = 256;
    }
    public Text(int length){
        this.length = length;
    }

    /**
     * Determines if an object can be a varchar of given length
     * @param s The object to check
     * @return `true` if the s.toString has fewer or equal characters than the maximum
     */
    @Override public boolean validate(Object s){
        return s.toString().length() <= length;
    }

    @Override public String toString(){
        return "text";
    }
	
}
