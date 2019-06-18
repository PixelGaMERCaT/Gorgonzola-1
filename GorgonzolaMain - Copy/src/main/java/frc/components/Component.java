package frc.components;
 /**
  * A basic component of the robot. Contains three methods, all of which can be overridden.
  * Example: Drivetrain
  */
public interface Component {
    /**
     * Initializes any necessary pieces of the Component
     */
    public default void init(){}
    /**
     * Updates the constituent pieces of this component; for example, the tick() method for the Drivetrain makes the wheels spin
     */
    public default void tick() {}

}