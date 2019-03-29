package frc.components;

import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.Globals;
import frc.robot.RobotMap;

/**
 * A wrapper for the Gear shifter solenoid; allows for access to the current gear.
 */
public class GearShifter implements Component {
    private Solenoid switcher;
    private InputManager im;
    public boolean highGear;

    public GearShifter() {
        highGear = false;
        if (!(Globals.isNSP)) {
            switcher = new Solenoid(RobotMap.GEAR_SHIFT);
        }
    }

    public void init() {
        im = Globals.im;
    }
    
    public void tick() {
        if (im.getGearSwitchButton()) {
            highGear = !highGear;
        } 
        switcher.set(highGear);
        //System.out.println("high gear!"+highGear);
    }

    /**
     * Returns the current gear of the robot
     * @return true if robot is in high gear, false otherwise
     */
    public boolean isHighGear() {
        return highGear;
    }
}