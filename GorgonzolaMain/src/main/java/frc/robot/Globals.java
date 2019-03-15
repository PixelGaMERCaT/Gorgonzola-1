package frc.robot;

import java.util.ArrayList;
import java.util.Arrays;

import edu.wpi.first.wpilibj.Compressor;
import frc.components.CameraManager;
import frc.components.Climber;
import frc.components.Component;
import frc.components.Drivetrain;
import frc.components.GearShifter;
import frc.components.Gyro;
import frc.components.InputManager;
import frc.components.Intake;
import frc.components.LogInterface;
import frc.components.NetworkInterface;
import frc.components.Shoulder;
import frc.components.Wrist;
import frc.components.lights.PWMLightController;
import frc.components.pose.PoseTracker;

/**
 * A class that contains all components of the robot to be accessed. For
 * example, Drivetrain accesses Globals.im (InputManager) for joystick
 * information
 */
public class Globals {
    public static boolean isNSP = false;
    public static boolean isAdelost = false;
    public static boolean isProto = true;

    public static InputManager im;
    public static Drivetrain drivetrain;
    public static Gyro gyro;
    public static NetworkInterface robotDataTable;
    public static LogInterface logger;
    public static PoseTracker poseTracker;
    public static GearShifter gearShifter;
    public static Shoulder shoulder;
    public static Wrist wrist;
    public static Climber climber;
    public static Intake intake;
    public static Compressor compressor;
    private static ArrayList<Component> components; // Contains all the components in Globals
    private static CameraManager cameraManager;
    /**
     * Initializes all components of globals
     */
    public static void init() {
        components = new ArrayList<Component>();
        compressor = new Compressor(RobotMap.COMPRESSOR);
        drivetrain = new Drivetrain();
        im = new InputManager();
        logger = new LogInterface();
        PWMLightController pwmLightController = new PWMLightController(0, 1);
        compressor.setClosedLoopControl(true);
        intake = new Intake();
        gearShifter = new GearShifter();
        poseTracker = new PoseTracker(50);
        gyro = new Gyro();
        robotDataTable = new NetworkInterface("RobotData");
        shoulder = new Shoulder();
        wrist = new Wrist();
        climber = new Climber();
        components.addAll(Arrays.asList(im, drivetrain, shoulder, wrist, pwmLightController, gearShifter, intake, gyro,
                poseTracker, climber, logger));
                //components.addAll(Arrays.asList(poseTracker, gearShifter, gyro, im, drivetrain, testTable, logger ));
        components.forEach(c -> c.init());

    }

    /**
     * Ticks all components in globals
     */
    public static void tick() {
        components.forEach(c -> {
            try {
                c.tick();
            } catch (Exception e) {
                System.err.println("Problem ticking " + c);
                e.printStackTrace();
            }
        });
    }


}