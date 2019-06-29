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
import frc.components.LogInterface;
import frc.components.NetworkInterface;
import frc.components.TipCorrector;
import frc.components.arm.Intake;
import frc.components.arm.Shoulder;
import frc.components.arm.Wrist;
import frc.components.lights.LightController;
import frc.components.pose.PoseTracker;

/**
 * A class that contains all components of the robot to be accessed. For
 * example, Drivetrain accesses Globals.inputManager for joystick
 * information
 * @author Jeff
 */
public class Globals {
    public static boolean isNSP = false; //True if the robot the code is running on is "Not Spare Parts"
    public static boolean isProto = false;  //True if the robot the code is running on is GorgonzolaProto
    
    public static InputManager inputManager;
    public static Drivetrain drivetrain;
    public static Gyro gyro;
    public static NetworkInterface robotDataTable;
    public static LogInterface logger;
    public static PoseTracker poseTracker;
    public static GearShifter gearShifter;
    public static LightController lightController;
    public static Shoulder shoulder;
    public static Wrist wrist;
    public static Climber climber;
    public static Intake intake;
    public static TipCorrector tipCorrector;
    public static Compressor compressor;
    public static CameraManager cameraManager;

    private static ArrayList<Component> components; // Contains all the active components in Globals

    /**
     * Initializes all components of globals
     */
    public static void init() {
        components = new ArrayList<Component>();
        logger = new LogInterface();
        inputManager = new InputManager();
        cameraManager = new CameraManager();
        compressor = new Compressor(RobotMap.COMPRESSOR);
        drivetrain = new Drivetrain();

        lightController = new LightController();
        compressor.setClosedLoopControl(true);
        intake = new Intake();
        gearShifter = new GearShifter();
        poseTracker = new PoseTracker(50);
        gyro = new Gyro();
        robotDataTable = new NetworkInterface("RobotData");

        shoulder = new Shoulder();
        wrist = new Wrist();
        climber = new Climber();
        tipCorrector = new TipCorrector();

        components.addAll(Arrays.asList(inputManager, drivetrain, tipCorrector, shoulder, wrist, gearShifter, intake,
                gyro, poseTracker, climber, cameraManager, lightController, logger));
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