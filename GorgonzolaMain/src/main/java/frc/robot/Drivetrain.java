
package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Drivetrain implements Component {

    private TalonSRX frontLeft, frontRight, backLeft, backRight;
    private InputManager im;
    private double currentLeftPosition = 0, currentRightPosition = 0;

    /**
     * The Default constructor for Drivetrain, sets up basic movement and sensor functionality
     */
    public Drivetrain() {
        im = Globals.im;
        frontLeft = new TalonSRX(RobotMap.FRONT_LEFT_TALON);
        frontRight = new TalonSRX(RobotMap.FRONT_RIGHT_TALON);
        backLeft = new TalonSRX(RobotMap.BACK_LEFT_TALON);
        backRight = new TalonSRX(RobotMap.BACK_RIGHT_TALON);
        backRight.setInverted(true);
        frontRight.setInverted(true);
        backLeft.follow(frontLeft);
        backRight.follow(frontRight);
        frontLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        frontRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        frontLeft.configNominalOutputForward(0);
        frontLeft.configNominalOutputReverse(0);
        frontLeft.configPeakOutputForward(1);
        frontLeft.configPeakOutputReverse(-1);
        frontLeft.configNominalOutputForward(0);
        frontLeft.configNominalOutputReverse(0);
        frontLeft.configPeakOutputForward(1);
        frontLeft.configPeakOutputReverse(-1);

        frontRight.configNominalOutputForward(0);
        frontRight.configNominalOutputReverse(0);
        frontRight.configPeakOutputForward(1);
        frontRight.configPeakOutputReverse(-1);
        frontRight.configNominalOutputForward(0);
        frontRight.configNominalOutputReverse(0);
        frontRight.configPeakOutputForward(1);
        frontRight.configPeakOutputReverse(-1);
        // TODO Acceleration and cruise velocity
        frontLeft.setSelectedSensorPosition(0);
        frontRight.setSelectedSensorPosition(0);
    }

    public void tick() {

        // driveBasic(im.getForward(), im.getTurn());
        magicDrive(im.getForward(), im.getTurn());
    }

    /**
     * A basic percent-based drive method. Moves the treads of the robot
     * 
     * @param forward A number between -1 (full backward) and 1 (full forward)
     * @param turn    A number between -1 (full right) and 1 (full left)
     */
    public void driveBasic(double forward, double turn) {
        frontLeft.set(ControlMode.PercentOutput, forward + turn);
        frontRight.set(ControlMode.PercentOutput, forward - turn);
    }

    /**
     * A Magic-Drive based movement option. Works similarly to driveBasic, but
     * resists unwanted change in motion.
     * 
     * @param forward A number between -1 (full backward) and 1 (full forward)
     * @param turn    A number between -1 (full right) and 1 (full left)
     */
    public void magicDrive(double forward, double turn) {
        if (Math.abs(forward) > .001 || Math.abs(turn) > .001) {
            currentLeftPosition = frontLeft.getSelectedSensorPosition(0);
            currentRightPosition = frontRight.getSelectedSensorPosition(0);
        }
        System.out.println(forward - turn);
        // TODO test running something like Math.max(Math.min(forward+turn, 1), -1) for
        // input to cap values

        // 4096 encoder units per turn, MAGIC_DRIVE_ROTATIONS turns forward as a target
        // from the joysticks at full power
        frontLeft.set(ControlMode.MotionMagic,
                currentLeftPosition + (forward + turn) * 4096 * Constants.MAGIC_DRIVE_ROTATIONS);
        frontRight.set(ControlMode.MotionMagic,
                currentRightPosition + (forward - turn) * 4096 * Constants.MAGIC_DRIVE_ROTATIONS);
    }

}