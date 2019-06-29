package frc.components;

import edu.wpi.first.wpilibj.Joystick;
import frc.CheeseLog.Loggable;
import frc.CheeseLog.SQLType.Bool;
import frc.CheeseLog.SQLType.Decimal;
import frc.CheeseLog.SQLType.Type;
import frc.components.arm.ArmPosition;
import frc.robot.ButtonMap;
import frc.robot.Globals;

/**
 * A component that parses input from the driverstation (joysticks, controllers etc)
 * @author Jeff
 */ 
public class InputManager implements Component {
    private LogInterface logger;
    private Joystick left, right, aux;
    private boolean shoulderManual, wristManual; //Determine whether the wrist or shoulder are in "manual control" mode

    private boolean shoulderSaveOverriden = false; //Keeps the override from being inverted multiple times per button press.
    private boolean tankDriveSet = false; //Keeps the tankDrive status from being inverted multiple times per button press.

    public InputManager() {
        shoulderManual = false;
        wristManual = false;
        left = new Joystick(ButtonMap.LEFT_STICK);
        right = new Joystick(ButtonMap.RIGHT_STICK);
        aux = new Joystick(ButtonMap.AUX_STICK);
    }

    public void init() {
        logger = Globals.logger;
        try {
            logger.inputManager = LogInterface.createTable("Input_Manager",
                    new String[] { "forward", "turn", "safetyButton", "shoulderjoy", "wristjoy" },
                    new Type[] { new Decimal(), new Decimal(), new Bool(), new Decimal(), new Decimal() },
                    new Loggable[] { () -> getPrimaryJoyY(), () -> getSecondaryJoyX(), () -> getDriveSafetyButton(),
                            () -> getShoulderManualPosition(), () -> getWristManualPosition() });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void tick() {
        //Update ArmManual booleans
        if (aux.getRawButtonPressed(ButtonMap.SHOULDER_MANUAL_OVERRIDE)) {
            shoulderManual = !shoulderManual;
        }
        if (aux.getRawButtonPressed(ButtonMap.WRIST_MANUAL_OVERRIDE)) {
            wristManual = !wristManual;
        }
    }

    /**
     * Returns a number from -1 to 1 denoting the position of the primary
     * joystick along the forward/backward axis
     * Depending on the control mode, this can be used in several ways:
     *      Arcade Drive (Normal Operation): Used to determine how much motor power to apply "forward,"
     *          that is, the more the joystick is moved forward, the faster the robot will drive forward
     *      Tank Drive (Emergency Operation): Used to determine how much motor power to apply to the left side of the robot.
     *          that is, the more the joystick is moved forward, the faster the left tread will move forward.
     * @return a number [-1, 1], denoting the position of the joystick along the
     *         forward/backward axis
     */
    public double getPrimaryJoyY() {
        return getDriveSafetyButton() ? left.getY() : 0;
    }

    /**
     * Returns a number from -1 to 1 denoting the amount that the secondary joystick is being moved from left to right
     * Depending on the control mode, this can be used in several ways:
     *      Arcade Drive (Normal Operation): Used to determine how much motor power to apply to "turn,"
     *             that is, the more the joystick is moved to the side, the more the robot will turn.
     *      Tank Drive (Emergency Operation): Not used.
     *      Auto-Align (Camera-controlled turning): Used to adjust the angle the robot is turning to in order
     *             to allow drivers to account for faulty camera placement or a bad camera reading.
     * @return a number [-1, 1], denoting the position of the joystick along the left/right axis
     */
    public double getSecondaryJoyX() {
        return getDriveSafetyButton() ? right.getX() : 0;
    }

    /**
     * Returns a number from -1 to 1 denoting the position of the secondary
     * joystick along the forward/backward axis
     * Depending on the control mode, this can be used in several ways:
     *      Arcade Drive (Normal Operation): Unused
     *      Tank Drive (Emergency Operation): Used to determine how much motor power to apply to the right side of the robot.
     *          that is, the more the joystick is moved forward, the faster the right tread will move forward
     * @return a number [-1, 1], denoting the position of the joystick along the
     *         forward/backward axis
     */
    public double getSecondaryJoyY() {
        return getDriveSafetyButton() ? right.getY() : 0;
    }

    /**
     * Returns a number in the range [-1,1] denoting the position of the operator's shoulder adjust joystick
     * In different controlmodes:
     *      Manual: How much power is being applied to the shoulder motors (-1 or 1 for full power in either direction)
     *      Certain Setpoints: How much to adjust the setpoint by -- changing the height of the shoulder 
     *             by small increments  --  "manual adjust" 
     * @return a number between -1 and 1 inclusive indicating the position of the joystick
     */
    public double getShoulderManualPosition() {
        return aux.getRawAxis(ButtonMap.SHOULDER_STICK);
    }

    /**
     * Returns a number in the range [-1,1] denoting the position of the operator's wrist adjust joystick
     * In different Controlmodes:
     *       Manual: How much power is being applied to the wrist motors (-1 or 1 for full power in either direction)
     *       Certain Setpoints: How much to adjust the setpoint by -- changing the angle of the wrist 
     *             by small increments  --  "manual adjust" 
     * @return a number between -1 and 1 inclusive indicating the position of the joystick
     */
    public double getWristManualPosition() {
        return aux.getRawAxis(ButtonMap.WRIST_STICK);
    }

    /**
     * Determines if the shoulder save override button has been pressed. This will turn off the feature
     * that disables the arm upon the detection of a faulty encoder.
     * @return true if this is the first tick that the shoulder save override button has been pressed,
     *         false if the button is not being pressed or is being held from a previous tick
     */
    public boolean getShoulderSaveOverride() {
        if (aux.getPOV() == 270 & !shoulderSaveOverriden) {
            shoulderSaveOverriden = true;
            return true;
        } else if (aux.getPOV() != 270) {
            shoulderSaveOverriden = false;
        }
        return false;
    }

    /**
     * Determines whether the driver would like to toggle the Tank Drive Override, to switch from 
     * Arcade to Tank drive or from Tank to Arcade drive
     * This happens only when the driver presses all four of the front-facing buttons (2 per joystick) at once
     * @return true if this is the first tick that all four buttons have been pressed, false otherwise
     */
    public boolean getTankDriveOverride() {
        //True if all four buttons are pressed at the same time:
        boolean buttonsPressed = right.getRawButton(ButtonMap.TANK_DRIVE_1)
                && right.getRawButton(ButtonMap.TANK_DRIVE_2) && left.getRawButton(ButtonMap.TANK_DRIVE_1)
                && left.getRawButton(ButtonMap.TANK_DRIVE_2);

        if (!tankDriveSet && buttonsPressed) {
            tankDriveSet = true;
            return true;
        } else if (!buttonsPressed) {
            tankDriveSet = false;
        }
        return false;
    }

    /**
     * Determines if the operator wants the arm to move to a new setpoint and returns the corresponding ArmPosition.
     * If the operator is not holding the safety button, returns ArmPosition.NO_MOVEMENT
     * If the operator has enabled manual control (see InputManager.tick()), returns FULL_MANUAL    
     * If the operator does not want to go to a new setpoint, returns ArmPosition.NO_CHANGE
     * Otherwise, returns a setpoint based off of the current controller input.
     * @return A setpoint corresponding to the current Operator input
     */
    public ArmPosition getArmPosition() {
        if (getArmSafetyButton()) {
            if (shoulderManual) {
                return ArmPosition.FULL_MANUAL;
            }
            if (aux.getRawButton(ButtonMap.HATCH_BALL_SWITCH)) {
                if (aux.getRawButton(ButtonMap.GROUND_PICKUP)) {
                    return ArmPosition.GROUND_PICKUP;
                }
                if (aux.getRawButton(ButtonMap.BALL_LOW)) {
                    return ArmPosition.BALL_LOW;
                }
                if (aux.getRawButton(ButtonMap.BALL_MEDIUM)) {
                    return ArmPosition.BALL_MEDIUM;
                }
                if (aux.getRawButton(ButtonMap.BALL_HIGH)) {
                    return ArmPosition.BALL_HIGH;
                }
                int pov = aux.getPOV();
                if (pov == ButtonMap.BALL_CARGO) {
                    return ArmPosition.BALL_CARGO;
                }
            } else {
                if (aux.getRawButton(ButtonMap.STOW)) {
                    return ArmPosition.STOW;
                }
                if (aux.getRawButton(ButtonMap.HATCH_LOW)) {
                    return ArmPosition.HATCH_LOW;
                }
                if (aux.getRawButton(ButtonMap.HATCH_MEDIUM)) {
                    return ArmPosition.HATCH_MEDIUM;
                }
                if (aux.getRawButton(ButtonMap.HATCH_HIGH)) {
                    return ArmPosition.HATCH_HIGH;
                }
            }
            return ArmPosition.NO_CHANGE; //Nothing will be done
        } else {
            return ArmPosition.NO_MOVEMENT;
        }
    }

    /**
    * Determines the state of the ball intake button
    * @return true if we should be intaking a ball, false otherwise
    */
    public boolean getBallIntakeButton() {
        return aux.getPOV() == ButtonMap.BALL_INTAKE;
    }

    /**
     * Determines the state of the ball output button
     * @return true if we should be "spitting" a ball, false otherwise
     */
    public boolean getBallOutputButton() {
        return aux.getPOV() == ButtonMap.BALL_OUTPUT;
    }

    /**
     * Determines the state of the rainbow button 
     * @return true if we should be making the lights shine pretty colors, false otherwise
     */
    public boolean getRainbowButton() {
        return right.getRawButton(ButtonMap.RAINBOW_BUTTON);
    }

    /**
    * Returns whether the climb knife deploy button is being pressed.
    * @return true if we should deploy the knives, false otherwise
    */
    public boolean getClimbDeployButton() {
        return left.getRawButton(ButtonMap.CLIMB_DEPLOY);
    }

    /**
    * Determines if the auto-align button has been pressed
    * @return true if we should be auto-aligning, false otherwise.
    */
    public boolean getAutoAlignButton() {
        return right.getRawButton(ButtonMap.AUTO_ALIGN);
    }

    /**
     * Returns whether the hatch intake button is being pressed
     * @return true if we should be intaking a hatch, false otherwise.
     */
    public boolean getHatchIntakeButton() {
        return aux.getRawButton(ButtonMap.HATCH_INTAKE_BUTTON);
    }

    /**
     * Determines whether the hatch output button is being pressed.
     * @return true if we should be outputting a hatch, false otherwise.
     */
    public boolean getHatchOutputButton() {
        return aux.getRawButton(ButtonMap.HATCH_OUTPUT_BUTTON);
    }

    /**
     * Determines whether the Tip Override button is being pressed.
     * @return true if the tip code should be deactivated, false otherwise.
     */
    public boolean getTipOverrideButton() {
        //Driver request: bind Tip Override to same button as climb knife deploy.
        return getClimbDeployButton();
    }

    /**
     * Determines whether the Tip Correction Enable button is being pressed.
     * @return true if the tip code should be reactivated, false otherwise.
     */
    public boolean getTipEnableButton() {
        return right.getRawButton(ButtonMap.TIP_ENABLE);
    }

    /**
     * Returns the state of the drive safety button
     * @return true if the driver deems it safe to move the drivetrain, false otherwise.
     */
    public boolean getDriveSafetyButton() {
        return left.getRawButton(ButtonMap.DRIVE_SAFETY);
    }

    /**
     * Returns whether the gear shift button is being pressed
     * @return true if we should be switching gears, false otherwise
     */
    public boolean getGearShiftButton() {
        return right.getRawButtonPressed(ButtonMap.GEAR_SHIFT);
    }

    /**
     * Returns whether the arm safety button is being pressed
     * @return true if the operator deems it safe for the arm to move, false otherwise
     */
    public boolean getArmSafetyButton() {
        return aux.getRawButton(ButtonMap.AUX_SAFETY);
    }
    /**
     * Determines whether the shoulder should be in manual mode
     * @return true if the shoulder should be manually controlled, false otherwise
     */
    public boolean isShoulderManual(){
        return shoulderManual;
    }
    /**
     * Determines whether the wrist should be in manual mode
     * @return true if the wrist should be manually controlled, false otherwise
     */
    public boolean isWristManual(){
        return wristManual;
    }

}