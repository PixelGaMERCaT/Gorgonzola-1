package frc.components;

import edu.wpi.first.wpilibj.Joystick;
import frc.CheeseLog.Loggable;
import frc.CheeseLog.SQLType.Bool;
import frc.CheeseLog.SQLType.Decimal;
import frc.CheeseLog.SQLType.Type;
import frc.robot.ButtonMap;
import frc.robot.Globals;
import frc.robot.RobotMap;

/**
 * A component that parses input from the driverstation (joysticks, etc)
 */
public class InputManager implements Component {
    private LogInterface logger;
    private Joystick left, right, aux;

    public InputManager() {
        left = new Joystick(RobotMap.LEFT_STICK);
        right = new Joystick(RobotMap.RIGHT_STICK);
        aux = new Joystick(RobotMap.AUX_STICK);
    }

    public void init() {
        logger = Globals.logger;
        try {
            logger.inputManager = LogInterface.table("Input_Manager",
                    new String[] { "forward", "turn", "safetyButton" },
                    new Type[] { new Decimal(), new Decimal(), new Bool() },
                    new Loggable[] { () -> getForward(), () -> getTurn(), () -> getSafetyButton() });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns whether the hatch output button is being pressed
     * 
     * @return true if we should be outputting a hatch, false otherwise
     */
    public boolean getHatchOutputButton() {
        return aux.getRawButton(4);
    }

    /**
     * A method to get a number from -1 to 1 denoting the position of the primary
     * joystick
     * 
     * @return a number [-1, 1], denoting the position of the joystick along the
     *         forward/backward axis
     */
    public double getForward() {
        return getSafetyButton() ? left.getY() : 0;
    }

    /**
     * Returns whether we should be intaking a hatch
     * 
     * @return true if we should be intaking a hatch, false otherwise.
     */
    public boolean getHatchIntakeButton() {
        return aux.getRawButton(5);

    }

    /**
     * A method to get a number from -1 to 1 denoting the position of the secondary
     * joystick
     * 
     * @return a number [-1, 1], denoting the position of the joystick along the
     *         left/right axis
     */
    public double getTurn() {
        return getSafetyButton() ? right.getX() : 0;
    }

    /**
     * A method that returns true if the safety button is being pressed, and false
     * otherwise
     * 
     * @return true if the safety button is being pressed, and false otherwise.
     */
    public boolean getSafetyButton() {
        return left.getRawButton(ButtonMap.SAFETY);
    }

    /**
     * Returns whether the gear shift button (button which shifts gears) is being
     * pushed
     * 
     * @return true if the button is being pushed, false otherwise
     */
    public boolean getGearSwitchButton() {
        return left.getRawButton(ButtonMap.GEAR_SHIFT);
    }

    public boolean getAuxButton(int id) {
        return aux.getRawButton(id);
    }

    /**
     * Returns the power with which the CAM should be moving.
     * 
     * @return a number [-1, 1], denoting the position of the joystick along the
     *         forward/backward axis
     */
    public double getClimb() {
        return right.getRawButton(1) ? right.getY() : 0;
    }

    /**
     * Returns whether the shoulder should be enabled
     * 
     * @return true if the shoulder should be enabled, false otherwise.
     */
    public boolean getShoulderButton() {
        return aux.getRawButton(ButtonMap.ELEVATOR_ENABLE);
    }

    /**
     * Returns whether the wrist should be enabled
     * 
     * @return true if the wrist should be enabled, false otherwise.
     */
    public boolean getWristButton() {
        return aux.getRawButton(3);
    }

    /**
     * Returns a number denoting the position of the shoulder within the range of
     * the shoulder
     * 
     * @return 0 for the bottom of the shoulder's range, .5 for the middle, etc
     */
    public double getShoulderHeight() {
        return (1.0 + aux.getY()) / 2.0;
    }

    /**
     * Returns whether the intake should be spinning inwards
     * 
     * @return true if the intake should be spinning in, false otherwise.
     */
    public boolean getIntakeInButton() {
        return aux.getRawButton(8);
    }

    /**
     * Returns whether the intake should be spinning outwards
     * 
     * @return true if the intake should be spinning outwards, false otherwise
     */
    public boolean getIntakeOutButton() {
        return aux.getRawButton(9);
    }

}