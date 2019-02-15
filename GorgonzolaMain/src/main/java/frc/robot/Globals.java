package frc.robot;

import java.util.ArrayList;
import java.util.Arrays;

import frc.components.Climber;
import frc.components.Component;
import frc.components.Drivetrain;
import frc.components.GearShifter;
import frc.components.Gyro;
import frc.components.InputManager;
import frc.components.LogInterface;
import frc.components.NetworkInterface;
import frc.components.PneumaticController;
import frc.components.Shoulder;
import frc.components.Wrist;
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
    private static Gyro gyro;
    public static NetworkInterface testTable;
    public static LogInterface logger;
    public static PoseTracker poseTracker;
    public static GearShifter gearShifter;
    public static Shoulder shoulder;
    public static Wrist wrist;
    public static Climber climber;
    public static PneumaticController pneumaticController;
    private static ArrayList<Component> components; // Contains all the components in Globals

    /**
     * Initializes all components of globals
     */
    public static void init() {
        pneumaticController=new PneumaticController();
        gearShifter = new GearShifter();
        components = new ArrayList<Component>();
        //poseTracker = new PoseTracker(50);
        im = new InputManager();
        //gyro = new Gyro();
        drivetrain = new Drivetrain();
        testTable = new NetworkInterface("blue");
        logger = new LogInterface();
        shoulder=new Shoulder();
        wrist=new Wrist();
        
        components.addAll(Arrays.asList(im, shoulder, wrist, logger));
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