package frc.components;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.CheeseLog.Loggable;
import frc.CheeseLog.SQLType.Decimal;
import frc.CheeseLog.SQLType.Type;
import frc.robot.Constants;
import frc.robot.FieldConstants;
import frc.robot.Globals;
import frc.robot.RobotMap;
import frc.talonmanager.ShoulderTalonManager;

public class Shoulder implements Component {
    public double height, armlength;
    private ShoulderTalonManager talon1, talon2;
    private double currentPosition;
    private LogInterface logger;
    private InputManager im;
    private double heightSetpoint;
    public ArmHeight desiredPos;

    public Shoulder() {
        desiredPos = ArmHeight.NO_MOVEMENT;
        talon1 = new ShoulderTalonManager(RobotMap.SHOULDER_TALON_1);
        talon2 = new ShoulderTalonManager(RobotMap.SHOULDER_TALON_2);
        heightSetpoint = Constants.SHOULDER_MIN_POSITION;
        talon1.initEncoder(Constants.SHOULDER_KP, Constants.SHOULDER_KI, Constants.SHOULDER_KD, Constants.SHOULDER_KF);
        if (Globals.isProto) {

            talon1.talon.setSensorPhase(false);
            talon2.talon.setSensorPhase(false);
            talon1.setInverted(true);
            talon2.setInverted(true);
        } else {
            talon1.talon.setSensorPhase(true);
            talon2.talon.setSensorPhase(true);

        }
        talon1.talon.configFeedbackNotContinuous(true, 0);
        talon2.talon.configFeedbackNotContinuous(true, 0);

        talon2.follow(talon1);
        currentPosition = getHeight();
    }

    public void init() {

        im = Globals.im;
        logger = Globals.logger;
        logger.shoulder = LogInterface.table("Shoulder",
                new String[] { "ShoulderVelocity", "ShoulderAngle", "ShoulderDesHeight", "Shoulderpercentout",
                        "Shoulderdifference", "Setpoint", "Shoulderencu" },
                new Type[] { new Decimal(), new Decimal(), new Decimal(), new Decimal(), new Decimal(), new Decimal(),
                        new Decimal() },
                new Loggable[] { () -> talon1.getEncoderVelocity(), () -> getHeight(), () -> heightSetpoint,
                        () -> talon1.talon.getMotorOutputPercent(), () -> talon1.talon.getClosedLoopError(),
                        () -> talon1.talon.getClosedLoopTarget(), () -> talon1.getEncoderPosition() });

    }

    double maxVelocity = 0;

    public void tick() {

        /*if (im.getAuxButton(11)) {
            talon1.initEncoder(SmartDashboard.getNumber("WristP", Constants.WRIST_KP),
                    SmartDashboard.getNumber("WristI", 0), SmartDashboard.getNumber("WristD", Constants.WRIST_KD),
                    SmartDashboard.getNumber("WristF", Constants.WRIST_KF));
            System.out.println("Wristp" + SmartDashboard.getNumber("WristP", -1));
        }*/
        ///maxVelocity = Math.max(maxVelocity, Math.abs(talon1.getEncoderVelocity()));
        // System.out.println("angle " + Math.toDegrees(getAngle()));
        // System.out.println(talon1.talon.getSelectedSensorPosition());
        SmartDashboard.putNumber("angleShoulder", getAngle() * 180 / Math.PI);
        SmartDashboard.putNumber("heightShoulder", getHeight());
        SmartDashboard.putNumber("shoulder encu", talon1.getEncoderPosition());
        SmartDashboard.putNumber("ShoulderDesHeight", heightToEncU(
                Constants.SHOULDER_MIN_POSITION + (im.getShoulderManualHeight() * (Constants.SHOULDER_RANGE))));
        SmartDashboard.putNumber("shoulderdiff", talon1.talon.getClosedLoopError());
        ArmHeight newPos = im.getArmPosition();
        if (newPos != ArmHeight.NO_CHANGE || desiredPos == ArmHeight.FULL_MANUAL
                || desiredPos == ArmHeight.POSITION_MANUAL) {
            desiredPos = im.getArmPosition();
        }
        switch (desiredPos) {
        case NO_MOVEMENT:
            talon1.set(ControlMode.PercentOutput, 0);
            break;
        case HATCH_LOW:
            setHeight(FieldConstants.LOW_HATCH_HEIGHT + Constants.INTAKE_OFFSET_HATCH
                    + 5.0 * im.getShoulderManualHeight());
            break;
        case HATCH_MEDIUM:
            setHeight(FieldConstants.MID_HATCH_HEIGHT + Constants.INTAKE_OFFSET_HATCH
                    + 5.0 * im.getShoulderManualHeight());
            break;
        case HATCH_HIGH:
            setHeight(FieldConstants.HIGH_HATCH_HEIGHT + Constants.INTAKE_OFFSET_HATCH
                    + 5.0 * im.getShoulderManualHeight());
            break;
        case BALL_LOW:
            setHeight(
                    FieldConstants.LOW_PORT_HEIGHT + Constants.INTAKE_OFFSET_BALL + 5.0 * im.getShoulderManualHeight());
            break;
        case BALL_MEDIUM:
            setHeight(
                    FieldConstants.MID_PORT_HEIGHT + Constants.INTAKE_OFFSET_BALL + 5.0 * im.getShoulderManualHeight());
            break;
        case BALL_HIGH:
            setHeight(Constants.SHOULDER_MAX_POSITION + 5.0 * im.getShoulderManualHeight());
            break;
        case GROUND_PICKUP:
            setHeight(Constants.SHOULDER_MIN_POSITION + 4.0 + 5.0 * im.getShoulderManualHeight());
            break;
        case POSITION_MANUAL:
            setHeight(Constants.SHOULDER_MIN_POSITION
                    + ((1.0 + im.getShoulderManualHeight()) / 2.0 * (Constants.SHOULDER_RANGE)));
            break;
        case FULL_MANUAL:
            talon1.set(ControlMode.PercentOutput, .1 + im.getShoulderManualHeight());
            
            break;
        case STOW:
            setHeight(Constants.SHOULDER_MIN_POSITION);
        }

        currentPosition = getHeight();

    }

    /**
     * Sets the shoulder height to a position between its lowest height and its
     * highest height
     * 
     * @param height
     */
    public void setHeight(double height) {
        height = Math.min(height, Constants.SHOULDER_MAX_POSITION);
        talon1.set(ControlMode.MotionMagic, heightToEncU(height));
    }

    /**
     * Returns the angle of the shoulder
     * 
     * @return the angle in radians
     */
    public double getAngle() {
        return talon1.getEncoderPositionContextual();
    }

    /**
     * Returns the height of the end of the arm (not counting the intake) off of the
     * ground
     * 
     * @return the height of the arm in inches
     */
    public double getHeight() {
        return Constants.ARM_JOINT_HEIGHT + Constants.ARM_LENGTH * Math.sin(getAngle());
    }

    /**
     * Converts arm height to Encoder units of the absolute encoder on the shoulder
     * joint
     * 
     * @param height
     *                   the height in inches of the end of the arm off the ground
     * @return the encoder units of that height
     */
    public double heightToEncU(double height) {
        return Math.asin((height - Constants.ARM_JOINT_HEIGHT) / Constants.ARM_LENGTH) / (2 * Math.PI)
                * Constants.SHOULDER_TICKS_PER_ROTATION + Constants.SHOULDER_ENCU_ZERO;
    }

}