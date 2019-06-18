package frc.components;
 /**
  * A basic component of the robot. Contains two methods, both of which can be overridden.
  * Example: Drivetrain uses init() to reset its encoders (and do more setup) and tick() to control the treads.
  */
public interface Component {
    /**
     * Initializes any necessary pieces of the Component
     */
    public default void init(){}
    /**
     * Updates the constituent pieces of this component
     */
    public default void tick() {}

}