package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

/**
 * A component that parses input from the driverstation (joysticks, etc)
 */
public class InputManager implements Component {

    private Joystick left, right, aux;

    public InputManager() {
        left = new Joystick(RobotMap.LEFT_STICK);
        right = new Joystick(RobotMap.RIGHT_STICK);
        aux = new Joystick(RobotMap.AUX_STICK);
    }

    public void init() {
        try {
            Globals.logger.logger.addStatement("IM", "forward", frc.CheeseLog.Type.DECIMAL, () -> {
                return getForward();
            }, true, false, false);
            Globals.logger.logger.addStatement("IM", "turn", frc.CheeseLog.Type.DECIMAL, () -> {
                return getTurn();
            }, true, false, false);

        } catch (Exception e) {
            // TODO Auto-generated catch block
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
}