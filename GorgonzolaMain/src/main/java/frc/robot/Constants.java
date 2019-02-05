package frc.robot;

/**
 * A class built to contain general constants, especially those used for general
 * calculations within code
 */
public class Constants {
    public static final double MINIMUM_INPUT = .05; // the minimum input necessary to move the robot
    public static final double MAX_DRIVE_VELOCITY = 31800; //In encoder units
    public static  final double MAX_TURN_VELOCITY = 15622;
	public static final double MAGIC_DRIVE_MAX_ROTATIONS = 10;

    public static final double TURN_KP=.04, TURN_KI=0, TURN_KD=0;
}
