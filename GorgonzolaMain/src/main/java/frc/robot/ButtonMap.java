package frc.robot;

/**
 * A class containing all the IDs for joystick input
 * @author Jeff
 */ 
public class ButtonMap {
    //Joystick IDs:
    public static final int LEFT_STICK = 0;
    public static final int RIGHT_STICK = 1;
    public static final int AUX_STICK = 5;

    //<Driver Buttons>
    //Primary Joystick:
    public static final int DRIVE_SAFETY = 1;
    public static final int CLIMB_DEPLOY = 2;
    
    //Secondary Joystick:
    public static final int GEAR_SHIFT = 1;
    public static final int AUTO_ALIGN = 3;
    public static final int TIP_ENABLE = 11;
    public static final int RAINBOW_BUTTON = 7;

    //Shared:
    public static final int TANK_DRIVE_1 = 8;
    public static final int TANK_DRIVE_2 = 9;
    
    //</Driver Buttons>

    
    
    //Operator:

    //Joystick IDs:
    public static final int SHOULDER_STICK = 3;
    public static final int WRIST_STICK = 1;

    //<Controller Buttons>
    public static final int AUX_SAFETY = 5;
    
    public static final int HATCH_INTAKE_BUTTON = 6;
    public static final int HATCH_OUTPUT_BUTTON = 8;
    
    //POV (represents an angle in degrees of black Directional pad)
    public static final int BALL_INTAKE = 180;
    public static final int BALL_OUTPUT = 0;
    public static final int BALL_CARGO = 90;

    //Arm Override Buttons:
    public static final int SHOULDER_MANUAL_OVERRIDE = 9;
    public static final int WRIST_MANUAL_OVERRIDE = 10;
    
    //Setpoint Buttons:
    public static final int STOW = 1;
    public static final int HATCH_LOW = 2;
    public static final int HATCH_MEDIUM = 3;
    public static final int HATCH_HIGH = 4;
    public static final int HATCH_BALL_SWITCH = 7;
    public static final int GROUND_PICKUP = 1;
    public static final int BALL_LOW = 2;
    public static final int BALL_MEDIUM = 3;
    public static final int BALL_HIGH = 4;
    //</Controller Buttons>

    

}