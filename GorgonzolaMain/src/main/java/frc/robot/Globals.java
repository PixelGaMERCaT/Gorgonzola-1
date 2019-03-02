package frc.robot;

import java.util.ArrayList;
import java.util.Arrays;

import edu.wpi.first.wpilibj.Compressor;
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
    public static boolean isProto = false;

    public static InputManager im;
    public static Drivetrain drivetrain;
    public static Gyro gyro;
    public static NetworkInterface testTable;
    public static LogInterface logger;
    public static PoseTracker poseTracker;
    public static GearShifter gearShifter;
    public static Shoulder shoulder;
    public static Wrist wrist;
    public static Climber climber;
    public static Intake intake;
    public static Compressor compressor;
    private static ArrayList<Component> components; // Contains all the components in Globals

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
        testTable = new NetworkInterface("blue");
        shoulder = new Shoulder();
        wrist = new Wrist();
        climber = new Climber();
        //GYRO IS COMMENTED OUT
        components.addAll(Arrays.asList(im, pwmLightController, drivetrain, shoulder, wrist, gearShifter, intake, gyro,
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

    /**
     * returns whether the robot is in auto
     * @return true if the robot is in auto, false otherwise
     */
    public static boolean isAuto() {
        return true; //TODO actually return
    }

}