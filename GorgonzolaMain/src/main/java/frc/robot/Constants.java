package frc.robot;

/**
 * A class built to contain general constants, especially those used for general
 * calculations within code
 */
public class Constants {
        public static final double MINIMUM_INPUT = .05; // the minimum input necessary to move the robot
        public static final double MAX_DRIVE_VELOCITY_HIGH = 104.774;// IPS
        public static final double MAX_DRIVE_VELOCITY_LOW = 53.414;
        public static final double MAX_TURN_VELOCITY_HIGH = 68.848;
        public static final double MAX_TURN_VELOCITY_LOW = 46.961;
        public static final double DRIVE_ENCU_PER_INCH = 2970.3;

        // Spooky zone (unchecked)
        // TODO reduce spookiness
        public static final double SHOULDER_TICKS_PER_ROTATION = 1024.0;
        public static final double SHOULDER_ENCU_ZERO = 720; // 1249.8, 1227
        public static final double SHOULDER_MIN_POSITION = 12; //inches
        public static final double SHOULDER_MAX_POSITION = 78;
        public static final double SHOULDER_MAX_VELOCITY = 55;
        public static final double SHOULDER_RANGE = SHOULDER_MAX_POSITION-SHOULDER_MIN_POSITION;
        public static final double INTAKE_OFFSET_HATCH=-2.17; //Distance from end of arm to hatch intake/outtake
        public static final double INTAKE_OFFSET_BALL=8.89; //Distance from end of arm to ball intake/outtake
        


        public static final double SHOULDER_KP = 15, SHOULDER_KI = 0, SHOULDER_KD = .5,
                        SHOULDER_KF = 1023.0 / SHOULDER_MAX_VELOCITY;
        public static final double WRIST_STOW_POSITION = 145.0*Math.PI/180.0;
        public static final double WRIST_GEAR_OFFSET = 3.6 * Math.PI/180; //Offset for slack in gearbox (radians)
        public static final double WRIST_TICKS_PER_ROTATION = 1024;
        public static final double WRIST_ENCU_ZERO = Globals.isProto ? -514 : 127;
        public static final double WRIST_ANGLE_RANGE = Math.PI;
        public static final double WRIST_MAX_VELOCITY = Globals.isProto ? 80 : 78;
        public static final double WRIST_KP = 10.0, WRIST_KI = 0, WRIST_KD = 0, WRIST_KF = 1023.0 / WRIST_MAX_VELOCITY;


        public static final double CLIMBER_TICKS_PER_ROTATION = 4096;
        public static final double ARM_JOINT_HEIGHT = 43.937;
        public static final double ARM_LENGTH = 38.7;
        public static final double WRIST_LENGTH = 20.250;

        // </Spooky>

        public static final double MAGIC_KP_HIGH = 0, MAGIC_KI_HIGH = 0, MAGIC_KD_HIGH = 0,
                        MAGIC_KF_HIGH = 1 * (1023.0 / MAX_DRIVE_VELOCITY_HIGH);
        public static final double MAGIC_KP_LOW = 0, MAGIC_KI_LOW = 0, MAGIC_KD_LOW = 0,
                        MAGIC_KF_LOW = 1023.0 / (MAX_DRIVE_VELOCITY_LOW * DRIVE_ENCU_PER_INCH / 10.0);

        public static final double TURN_KP = .04, TURN_KI = 0, TURN_KD = 0;

        public static final double MAX_CLIMB_VELOCITY=1;
        public static final double CLIMB_KP= 0, CLIMB_KI=0, CLIMB_KD=0, CLIMB_KF=1023/MAX_CLIMB_VELOCITY;
}
