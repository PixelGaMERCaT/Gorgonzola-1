package frc.robot;
 /**
  * A class containing all of the constants used to locate things on the robot.
  * Exception: all Button indeces are to be placed in ButtonMap
  */
public class RobotMap {

    /*
    public static final int FRONT_RIGHT_TALON=2;
    public static final int FRONT_LEFT_TALON=1;
    public static final int BACK_RIGHT_TALON=4;
    public static final int BACK_LEFT_TALON=3;
    public static final int MIDDLE_LEFT_TALON=12;
    public static final int MIDDLE_RIGHT_TALON=15;
    */
    /*
    public static final int FRONT_RIGHT_TALON=1;
    public static final int FRONT_LEFT_TALON=2;
    public static final int BACK_RIGHT_TALON=3;
    public static final int BACK_LEFT_TALON=4;
    public static final int MIDDLE_LEFT_TALON=12;
    public static final int MIDDLE_RIGHT_TALON=15;
    */
    
    
    //IMPORTANT NOTE: *****_TALON_1 is the one with the encoder for wrist/shoulder
    //FRONT_****_TALON is the one with the encoder for drive
    
    //DRIVE TALONS:
    public static final int FRONT_LEFT_TALON=12;
    public static final int BACK_LEFT_TALON=13;
    public static final int MIDDLE_LEFT_TALON=14;
    public static final int FRONT_RIGHT_TALON=1;
    public static final int BACK_RIGHT_TALON=0;
    public static final int MIDDLE_RIGHT_TALON=11;
    

    

    //Pneumatics:
    public static final int COMPRESSOR=0;
    public static final int GEAR_SHIFT=0;
    public static final int INTAKE_LEFT_SOLENOID=1;
    public static final int INTAKE_RIGHT_SOLENOID=2;
    public static final int CLIMB_KNIFE_SOLENOID= 3;
    public static final int INTAKE_ACTUATOR_SOLENOID=4;
    public static final int HATCH_DEPLOY_SOLENOID=5;
    //Arm:
    public static final int SHOULDER_TALON_1 =2;
    public static final int SHOULDER_TALON_2 =3;
    public static final int WRIST_TALON_1 =5;
    public static final int WRIST_TALON_2 =4;

    //Intake:
    public static final int INTAKE_TALON_1=7;
    public static final int INTAKE_TALON_2=6;
    public static final int CLIMB_TALON_1 = 9;
    public static final int CLIMB_TALON_2 = 10;
}