package frc.motionprofiling;

import frc.robot.Constants;

class MPConstants {
    public static final double MAX_VELOCITY=65;
    public static final double MAX_ACCELERATION=150;
    public static final double MAX_JERK=9999;
    public static final double  WHEELBASE_WIDTH=24.1;
    public static final int TICKS_PER_ROTATION=4096;
    public static final double INCHES_PER_ROTATION=Constants.DRIVE_ENCU_PER_INCH*TICKS_PER_ROTATION;


    //PIDVA
    public static final double PATH_KP=0;
    public static final double PATH_KI=0;
    public static final double PATH_KD=0;
    public static final double PATH_KV=-1.0/Constants.MAX_DRIVE_VELOCITY_HIGH;
    public static final double PATH_KA=-1.0/Constants.MAX_DRIVE_ACCELERATION_HIGH;
    public static final double TURN_KP=0;
    public static final double TURN_KI=0;
    public static final double TURN_KD=0;
}