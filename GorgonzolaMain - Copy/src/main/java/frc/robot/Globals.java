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
import frc.components.Shooter;
import frc.components.TipCorrector;
import frc.components.arm.ArmIMU;
import frc.components.arm.Intake;
import frc.components.arm.Shoulder;
import frc.components.arm.Wrist;
import frc.components.lights.LightController;
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
    private static ArrayList<Component> components; // Contains all the components in Globals
    public static CameraManager cameraManager;
    public static ArmIMU armIMU;
    public static Shooter shootyMcShootyFace;
    /**
     * Initializes all components of globals
     */
    public static void init() {
        components = new ArrayList<Component>();
        logger = new LogInterface();
        im = new InputManager();
        // cameraManager = new CameraManager();
        // compressor = new Compressor(RobotMap.COMPRESSOR);
        // armIMU=new ArmIMU();
        // drivetrain = new Drivetrain();
        
        // lightController = new LightController();
        // compressor.setClosedLoopControl(true);
        // intake = new Intake();
        // gearShifter = new GearShifter();
        // poseTracker = new PoseTracker(50);
        // gyro = new Gyro();
        // robotDataTable = new NetworkInterface("RobotData");
        
        // shoulder = new Shoulder();
        // wrist = new Wrist();
        // climber = new Climber();
        // tipCorrector = new TipCorrector();
        shootyMcShootyFace= new Shooter();
        components.addAll(Arrays.asList(im, shootyMcShootyFace, logger  /*drivetrain, tipCorrector, shoulder, wrist, armIMU, gearShifter, intake, gyro,
                poseTracker, climber, cameraManager, lightController*/));
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
        // try {
        //     lightController.Update();
        //     //System.out.println("lights updating");
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
    }

}