package frc.robot;

/**
 * A class containing all of the constants used to ID things on the robot
 * Example: all solenoid ID's are stored below
 * @author Jeff
 */ 
public class RobotMap {

    //DRIVE TALONS:
    public static final int FRONT_LEFT_TALON = 12;
    public static final int BACK_LEFT_TALON = 13;
    public static final int MIDDLE_LEFT_TALON = 14;
    public static final int FRONT_RIGHT_TALON = 1;
    public static final int BACK_RIGHT_TALON = 0;
    public static final int MIDDLE_RIGHT_TALON = 11;

    //Pneumatics:
    public static final int COMPRESSOR = 0;
    public static final int GEAR_SHIFT_SOLENOID = 0;
    public static final int LEFT_SUCTION_CUP = 1;
    public static final int RIGHT_SUCTION_CUP = 2;
    public static final int CLIMB_KNIFE_SOLENOID = 3;
    public static final int HATCH_INTAKE_SOLENOID = 4;
    public static final int HATCH_DEPLOY_SOLENOID = 5;
    
    //Arm:
    public static final int SHOULDER_TALON_1 = 2;
    public static final int SHOULDER_TALON_2 = 3;
    public static final int WRIST_TALON_1 = 5;
    public static final int WRIST_TALON_2 = 4;

    //Intake:
    public static final int INTAKE_TALON_1 = 7;
    public static final int INTAKE_TALON_2 = 6;

    //UNUSED:
    public static final int ARM_IMU_TALON = 10;
}