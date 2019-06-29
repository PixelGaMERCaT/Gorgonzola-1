
package frc.components;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.PIDController;
import frc.CheeseLog.Loggable;
import frc.CheeseLog.SQLType.Bool;
import frc.CheeseLog.SQLType.Decimal;
import frc.CheeseLog.SQLType.Type;
import frc.robot.Constants;
import frc.robot.Globals;
import frc.robot.RobotMap;
import frc.talonmanager.DriveTalonManager;

/**
 * Responsible for managing the drivebase (treads) of the robot, along with the drive sensors.
 * @author Jeff
 */ 
public class Drivetrain implements Component {
    private DriveTalonManager frontLeft, frontRight, middleLeft, backLeft, backRight, middleRight;
    private InputManager inputManager;
    private Gyro gyro;
    private GearShifter shifter;
    private LogInterface logger;
    private NetworkInterface robotDataTable;
    public PIDController turnController;
    private TipCorrector tipCorrector;
    private CameraManager cameraManager;

    private boolean cameraSetpointSet = false; //Indicates whether the Camera's turn setpoint has been set yet for a given auto-align
    private boolean tankDrive = false; //Indicates whether the tank drive override has been engaged
    private double cameraYawSetpoint; //The setpoint, in degrees, that the robot should turn to to face a camera sighting.

    public Drivetrain() {
        frontLeft = new DriveTalonManager(RobotMap.FRONT_LEFT_TALON);
        frontRight = new DriveTalonManager(RobotMap.FRONT_RIGHT_TALON);
        backLeft = new DriveTalonManager(RobotMap.BACK_LEFT_TALON);
        backRight = new DriveTalonManager(RobotMap.BACK_RIGHT_TALON);
        if (!Globals.isNSP) {
            middleLeft = new DriveTalonManager(RobotMap.MIDDLE_LEFT_TALON);
            middleRight = new DriveTalonManager(RobotMap.MIDDLE_RIGHT_TALON);
            middleRight.setInverted(true);
            middleLeft.follow(frontLeft);
            middleRight.follow(frontRight);
        }
        backRight.setInverted(true);
        frontRight.setInverted(true);

        backLeft.follow(frontLeft);
        backRight.follow(frontRight);
        frontLeft.initEncoder(0, 0, 0, 0);
        frontRight.initEncoder(0, 0, 0, 0);
        resetEncoders();

    }

    public void init() {
        shifter = Globals.gearShifter;
        cameraManager = Globals.cameraManager;
        tipCorrector = Globals.tipCorrector;
        inputManager = Globals.inputManager;
        logger = Globals.logger;
        robotDataTable = Globals.robotDataTable;
        gyro = Globals.gyro;
        try {
            logger.drivetrain = LogInterface.createTable("Drivetrain",
                    new String[] { "disLeft", "disRight", "leftPower", "rightPower", "velRight", "velLeft",
                            "PercentLeft", "PercentRight" },
                    new Type[] { new Decimal(), new Decimal(), new Decimal(), new Decimal(), new Decimal(),
                            new Decimal(), new Decimal(), new Decimal() },
                    new Loggable[] { () -> frontLeft.getEncoderPositionContextual(),
                            () -> frontRight.getEncoderPositionContextual(), () -> frontLeft.getOutputCurrent(),
                            () -> frontRight.getOutputCurrent(), () -> frontRight.getEncoderVelocityContextual(),
                            () -> frontLeft.getEncoderVelocityContextual(), () -> frontLeft.getMotorOutputPercent(),
                            () -> frontRight.getMotorOutputPercent() });

            logger.turnController = LogInterface.createTable("Turn_Controller",
                    new String[] { "angle", "output", "setpoint", "enabled" },
                    new Type[] { new Decimal(), new Decimal(), new Decimal(), new Bool() },
                    new Loggable[] { () -> gyro.getYaw(), () -> turnController.get(),
                            () -> turnController.getSetpoint(), () -> turnController.isEnabled() });
        } catch (Exception e) {
            e.printStackTrace();
        }
        turnController = new PIDController(Constants.TURN_KP, Constants.TURN_KI, Constants.TURN_KD, gyro, o -> {
        });
        turnController.setAbsoluteTolerance(0);
        turnController.setInputRange(-180, 180);
        turnController.setOutputRange(-1, 1);
        turnController.setContinuous(true);
    }

    /**
     * Resets the drivetrain's encoders
     */
    public void resetEncoders() {
        frontLeft.resetEncoder();
        frontRight.resetEncoder();
    }

    /**
     * Sets the setpoint for the Turn PID controller.
     * @param angle the setpoint, in degrees
     */
    public void setTurnSetpoint(double angle) {
        turnController.setSetpoint(angle);
        turnController.enable();
    }

    public void tick() {

        if (!tipCorrector.isCorrecting()) {
            if (!inputManager.getAutoAlignButton()) {
                //Normal Conditions:
                cameraSetpointSet = false;
                if (tankDrive) {
                    //Tank Drive Override:
                    tankDrive(inputManager.getPrimaryJoyY(), inputManager.getSecondaryJoyY());
                } else {
                    //Normal Drive (default):
                    driveBasic(inputManager.getPrimaryJoyY(), inputManager.getSecondaryJoyX());
                }
            } else if (inputManager.getDriveSafetyButton()) {
                //Auto-Align to Target:
                try {
                    if (!cameraSetpointSet) {
                        //Set yawSetpoint to the absolute angle of the sighting
                        cameraSetpointSet = true;
                        cameraYawSetpoint = cameraManager.getPrimarySightingAngleAbsolute();
                    }
                    //Turn to yawSetpoint (with manual adjustment)
                    setTurnSetpoint(cameraYawSetpoint - 10.0 * inputManager.getSecondaryJoyX());
                    shifter.setGear(false);
                    driveBasic(inputManager.getPrimaryJoyY(), turnController.get());
                } catch (Exception e) {
                    System.out.println("Can't Get Sighting angle.");
                    cameraSetpointSet = false;
                    driveBasic(inputManager.getPrimaryJoyY(), inputManager.getSecondaryJoyX());
                }
            }

            //Set Tank Drive override:
            if (inputManager.getTankDriveOverride()) {
                tankDrive = !tankDrive;
                robotDataTable.setBoolean("tankDrive", true);
            } else {
                robotDataTable.setBoolean("tankDrive", false);
            }
        }
    }

    /**
     * Drives the two sides of the robot independently.
     * @param left the amount of power (-1 being full forward, 1 being full backward) to apply to the left side of the robot
     * @param right the amount of power (-1 being full forward, 1 being full backward) to apply to the right side of the robot
     */
    private void tankDrive(double left, double right) {
        frontLeft.set(ControlMode.PercentOutput, left);
        frontRight.set(ControlMode.PercentOutput, right);
    }

    /**
     * A basic percent-based drive method. Moves the treads of the robot. 
     * Squares the input values to provide smoother driving
     * @param forward A number between -1 (full forward) and 1 (full backward)
     * @param turn    A number between -1 (full right) and 1 (full left)
     */
    public void driveBasic(double forward, double turn) {
        forward = forward * forward * Math.signum(forward);
        turn = turn * turn * Math.signum(turn);
        frontLeft.set(ControlMode.PercentOutput, forward - turn);
        frontRight.set(ControlMode.PercentOutput, forward + turn);
    }

    /**
     * A drive method designed for auto; the same as driveBasic but without squaring
     * @param forward A number between -1 (full backward) and 1 (full forward)
     * @param turn    A number between -1 (full right) and 1 (full left)
     */
    public void autoDrive(double forward, double turn) {
        frontLeft.set(ControlMode.PercentOutput, forward - turn);
        frontRight.set(ControlMode.PercentOutput, forward + turn);
    }

    /**
     * Drives the robot foward based on Motion profiling output
     * @param left the power to apply to the left side of the robot
     * @param right the power to apply to the right side of the robot
     * @param turn a correction factor based on the output of a turnPID
     */
    public void driveMP(double left, double right, double turn) {
        frontLeft.set(ControlMode.PercentOutput, left - turn);
        frontRight.set(ControlMode.PercentOutput, right + turn);
    }

    /**
     * Returns the position of the left encoder in native units
     */
    public int getLeftPosition() {
        return frontLeft.getEncoderPosition();
    }

    /**
     * returns the position of the right encoder in native units
     */
    public int getRightPosition() {
        return frontRight.getEncoderPosition();
    }

    /**
    * Returns the position of the left encoder in inches travelled
    */
    public double getLeftPositionInches() {
        return frontLeft.getEncoderPositionContextual();
    }

    /**
     * returns the position of the right encoder in inches travelled
     */
    public double getRightPositionInches() {
        return frontRight.getEncoderPositionContextual();
    }

    /**
     * Returns the velocity of the left encoder in native units per 100ms
     */
    public int getLeftVelocity() {
        return frontLeft.getEncoderVelocity();
    }

    /**
     * returns the velocity of the right encoder in native units per 100ms
     */
    public int getRightVelocity() {
        return frontRight.getEncoderVelocity();
    }

}