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
     * A method to get a number from -1 to 1 denoting the position of the primary joystick
     * @return a number [-1, 1], denoting the position of the joystick along the forward/backward axis
     */
    public double getForward() {
        return getSafetyButton() ? left.getY() : 0;
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
    /**
     * Returns whether the gear shift button (button which shifts gears) is being pushed
     * @return true if the button is being pushed, false otherwise
     */
    public boolean getGearSwitchButton(){
        return left.getRawButton(ButtonMap.GEAR_SHIFT);
    }
    /**
     * Returns whether the elevator should be enabled
     * @return true if the elevator should be enabled, false otherwise.
     */
    public boolean getShoulderButton(){
        return aux.getRawButton(ButtonMap.ELEVATOR_ENABLE);
    }
    public boolean getWristButton(){
        return aux.getRawButton(3);
    }
    /**
     * 
     */
    public double getShoulderHeight(){
        return (1.0+aux.getY())/2.0;
    }
    public boolean getIntakeInButton(){
        return aux.getRawButton(4);
    }
    
    public boolean getIntakeOutButton(){
        return aux.getRawButton(5);
    }

}