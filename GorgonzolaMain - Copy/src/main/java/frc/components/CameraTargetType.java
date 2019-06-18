package frc.components;

public enum CameraTargetType {
    PLAYER_STATION, CARGO_SHIP_LEFT, CARGO_SHIP_RIGHT, INNER_ROCKET_LEFT, OUTER_ROCKET_LEFT, CARGO_ROCKET_LEFT, CARGO_ROCKET_RIGHT, INNER_ROCKET_RIGHT, OUTER_ROCKET_RIGHT, HATCH_ROCKET_RIGHT;
    private double angle; //angle of the robot to face it
    static {
        PLAYER_STATION.angle=180;
        CARGO_SHIP_LEFT.angle = -90.0;
        CARGO_SHIP_RIGHT.angle = 90.0;
        INNER_ROCKET_LEFT.angle = 45;
        INNER_ROCKET_RIGHT.angle = -45;
        OUTER_ROCKET_LEFT.angle = 135;
        OUTER_ROCKET_RIGHT.angle = -135;
        CARGO_ROCKET_LEFT.angle = -90;
        CARGO_ROCKET_RIGHT.angle = 90;
    }
    public double getTargetAngle() {
        return angle;
    }

}