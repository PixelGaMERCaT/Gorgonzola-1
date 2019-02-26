package frc.components;

import edu.wpi.first.wpilibj.Joystick;
import frc.CheeseLog.Loggable;
import frc.CheeseLog.SQLType.Bool;
import frc.CheeseLog.SQLType.Decimal;
import frc.CheeseLog.SQLType.Type;
import frc.robot.ButtonMap;
import frc.robot.Globals;

/**
 * A component that parses input from the driverstation (joysticks, etc)
 */
public class InputManager implements Component {
    private LogInterface logger;
    private Joystick left, right, aux;
    private ArmControlState state; 

    public InputManager() {
        state = ArmControlState.AUTO;
        left = new Joystick(ButtonMap.LEFT_STICK);
        right = new Joystick(ButtonMap.RIGHT_STICK);
        aux = new Joystick(ButtonMap.AUX_STICK);
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

    public void tick() {
        if (aux.getRawButtonPressed(ButtonMap.POS_MANUAL_OVERRIDE)) {
            switch (state) {
            case POSITON_MANUAL:
                state = ArmControlState.AUTO;
                break;
            default:
                state = ArmControlState.POSITON_MANUAL;
            }
        }

        if (aux.getRawButtonPressed(ButtonMap.FULL_MANUAL_OVERRIDE)) {
            switch (state) {
            case FULL_MANUAL:
                state = ArmControlState.AUTO;
                break;
            default:
                state = ArmControlState.FULL_MANUAL;
            }
        }
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
     * Returns whether the hatch button is being pressed
     * 
     * @return true if we should be holding a hatch, false otherwise.
     */
    public boolean getHatchButton() {
        return aux.getRawButton(ButtonMap.SUCC_TOGGLE_BUTTON);

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
        return left.getRawButton(ButtonMap.DRIVE_SAFETY);
    }

    /**
     * Returns whether the gear shift button (button which shifts gears) is being
     * pushed
     * 
     * @return true if the button is being pushed, false otherwise
     */
    public boolean getGearSwitchButton() {
        return right.getRawButton(ButtonMap.GEAR_SHIFT);
    }

    

    /**
     * Gives the desired location of the arm, based on the following:
     * @return the desired height of the arm
     */
    public ArmHeight getArmPosition() {
        if (getArmSafetyButton()) {
            if (state == ArmControlState.FULL_MANUAL)
                return ArmHeight.FULL_MANUAL;
            if (state == ArmControlState.POSITON_MANUAL)
                return ArmHeight.POSITION_MANUAL;

            if (aux.getRawButton(1)) {
                return ArmHeight.GROUND_PICKUP;
            }
            if (aux.getRawButton(2)) {
                return ArmHeight.BALL_LOW;
            }
            if (aux.getRawButton(3)) {
                return ArmHeight.BALL_MEDIUM;
            }
            if (aux.getRawButton(4)) {
                return ArmHeight.BALL_HIGH;
            }
            int pov = aux.getPOV();
            System.out.println("pov " + pov);
            if (pov == ButtonMap.HATCH_LOW) {
                return ArmHeight.HATCH_LOW;
            }
            if (pov == ButtonMap.HATCH_MID) {
                return ArmHeight.HATCH_MEDIUM;
            }
            if (pov == ButtonMap.HATCH_HIGH) {
                return ArmHeight.HATCH_HIGH;
            }
            if (pov == ButtonMap.STOW) {
                return ArmHeight.STOW;
            }

            return ArmHeight.NO_CHANGE; //Nothing will be done
        } else {
            return ArmHeight.NO_MOVEMENT;
        }

    }

    public boolean getArmSafetyButton() {
        return aux.getRawButton(ButtonMap.AUX_SAFETY);
    }

    /**
     * Returns the power with which the CAM should be moving.
     * 
     * @return a number [-1, 1], denoting the position of the joystick along the
     *         forward/backward axis
     */
    public double getClimb() {
        return right.getY();
    }

    public boolean getManualClimbButton() {
        return right.getRawButton(ButtonMap.CAM_MANUAL);
    }

    public boolean getAutoClimbButton() {
        return right.getRawButton(ButtonMap.CAM_AUTO);
    }


    /**
     * Returns a number denoting the position of the shoulder within the range of
     * the shoulder
     * 
     * @return 0 for the bottom of the shoulder's range, .5 for the middle, etc
     */
    public double getShoulderManualHeight() {
        return aux.getRawAxis(ButtonMap.SHOULDER_STICK);
    }

    public double getWristManualPosition() {
        return aux.getRawAxis(ButtonMap.WRIST_STICK);
    }

    /**
     * Returns whether the intake should be spinning inwards
     * 
     * @return true if the intake should be spinning in, false otherwise.
     */
    public boolean getBallIntakeInButton() {
        return aux.getRawButton(ButtonMap.BALL_INTAKE);
    }

    /**
     * Returns whether the intake should be spinning outwards
     * 
     * @return true if the intake should be spinning outwards, false otherwise
     */
    public boolean getIntakeOutButton() {
        return aux.getRawButton(ButtonMap.BALL_OUTPUT);

    }

    public boolean getClimbKnives() {
        return left.getRawButton(ButtonMap.KNIFE_DEPLOY);
    }

}