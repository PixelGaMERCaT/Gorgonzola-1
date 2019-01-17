package frc.robot;
public interface Component {
    /**
     * Initializes any necessary pieces of the Component
     */
    public default void init(){}
    /**
     * Updates the constituent pieces of this component; for example, the tick() method for the Drivetrain makes the wheels spin
     */
    public default void tick() {}
    /**
     * Logs any important information from the Component
     */
    public default void log(){}
}