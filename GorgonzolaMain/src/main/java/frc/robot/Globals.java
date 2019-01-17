package frc.robot;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A class that contains all components of the robot to be accessed. For
 * example, Drivetrain accesses Globals.im (InputManager) for joystick
 * information
 */
public class Globals {

    public static InputManager im;
    public static Drivetrain drivetrain;
    public static Gyro gyro;
    public static NetworkInterface testTable;
    private static ArrayList<Component> components; // Contains all the components in Globals

    /**
     * Initializes all components of globals
     */
    public static void init() {
        components = new ArrayList<Component>();
        im = new InputManager();
        drivetrain = new Drivetrain();
        gyro = new Gyro();
        testTable=new NetworkInterface("test");
        components.addAll(Arrays.asList(im, drivetrain, gyro, testTable));
        components.forEach(c->c.init());
    }

    /**
     * Ticks all components in globals
     */
    public static void tick() {
        components.forEach(c->c.tick());

    }

}