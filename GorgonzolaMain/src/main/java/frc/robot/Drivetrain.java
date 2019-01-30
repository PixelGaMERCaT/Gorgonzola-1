
package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drivetrain implements Component {
    public TalonSRX frontLeft, frontRight, backLeft, backRight;
    private InputManager im;
    public double currentLeftPosition, currentRightPosition;
    private Gyro gyro;
    public double setPointLeft, setPointRight; 
    private PIDController turnController;
    
    /**
     * The Default constructor for Drivetrain, sets up basic movement and sensor
     * functionality
     */
    public Drivetrain() {
        
        im = Globals.im;
        frontLeft = new TalonSRX(RobotMap.FRONT_LEFT_TALON);
        frontRight = new TalonSRX(RobotMap.FRONT_RIGHT_TALON);
        backLeft = new TalonSRX(RobotMap.BACK_LEFT_TALON);
        backRight = new TalonSRX(RobotMap.BACK_RIGHT_TALON);
        if (Globals.isAdelost){

        } else  {
            backRight.setInverted(true);
            frontRight.setInverted(true);
        }
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
        frontLeft.config_kP(0, 1.1);
        frontLeft.config_kI(0, 0);
        frontLeft.config_kD(0, 0);
        frontLeft.config_kF(0, .3);
        frontRight.config_kP(0, 1.1);
        frontRight.config_kI(0, 0);
        frontRight.config_kD(0, 0);
        frontRight.config_kF(0, .3);
        resetEncoders();
        // TODO Acceleration and cruise velocity
    }

    public void init() {
        gyro = Globals.gyro;
        currentLeftPosition = frontLeft.getSelectedSensorPosition(0);
        currentRightPosition = frontRight.getSelectedSensorPosition(0);
        turnController = new PIDController(Constants.TURN_KP, Constants.TURN_KI, Constants.TURN_KD, gyro, o -> {
        });
        turnController.setAbsoluteTolerance(1);
        turnController.setInputRange(-180, 180);
        turnController.setOutputRange(-1, 1);
        turnController.setContinuous(true);
        try {
            Globals.logger.logger.addStatement("Drivetrain", "encoderLeft", frc.CheeseLog.Type.INT, () -> {
                return frontLeft.getSelectedSensorPosition(0);
            }, true, false, false);
            Globals.logger.logger.addStatement("Drivetrain", "encoderRight", frc.CheeseLog.Type.INT, () -> {
                return frontRight.getSelectedSensorPosition(0);
            }, true, false, false);
            Globals.logger.logger.addStatement("Drivetrain", "setPointLeft", frc.CheeseLog.Type.DECIMAL, () -> {
                return setPointLeft;
            }, true, false, false);
            Globals.logger.logger.addStatement("Drivetrain", "setPointRight", frc.CheeseLog.Type.DECIMAL, () -> {
                return setPointRight;
            }, true, false, false);
            Globals.logger.logger.addStatement("TurnController", "Angle", frc.CheeseLog.Type.DECIMAL,
                    () -> gyro.getNormalizedYaw(), true, false, false);
            Globals.logger.logger.addStatement("TurnController", "PIDOut", frc.CheeseLog.Type.DECIMAL,
                    () -> turnController.get(), true, false, false);
            Globals.logger.logger.addStatement("TurnController", "Setpoint", frc.CheeseLog.Type.DECIMAL,
                    () -> turnController.getSetpoint(), true, false, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //this.setAngleSetpoint(90.0);
    }

    public void resetEncoders() {
        frontLeft.setSelectedSensorPosition(0, 0, 0);
        frontRight.setSelectedSensorPosition(0, 0, 0);
        setPointLeft = 0;
        setPointRight = 0;
    }
    public void setAngleSetpoint(double angle){
        turnController.setSetpoint(angle);
        turnController.enable();
    }
    public void tick() {
        //magicDrive(im.getForward(), im.getTurn());
        System.out.println(turnController.get());
        // driveBasic(im.getForward(), turnController.get()* (im.getSafetyButton() ? 1 : 0));
        driveBasic(im.getForward(), im.getTurn());
    }

    /**
     * A basic percent-based drive method. Moves the treads of the robot
     * 
     * @param forward A number between -1 (full backward) and 1 (full forward)
     * @param turn    A number between -1 (full right) and 1 (full left)
     */
    public void driveBasic(double forward, double turn) {
        forward = forward * forward * Math.signum(forward);
        turn = turn * turn * Math.signum(turn);
        //System.out.println("Positions:\n" + "left" + frontLeft.getSelectedSensorPosition(0) + "\nright"
        //        + frontRight.getSelectedSensorPosition(0));

        frontLeft.set(ControlMode.PercentOutput, forward + turn);
        frontRight.set(ControlMode.PercentOutput, forward - turn);
    }

    public void autoDrive(double forward, double turn) {
        frontLeft.set(ControlMode.PercentOutput, forward + turn);
        frontRight.set(ControlMode.PercentOutput, forward - turn);
        System.out.println("Drive Method: " + forward + ", " + turn);
    }

    /**
     * A Magic-Drive based movement option. Works similarly to driveBasic, but
     * resists unwanted change in motion.
     * 
     * @param forward A number between -1 (full backward) and 1 (full forward)
     * @param turn    A number between -1 (full right) and 1 (full left)
     */

    public void magicDrive(double forward, double turn) {

        /*forward = forward * forward * Math.signum(forward);
        forward = Math.max(Math.min(1, forward), -1);
        turn = turn * turn * Math.signum(turn); //TODO Experiment with not minning, try something with a drive ratio
        turn = Math.max(Math.min(1, turn), -1);
        //TODO Check for problems with driving forward close to max and trying to turn; try separate turn distance
        currentLeftPosition = frontLeft.getSelectedSensorPosition(0);
        currentRightPosition = frontRight.getSelectedSensorPosition(0);

        setPointLeft += (forward * Constants.MAX_DRIVE_VELOCITY + turn * Constants.MAX_TURN_VELOCITY);
        setPointRight += (forward * Constants.MAX_DRIVE_VELOCITY - turn * Constants.MAX_TURN_VELOCITY);

        if (Math.abs(setPointLeft - currentLeftPosition) > 4096 * Constants.MAGIC_DRIVE_MAX_ROTATIONS) {
            setPointLeft = Math.signum(setPointLeft - currentLeftPosition) * 4096 * Constants.MAGIC_DRIVE_MAX_ROTATIONS
                    + currentLeftPosition;
        }

        if (Math.abs(setPointRight - currentRightPosition) > 4096 * Constants.MAGIC_DRIVE_MAX_ROTATIONS) {
            setPointRight = Math.signum(setPointRight - currentRightPosition) * 4096
                    * Constants.MAGIC_DRIVE_MAX_ROTATIONS + currentRightPosition;
        }
        frontLeft.set(ControlMode.MotionMagic, setPointLeft);
        frontRight.set(ControlMode.MotionMagic, setPointRight);
        */
        frontLeft.set(ControlMode.MotionMagic, 40960);
        frontRight.set(ControlMode.MotionMagic, 40960);
        SmartDashboard.putNumber("setPoint", setPointLeft);
        SmartDashboard.putNumber("currentPosition", frontLeft.getSelectedSensorPosition(0));

    }

}