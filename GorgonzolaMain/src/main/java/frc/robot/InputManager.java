package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import frc.CheeseLog.Output.*;
import frc.CheeseLog.*;
import frc.CheeseLog.SQLType.*;

/**
 * A component that parses input from the driverstation (joysticks, etc)
 */
public class InputManager implements Component {
    LogInterface logger;
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
     * A method to get a number from -1 to 1 denoting the position of the primary joystick
     * @return a number [-1, 1], denoting the position of the joystick along the forward/backward axis
     */
    public double getForward() {
        return getSafetyButton() ? left.getRawAxis(0) : 0;
    }

    /**
     * A method to get a number from -1 to 1 denoting the position of the primary joystick
     * @return a number [-1, 1], denoting the position of the joystick along the forward/backward axis
     */
    public double getTurn() {
        return getSafetyButton() ? right.getX() : 0;
    }

    /**
     * A method that returns true if the safety button is being pressed, and false otherwise
     * @return true if the safety button is being pressed, and false otherwise.
     */
    public boolean getSafetyButton() {
        return left.getRawButton(ButtonMap.SAFETY);
    }

    public boolean getGearSwitchButton(){
        return left.getRawButton(ButtonMap.GEAR_SHIFT);
    }
}