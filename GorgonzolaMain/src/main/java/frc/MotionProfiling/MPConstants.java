package frc.MotionProfiling;

import frc.robot.Constants;

class MPConstants {
    public static final double MAX_VELOCITY = 130;//65
    public static final double MAX_ACCELERATION = 170;
    public static final double MAX_JERK = 9999;
    public static final double WHEELBASE_WIDTH = 24.1;
    public static final int TICKS_PER_ROTATION = 4096;
    public static final double INCHES_PER_ROTATION = TICKS_PER_ROTATION / Constants.DRIVE_ENCU_PER_INCH;
    public static final double DELTA_TIME = .02;
    //divide by encoder tick count, multiply by inches per rotation

    //PIDVA
    public static final double PATH_KP = -.08;//-.08
    public static final double PATH_KI = 0;//NEVER SET
    public static final double PATH_KD = 0;
    public static final double PATH_KV = -0.495 / Constants.MAX_DRIVE_VELOCITY_HIGH;//-0.49 / Constants.MAX_DRIVE_VELOCITY_HIGH;//
    public static final double PATH_KA = -0.28/ Constants.MAX_DRIVE_ACCELERATION_HIGH;//-0.3 / Constants.MAX_DRIVE_ACCELERATION_HIGH;//
    public static final double TURN_KP = .06;
    public static final double TURN_KI = 0;//NEVER SET
    public static final double TURN_KD = .255;
    public static final double TURN_KF = 0;

}