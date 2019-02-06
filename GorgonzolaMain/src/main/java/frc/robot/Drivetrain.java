
package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Solenoid;
import frc.CheeseLog.Loggable;
import frc.CheeseLog.SQLType.Bool;
import frc.CheeseLog.SQLType.Decimal;
import frc.CheeseLog.SQLType.Int;
import frc.CheeseLog.SQLType.Type;

public class Drivetrain implements Component {
    public TalonManager frontLeft, frontRight, middleLeft, backLeft, backRight, middleRight;
    private InputManager im;
    private Compressor compressor;
    private Solenoid switcher;
    public double currentLeftPosition, currentRightPosition;
    private Gyro gyro;

    private LogInterface logger;
    public double setpointLeft, setpointRight;
    public PIDController turnController;

    /**
     * The Default constructor for Drivetrain, sets up basic movement and sensor
     * functionality
     */
    public Drivetrain() {
        frontLeft = new TalonManager(RobotMap.FRONT_LEFT_TALON);
        frontRight = new TalonManager(RobotMap.FRONT_RIGHT_TALON);
        backLeft = new TalonManager(RobotMap.BACK_LEFT_TALON);
        backRight = new TalonManager(RobotMap.BACK_RIGHT_TALON);
        if (!Globals.isNSP) {
            middleLeft = new TalonManager(RobotMap.MIDDLE_LEFT_TALON);
            middleRight = new TalonManager(RobotMap.MIDDLE_RIGHT_TALON);
            middleRight.setInverted(true);
            middleLeft.follow(frontLeft);
            middleRight.follow(frontRight);
            compressor = new Compressor(RobotMap.COMPRESSOR);
            compressor.setClosedLoopControl(true);
            switcher = new Solenoid(0);

        }
        if (!Globals.isAdelost){
        backRight.setInverted(true);
        frontRight.setInverted(true);
        }
        
        backLeft.follow(frontLeft);
        backRight.follow(frontRight);
        frontLeft.initEncoders(0, 0, 0, 1.1);
        frontRight.initEncoders(0, 0, 0, 1.1);
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
                    new String[] { "encoderLeft", "encoderRight", "setpointLeft", "setpointRight" },
                    new Type[] { new Int(), new Int(), new Decimal(), new Decimal() },
                    new Loggable[] { () -> frontLeft.getEncoderPosition(), () -> frontRight.getEncoderPosition(),
                            () -> setpointLeft, () -> setpointRight });

            logger.drivetrain = LogInterface.table("Drivetrain",
                    new String[] { "encoderLeft", "encoderRight", "leftPower", "rightPower" },
                    new Type[] { new Int(), new Int(), new Decimal(), new Decimal() },
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
        this.setYawSetpoint(90.0);
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

    int maxVelocity = 0;

    public void tick() {

        driveBasic(im.getForward(), im.getTurn());
        //driveBasic(im.getForward(), turnController.get()* (im.getSafetyButton() ? 1 : 0));

        /*if (Math.abs(maxVelocity)-Math.abs(frontRight.getEncoderVelocity())<0){
            System.out.println("right velocity: "+ frontRight.getEncoderVelocity());
            System.out.println("left velocity: "+ frontLeft.getEncoderVelocity());
            maxVelocity=frontRight.getEncoderVelocity();
        }*/
        //System.out.println("right distance "+ frontRight.getEncoderPosition());
        if (!(Globals.isNSP || Globals.isAdelost)) {
            switcher.set(im.getGearSwitchButton());
        }
    }

    /**
     * A basic percent-based drive method. Moves the treads of the robot
     * @param forward A number between -1 (full backward) and 1 (full forward)
     * @param turn    A number between -1 (full right) and 1 (full left)
     */
    public void driveBasic(double forward, double turn) {
        forward = forward * forward * Math.signum(forward);
        turn = turn * turn * Math.signum(turn);
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

        forward = forward * forward * Math.signum(forward);
        forward = Math.max(Math.min(1, forward), -1);
        turn = turn * turn * Math.signum(turn); //TODO Experiment with not minning, try something with a drive ratio
        turn = Math.max(Math.min(1, turn), -1);
        //TODO Check for problems with driving forward close to max and trying to turn; try separate turn distance
        currentLeftPosition = frontLeft.getEncoderPosition();
        currentRightPosition = frontRight.getEncoderPosition();

        setpointLeft += (forward * Constants.MAX_DRIVE_VELOCITY + turn * Constants.MAX_TURN_VELOCITY);
        setpointRight += (forward * Constants.MAX_DRIVE_VELOCITY - turn * Constants.MAX_TURN_VELOCITY);
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