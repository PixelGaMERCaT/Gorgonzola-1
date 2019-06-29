package frc.components;

import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.Globals;
import frc.robot.RobotMap;

/**
 * Controls what gear the Drivetrain of the robot is in; 
 * @author Jeff
 */ 
public class GearShifter implements Component {
    private Solenoid gearShifter;
    private InputManager inputManager;
    private boolean highGear; //true if the robot is in high gear, false otherwise.

    public GearShifter() {
        highGear = false;
        if (!(Globals.isNSP)) {
            gearShifter = new Solenoid(RobotMap.GEAR_SHIFT_SOLENOID);
        }
    }

    public void init() {
        inputManager = Globals.inputManager;
    }
    
    public void tick() {
        if (inputManager.getGearShiftButton()) {
            highGear = !highGear;
        } 
        gearShifter.set(highGear);
    }

    /**
     * Returns the current gear of the robot
     * @return true if robot is in high gear, false otherwise
     */
    public boolean isHighGear() {
        return highGear;
    }
    /**
     * Sets the gear to the desired gear
     * @param highGear true if we wish to switch to high gear, false otherwise.
     */
    public void setGear(boolean highGear){
        this.highGear=highGear;
    }
}