package frc.robot;

/**
 * A class built to contain general constants, especially those used for general
 * calculations within code
 * @author Jeff
 */ 
public class Constants {

        //Encoder unit value for 0 degrees (level with ground) for shoulder.
        //ABSOLUTELY ESSENTIAL FOR ARM CONTROL. MUST BE SET USING A PHONE OR
        //ELECTRONIC LEVEL BEFORE SETPOINTS ARE ATTEMPTED. 
        public static final double SHOULDER_ENCU_ZERO = Globals.isProto ? -522 : -481;

        //Encoder unit value for 0 degrees (level with the current shoulder angle) of the wrist
        //ABSOLUTELY ESSENTIAL FOR ARM CONTROL. MUST BE SET USING A PHONE OR ELECTRONIC LEVEL
        //BEFORE SETPOINTS ARE ATTEMPTED.
        public static final double WRIST_ENCU_ZERO = Globals.isProto ? -518 : 449;

        //How far, in degrees, the robot has to tip to trigger tip correction
        public static final double TIP_PITCH_THRESHOLD = 20;
        
 
        //Conversion calculated from gear ratios. Converts drivetrain encoder units (ENCU or ticks) to inches
        public static final double DRIVE_ENCU_PER_INCH = 2970.3;

        //Number of encoder units in one (theoretical) rotation
        public static final double SHOULDER_ENCU_PER_ROTATION = 1024.0;
        public static final double WRIST_ENCU_PER_ROTATION = 1024.0;

        //Experimentally determined. Measured in Encoder Units/100 milliseconds (or .1 seconds):
        public static final double SHOULDER_MAX_VELOCITY = 55;
        public static final double WRIST_MAX_VELOCITY = Globals.isProto ? 80 : 78;

        //Bounds for reasonable Shoulder control, in inches off the ground of the tip of the arm joint.
        public static final double SHOULDER_MIN_POSITION = 12;
        public static final double SHOULDER_MAX_POSITION = 80;

        //PIDF Constants:
        public static final double SHOULDER_KP = 15, SHOULDER_KI = 0, SHOULDER_KD = 0,
                        SHOULDER_KF = 1023.0 / SHOULDER_MAX_VELOCITY;
        public static final double WRIST_KP = 10.0, WRIST_KI = 0, WRIST_KD = 0, WRIST_KF = 1023.0 / WRIST_MAX_VELOCITY;
        public static final double TURN_KP = -.09, TURN_KI = 0, TURN_KD = Globals.isProto ? -0.08 : -0.1;

        //Information about the dimensions of the Arm (in inches):
        public static final double ARM_JOINT_HEIGHT = 43.937;
        public static final double ARM_LENGTH = 38.7;
        public static final double WRIST_LENGTH = 20.250;

        
        //Low Gear Drive Statistics (See Github Issuese for problems with velocity measurement):
        public static final double MAX_DRIVE_VELOCITY_LOW = 105;
        public static final double MAX_DRIVE_ACCELERATION_LOW = 105;

        

        
        //Unused except for in deprecated classes:
        public static final double ARM_EMERGENCY_THRESHOLD = 30;
        public static final double ARM_IMU_OFFSET = 14;

}
