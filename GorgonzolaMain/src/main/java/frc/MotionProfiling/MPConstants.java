package frc.motionprofiling;

import frc.robot.Constants;
/**
 * Holds constants important for motion profiling. All constants are for low gear.
 * @author Jeff
 */
public class MPConstants {

    //Path/Config Constants:
    public static final double MAX_VELOCITY = 65; //Inches per second. See Github for issues with velocity recording.
    public static final double MAX_ACCELERATION = 80; //Inches per (second)^2
    public static final double MAX_JERK = 9999; //Inches per (second)^3, the maximum allowable change in acceleration
    public static final double WHEELBASE_WIDTH = 24.1; //Experimentally determined by spinning the robot
    public static final int TICKS_PER_ROTATION = 4096; //Based on sensor values
    public static final double INCHES_PER_ROTATION = TICKS_PER_ROTATION / Constants.DRIVE_ENCU_PER_INCH; //Calculated so that Encoder follower will return proper distances.
    public static final double DELTA_TIME = .02; //The number of seconds between ticks

    //PIDVA:
    public static final double PATH_KP = -0.09;
    public static final double PATH_KI = 0;//NEVER SET
    public static final double PATH_KD = 0;
    public static final double PATH_KV =-1.3/Constants.MAX_DRIVE_VELOCITY_LOW; 
    public static final double PATH_KA =-.2/Constants.MAX_DRIVE_ACCELERATION_LOW;
    public static final double TURN_KP =0.09;
    public static final double TURN_KI = 0;//NEVER SET
    public static final double TURN_KD = 0;
    public static final double TURN_KF = 0;

}