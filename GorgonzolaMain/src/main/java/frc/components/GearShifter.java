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
    private boolean highGear;
    private boolean buttonPressed = false;

    public GearShifter() {
        highGear = false;
        if (!(Globals.isNSP || Globals.isAdelost)) {
            switcher = new Solenoid(RobotMap.GEAR_SHIFT);
        }
    }

    public void init() {
        im = Globals.im;
        highGear = false;
    }

    public void tick() {
        if (im.getGearSwitchButton() && !buttonPressed) {
            highGear = !highGear;
            buttonPressed = true;
        } else if (!im.getGearSwitchButton()) {
            buttonPressed = false;
        }
        if (!(Globals.isNSP || Globals.isAdelost)) {
            switcher.set(highGear);

        }
    }

    /**
     * Returns the current gear of the robot
     * @return true if robot is in high gear, false otherwise
   
     */
    public boolean isHighGear() {
        return highGear;
    }
}