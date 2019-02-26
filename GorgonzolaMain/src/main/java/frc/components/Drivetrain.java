
package frc.components;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PIDController;
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
    private InputManager im;
    private Compressor compressor;
    public double currentLeftPosition, currentRightPosition;
    private Gyro gyro;
    private GearShifter shifter;
    private LogInterface logger;
    public double setpointLeft, setpointRight;
    public PIDController turnController;

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
        im = Globals.im;
        logger = Globals.logger;
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

            logger.drivetrain = LogInterface.table("Drivetrain",
                    new String[] { "encoderLeft", "encoderRight", "leftPower", "rightPower" },
                    new Type[] { new Decimal(), new Decimal(), new Decimal(), new Decimal() },
                    new Loggable[] { () -> frontLeft.getEncoderPosition(), () -> frontRight.getEncoderPosition(),
                            () -> frontLeft.getOutputCurrent(), () -> frontRight.getOutputCurrent() });

            logger.turnController = LogInterface.manualTable("Turn_Controller",
                    new String[] { "angle", "output", "setpoint", "enabled" },
                    new Type[] { new Decimal(), new Decimal(), new Decimal(), new Bool() },
                    new Loggable[] { () -> gyro.getYaw(), () -> turnController.get(),
                            () -> turnController.getSetpoint(), () -> turnController.isEnabled() });
            //TODO use turncontroller table
        } catch (Exception e) {
            e.printStackTrace();
        }
        turnController = new PIDController(Constants.TURN_KP, Constants.TURN_KI, Constants.TURN_KD, gyro, o -> {
        });
        turnController.setAbsoluteTolerance(1);
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

    public void tick() {
        maxVelocity = Math.max(maxVelocity, Math.abs(frontLeft.getEncoderVelocity()));
        //System.out.println("PositionR "+frontRight.getEncoderPositionContextual());
        //System.out.println("PositionL "+frontLeft.getEncoderPositionContextual());
        //System.out.println(maxVelocity);
        
        driveBasic(im.getForward(), im.getTurn());
                
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