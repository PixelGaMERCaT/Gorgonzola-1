package frc.components;
/**               UNUSED ENUM
 * Defines the type of camera target, as well as some properties of that target relative to the field.
 */
public enum CameraTargetType {
    PLAYER_STATION, CARGO_SHIP_LEFT, CARGO_SHIP_RIGHT, INNER_ROCKET_LEFT, OUTER_ROCKET_LEFT, CARGO_ROCKET_LEFT, CARGO_ROCKET_RIGHT, INNER_ROCKET_RIGHT, OUTER_ROCKET_RIGHT, HATCH_ROCKET_RIGHT;
    private double robotAngle; //angle that the robot must be at to face this target (with 0 being forward), in degrees
    static {
        PLAYER_STATION.robotAngle=180;
        CARGO_SHIP_LEFT.robotAngle = -90.0;
        CARGO_SHIP_RIGHT.robotAngle = 90.0;
        INNER_ROCKET_LEFT.robotAngle = 45;
        INNER_ROCKET_RIGHT.robotAngle = -45;
        OUTER_ROCKET_LEFT.robotAngle = 135;
        OUTER_ROCKET_RIGHT.robotAngle = -135;
        CARGO_ROCKET_LEFT.robotAngle = -90;
        CARGO_ROCKET_RIGHT.robotAngle = 90;
    }
    /**
     * Returns the angle the robot should be at to face this target.
     * @return the angle, in degrees relative to the field (with zero being forward)
     */
    public double getTargetAngle() {
        return robotAngle;
    }

}