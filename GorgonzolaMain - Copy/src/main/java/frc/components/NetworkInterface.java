package frc.components;

import java.util.HashMap;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableType;
import edu.wpi.first.networktables.NetworkTableValue;

/**
 * Allows interfacing with Networktables; wraps NetworkTable
 */
public class NetworkInterface implements Component {
    private HashMap<String, NetworkTableEntry> tableValues; // contains the respective table entries for each name
    private NetworkTable table;

    /**
     * A basic constructor for a NetworkInterface, initializes internal variables
     * and a table named as tableName
     * 
     * @param tableName the name of the table this NetworkTable instance will
     *                  represent
     */
    public NetworkInterface(String tableName) {
        tableValues = new HashMap<String, NetworkTableEntry>();
        table = NetworkTableInstance.getDefault().getTable(tableName);
    }

    /**
     * Adds a key to the table
     */
    private void put(String key) {
        tableValues.put(key, table.getEntry(key));
    }
    
    /**
     * Returns the value associated with a given key
     * 
     * @param key The key of the object in NetworkTables
     * @return the value in the table at that location, as a NetworkTableValue
     * @throws Exception If the value does not exist in the table
     */
    public NetworkTableValue get(String key) throws Exception {
        if (!tableValues.containsKey(key)) {
            tableValues.put(key, table.getEntry(key));
        }
        return tableValues.get(key).getValue();
    }
    
    /**
     * Sets the given key as a boolean; implicitly defines table values
     * 
     * @param key   The key in the table
     * @param value The boolean value to set key to
     */
    public void setBoolean(String key, boolean value) {
        if (!tableValues.containsKey(key)) {
            put(key);
        }
        tableValues.get(key).setBoolean(value);
    }

    /**
     * Sets the given key as a boolean array; implicitly defines table values
     * 
     * @param key   The key in the table
     * @param value The boolean array value to set key to
     */

    public void setBooleanArray(String key, boolean[] value) {
        if (!tableValues.containsKey(key)) {
            put(key);
        }
        tableValues.get(key).setBooleanArray(value);
    }

    /**
     * Sets the given key as a double; implicitly defines table values
     * 
     * @param key   The key in the table
     * @param value The double value to set key to
     */

    public void setDouble(String key, double value) {
        if (!tableValues.containsKey(key)) {
            put(key);
        }
        tableValues.get(key).setDouble(value);
    }

    /**
     * Sets the given key as a double array; implicitly defines table values
     * 
     * @param key   The key in the table
     * @param value The double array value to set key to
     */

    public void setDoubleArray(String key, double[] value) {
        if (!tableValues.containsKey(key)) {
            put(key);
        }
        tableValues.get(key).setDoubleArray(value);
    }

    /**
     * Sets the given key as a Number (BigInteger, Long, Double, etc); implicitly
     * defines table values
     * 
     * @param key   The key in the table
     * @param value The Number value to set key to
     */

    public void setNumber(String key, Number value) {
        if (!tableValues.containsKey(key)) {
            put(key);
        }
        tableValues.get(key).setNumber(value);
    }

    /**
     * Sets the given key as a Number (BigInteger, Long, Double, etc) Array;
     * implicitly defines table values
     * 
     * @param key   The key in the table
     * @param value The Number Array value to set key to
     */

    public void setNumberArray(String key, Number[] value) {
        if (!tableValues.containsKey(key)) {
            put(key);
        }
        tableValues.get(key).setNumberArray(value);
    }

    /**
     * Sets the given key as a Raw (byte array); implicitly defines table values
     * 
     * @param key   The key in the table
     * @param value The (byte array) raw value to set key to
     */

    public void setRaw(String key, byte[] value) {
        if (!tableValues.containsKey(key)) {
            put(key);
        }
        tableValues.get(key).setRaw(value);
    }

    /**
     * Sets the given key as a String; implicitly defines table values
     * 
     * @param key   The key in the table
     * @param value The String value to set key to
     */

    public void setString(String key, String value) {
        if (!tableValues.containsKey(key)) {
            put(key);
        }
        tableValues.get(key).setString(value);
    }

    /**
     * Sets the given key as a String Array; implicitly defines table values
     * 
     * @param key   The key in the table
     * @param value The String array value to set key to
     */

    public void setStringArray(String key, String[] value) {
        if (!tableValues.containsKey(key)) {
            put(key);
        }
        tableValues.get(key).setStringArray(value);
    }

    /**
     * Sets the given key as an Object; the object must be one of the types accepted
     * in the other set methods. implicitly defines table values
     * 
     * @param key   The key in the table
     * @param value The generic object value to set key to
     */

    public void setValue(String key, Number value) {
        if (!tableValues.containsKey(key)) {
            put(key);
        }
        tableValues.get(key).setValue(value);
    }
}