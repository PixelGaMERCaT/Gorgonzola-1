package frc.robot;

import java.util.ArrayList;
import java.util.Arrays;

import frc.pose.PoseTracker;

/**
 * A class that contains all components of the robot to be accessed. For
 * example, Drivetrain accesses Globals.im (InputManager) for joystick
 * information
 */
public class Globals {
    public static boolean isNSP = true;
    public static InputManager im;
    public static Drivetrain drivetrain;
    public static Gyro gyro;
    public static NetworkInterface testTable;
    public static LogInterface logger;
    public static PoseTracker poseTracker;
    private static ArrayList<Component> components; // Contains all the components in Globals

    /**
     * Initializes all components of globals
     */
    public static void init() {
        components = new ArrayList<Component>();
        poseTracker = new PoseTracker(50);
        im = new InputManager();
        gyro = new Gyro();
        drivetrain = new Drivetrain();
        testTable = new NetworkInterface("blue");
        logger = new LogInterface();
        components.addAll(Arrays.asList(poseTracker, gyro, /*im*/ drivetrain, testTable, logger));
        components.forEach(c -> c.init());

    }

    /**
     * Ticks all components in globals
     */
    public static void tick() {

        components.forEach(c -> {
            double startTime = System.currentTimeMillis();
            c.tick();
            System.out.println(c + " " + ( System.currentTimeMillis()- startTime));
        });

        System.out.println("Heading "+poseTracker.get(System.nanoTime()-PoseTracker.tickTime*25).heading);
    }

    /**
     * returns whether the robot is in auto
     * @return true if the robot is in auto, false otherwise
     */
    public static boolean isAuto() {
        return true;
        //TODO Actual returning
    }

}