
package frc.components;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.CheeseLog.Loggable;
import frc.CheeseLog.SQLType.Bool;
import frc.CheeseLog.SQLType.Decimal;
import frc.CheeseLog.SQLType.Type;
import frc.robot.Constants;
import frc.robot.Globals;
import frc.robot.RobotMap;
import frc.talonmanager.DriveTalonManager;

public class Drivetrain implements Component {
    public DriveTalonManager frontLeft, frontRight, middleLeft, backLeft, backRight, middleRight;
    private InputManager inputManager;

    public double currentLeftPosition, currentRightPosition;
    private Gyro gyro;
    private GearShifter shifter;
    private LogInterface logger;
    private NetworkInterface robotDataTable;
    public double setpointLeft, setpointRight;
    public PIDController turnController;
    private TipCorrector tipCorrector;
    private CameraManager cameraManager;

    /**
     * The Default constructor for Drivetrain, sets up basic movement and sensor
     * functionality
     */
    public Drivetrain() {
        frontLeft = new DriveTalonManager(RobotMap.FRONT_LEFT_TALON);
        frontRight = new DriveTalonManager(RobotMap.FRONT_RIGHT_TALON);
        backLeft = new DriveTalonManager(RobotMap.BACK_LEFT_TALON);
        backRight = new DriveTalonManager(RobotMap.BACK_RIGHT_TALON);
        if (!(Globals.isNSP || Globals.isAdelost)) {
            middleLeft = new DriveTalonManager(RobotMap.MIDDLE_LEFT_TALON);
            middleRight = new DriveTalonManager(RobotMap.MIDDLE_RIGHT_TALON);
            middleRight.setInverted(true);
            middleLeft.follow(frontLeft);
            middleRight.follow(frontRight);

        }
        if (!Globals.isAdelost) {
            backRight.setInverted(true);
            frontRight.setInverted(true);
        }

        backLeft.follow(frontLeft);
        backRight.follow(frontRight);
        frontLeft.initEncoder(Constants.MAGIC_KP_LOW, Constants.MAGIC_KI_LOW, Constants.MAGIC_KD_LOW,
                -Constants.MAGIC_KF_LOW);
        frontRight.initEncoder(Constants.MAGIC_KP_LOW, Constants.MAGIC_KI_LOW, Constants.MAGIC_KD_LOW,
                -Constants.MAGIC_KF_LOW);
        resetEncoders();

    }

    public void init() {
        SmartDashboard.putNumber("TurnP", Constants.TURN_KP);
        SmartDashboard.putNumber("TurnD", Constants.TURN_KD);
        shifter = Globals.gearShifter;
        cameraManager = Globals.cameraManager;
        tipCorrector = Globals.tipCorrector;
        inputManager = Globals.inputManager;
        logger = Globals.logger;
        robotDataTable=Globals.robotDataTable;
        gyro = Globals.gyro;
        currentLeftPosition = frontLeft.getEncoderPosition();
        currentRightPosition = frontRight.getEncoderPosition();
        try {
            logger.magicDrive = LogInterface.manualTable("Magic_Drive",
                    new String[] { "encoderLeft", "encoderRight", "setpointLeft", "setpointRight", "currentLeft",
                            "currentRight" },
                    new Type[] { new Decimal(), new Decimal(), new Decimal(), new Decimal(), new Decimal(),
                            new Decimal() },
                    new Loggable[] { () -> frontLeft.getEncoderPosition(), () -> frontRight.getEncoderPosition(),
                            () -> setpointLeft, () -> setpointRight, () -> frontLeft.getOutputCurrent(),
                            () -> frontRight.getOutputCurrent() });
            //frontLeft.talon.getMotorOutputVoltage();

            logger.drivetrain = LogInterface.table("Drivetrain",
                    new String[] { "disLeft", "disRight", "leftPower", "rightPower", "velRight", "velLeft",
                            "voltLeft", "voltRight" },
                    new Type[] { new Decimal(), new Decimal(), new Decimal(), new Decimal(), new Decimal(),
                            new Decimal(), new Decimal(), new Decimal() },
                    new Loggable[] { () -> frontLeft.getEncoderPositionContextual(), () -> frontRight.getEncoderPositionContextual(),
                            () -> frontLeft.getOutputCurrent(), () -> frontRight.getOutputCurrent(),
                            () -> frontRight.getEncoderVelocityContextual(),
                            () -> frontLeft.getEncoderVelocityContextual(),
                            () -> frontLeft.talon.getMotorOutputVoltage(),
                            () -> frontRight.talon.getMotorOutputVoltage() });

            logger.turnController = LogInterface.table("Turn_Controller",
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
        setpointLeft = 0;
        setpointRight = 0;
    }

    /**
     * Sets the setpoint for the Turn PID controller.
     * @param angle the setpoint
     */
    public void setYawSetpoint(double angle) {
        turnController.setSetpoint(angle);
        turnController.enable();
    }

    double maxVelocity = 0;
    double imForward = 0;
    double tick = 0;
    boolean setPointSet = false;
    boolean tankDrive = false;
    boolean tankDriveSet = false;
    double setpoint;
    public void tick() {
        SmartDashboard.putNumber("turnout", turnController.get());
        maxVelocity = Math.max(maxVelocity, Math.abs(frontLeft.getEncoderVelocity()));
        //System.out.println("PositionR "+frontRight.getEncoderPositionContextual());
        //System.out.println("PositionL "+frontLeft.getEncoderPositionContextual());
        //SmartDashboard.putNumber("maxvelDrive", maxVelocity);
        SmartDashboard.putNumber("navx ", gyro.getNormalizedYaw());

        try {
            SmartDashboard.putNumber("camera1Angle", cameraManager.getPrimarySightingAnglePose());
        } catch (Exception e) {
            //e.printStackTrace();

        }
        if (!tipCorrector.isCorrecting()) {
            if (!inputManager.getCameraEnable()) {
                setPointSet = false;
                if (tankDrive) {
                    tankDrive(inputManager.getForward(), inputManager.getRightForward());
                } else {
                    driveBasic(inputManager.getForward(), inputManager.getTurn());
                }
            } else if (inputManager.getDriveSafetyButton()) {
                try {
                    turnController.setP(SmartDashboard.getNumber("TurnP", Constants.TURN_KP));
                    turnController.setD(SmartDashboard.getNumber("TurnD", Constants.TURN_KD));
                    //System.out.println("d"+turnController.getD()+ "P"+turnController.getP());
                    if (!setPointSet) {
                        setPointSet = true;
                        setYawSetpoint(cameraManager.getPrimarySightingAnglePose());
                        setpoint=turnController.getSetpoint();
                        System.out.println(cameraManager.getPrimarySightingAnglePose() + " " + gyro.getNormalizedYaw());
                    } else {
                        turnController.setSetpoint(setpoint-10.0*inputManager.getTurn());
                    }
                    shifter.highGear = false;
                    driveBasic(inputManager.getForward(), turnController.get());
                } catch (Exception e) {
                    System.out.println("Can't Get Sighting angle.");
                    setYawSetpoint(gyro.getNormalizedYaw());
                    System.out.println("setting setpoint"+ turnController.getSetpoint());
                    setpoint=turnController.getSetpoint();
                    setPointSet=true;
                    //e.printStackTrace();
                }
            }
            if (inputManager.getTankDriveOverride() && !tankDriveSet) {
                tankDrive = !tankDrive;
                robotDataTable.setBoolean("tankDrive", true);
                tankDriveSet = true;
            } else if (!inputManager.getTankDriveOverride()) {
                robotDataTable.setBoolean("tankDrive", false);
                tankDriveSet = false;
            }
        }
        //SmartDashboard.putNumber("leftEnc", frontLeft.getEncoderPositionContextual());
        //SmartDashboard.putNumber("rightEnc", frontRight.getEncoderPositionContextual());
        //SmartDashboard.putNumber("dis", SmartDashboard.getNumber("final distance no assist", -108699)
        //        + frontLeft.getEncoderPositionContextual());
    }

    private void tankDrive(double left, double right) {
        frontLeft.set(ControlMode.PercentOutput, left);
        frontRight.set(ControlMode.PercentOutput, right);
    }

    /**
     * A basic percent-based drive method. Moves the treads of the robot
     * @param forward A number between -1 (full backward) and 1 (full forward)
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
     * A Motion Magic-based movement option. Works similarly to driveBasic, but
     * resists unwanted change in motion.
     * 
     * @param forward A number between -1 (full backward) and 1 (full forward)
     * @param turn    A number between -1 (full right) and 1 (full left)
     */

    public void magicDrive(double forward, double turn) {

        /*forward = forward * forward * Math.signum(forward);
        turn = turn * turn * Math.signum(turn); //TODO Experiment with not minning, try something with a drive ratio
        *///TODO Check for problems with driving forward close to max and trying to turn; try separate turn distance
        currentLeftPosition = frontLeft.getEncoderPosition();
        currentRightPosition = frontRight.getEncoderPosition();
        turn = 0; //TODO REMOVE
        setpointLeft += (forward * Constants.MAX_DRIVE_VELOCITY_LOW / 50.0 * Constants.DRIVE_ENCU_PER_INCH
                - turn * Constants.MAX_TURN_VELOCITY_LOW);
        setpointRight += (forward * Constants.MAX_DRIVE_VELOCITY_LOW / 50.0 * Constants.DRIVE_ENCU_PER_INCH
                + turn * Constants.MAX_TURN_VELOCITY_LOW);
        System.out.println("Forward" + (forward * Constants.MAX_DRIVE_VELOCITY_LOW * Constants.DRIVE_ENCU_PER_INCH));
        System.out.println("turn" + turn);
        /*
        if (Math.abs(setPointLeft - currentLeftPosition) > 4096 * Constants.MAGIC_DRIVE_MAX_ROTATIONS) {
        setPointLeft = Math.signum(setPointLeft - currentLeftPosition) * 4096 * Constants.MAGIC_DRIVE_MAX_ROTATIONS
        + currentLeftPosition;
        }
        
        if (Math.abs(setPointRight - currentRightPosition) > 4096 * Constants.MAGIC_DRIVE_MAX_ROTATIONS) {
        setPointRight = Math.signum(setPointRight - currentRightPosition) * 4096
        * Constants.MAGIC_DRIVE_MAX_ROTATIONS + currentRightPosition;
        }*/

        frontLeft.set(ControlMode.MotionMagic, setpointLeft);
        frontRight.set(ControlMode.MotionMagic, setpointRight);
        logger.magicDrive.log(logger.getTick());
    }

    /**
     * Returns the position of the left encoder
     */
    public int getLeftPosition() {
        return frontLeft.getEncoderPosition();
    }

    /**
     * returns the position of the right encoder
     */
    public int getRightPosition() {
        return frontRight.getEncoderPosition();
    }

    /**
    * Returns the position of the left encoder
    */
    public double getLeftPositionInches() {
        return frontLeft.getEncoderPositionContextual();
    }

    /**
     * returns the position of the right encoder
     */
    public double getRightPositionInches() {
        return frontRight.getEncoderPositionContextual();
    }

    /**
     * Returns the velocity of the left encoder
     */
    public int getLeftVelocity() {
        return frontLeft.getEncoderVelocity();
    }

    /**
     * returns the velocity of the right encoder
     */
    public int getRightVelocity() {
        return frontRight.getEncoderVelocity();
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
}