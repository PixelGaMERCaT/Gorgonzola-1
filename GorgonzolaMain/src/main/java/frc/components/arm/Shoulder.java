package frc.components.arm;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.CheeseLog.Loggable;
import frc.CheeseLog.SQLType.Bool;
import frc.CheeseLog.SQLType.Decimal;
import frc.CheeseLog.SQLType.Type;
import frc.components.ArmHeight;
import frc.components.Component;
import frc.components.InputManager;
import frc.components.LogInterface;
import frc.components.NetworkInterface;
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
    private Wrist wrist;
    private NetworkInterface robotDataTable;
    private ArmIMU imu;

    public Shoulder() {
        desiredPos = ArmHeight.NO_MOVEMENT;
        talon1 = new ShoulderTalonManager(RobotMap.SHOULDER_TALON_1);
        talon2 = new ShoulderTalonManager(RobotMap.SHOULDER_TALON_2);
        heightSetpoint = Constants.SHOULDER_MIN_POSITION;
        talon1.initEncoder(Constants.SHOULDER_KP, Constants.SHOULDER_KI, Constants.SHOULDER_KD, Constants.SHOULDER_KF);
        if (Globals.isProto) {
            talon1.talon.setSensorPhase(true);
            talon2.talon.setSensorPhase(true);
            talon1.setInverted(false);
            talon2.setInverted(false);
        } else {
            talon1.talon.setSensorPhase(true);
            talon2.talon.setSensorPhase(true);
            talon1.setInverted(false);
            talon2.setInverted(false);

        }
        talon1.talon.configFeedbackNotContinuous(true, 0);
        talon2.talon.configFeedbackNotContinuous(true, 0);
        talon2.follow(talon1);
        imu = Globals.armIMU;
        currentPosition = getHeight();
    }

    public void init() {
        robotDataTable = Globals.robotDataTable;
        im = Globals.im;
        logger = Globals.logger;
        logger.shoulder = LogInterface.table("Shoulder",
                new String[] { "ShoulderVelocity", "ShoulderAngle", "ShoulderDesHeight", "Shoulderpercentout",
                        "Shoulderdifference", "Setpoint", "Shoulderencu", "ShoulderIMUAngle", "ShoulderIMU0",
                        "ShoulderIMU1", "ShoulderIMU2", "ShouldIMUSave", "SaveOverride" },
                new Type[] { new Decimal(), new Decimal(), new Decimal(), new Decimal(), new Decimal(), new Decimal(),
                        new Decimal(), new Decimal(), new Decimal(), new Decimal(), new Decimal(), new Bool(), new Bool() },
                new Loggable[] { () -> talon1.getEncoderVelocity(), () -> getHeight(), () -> heightSetpoint,
                        () -> talon1.talon.getMotorOutputPercent(), () -> talon1.talon.getClosedLoopError(),
                        () -> talon1.talon.getClosedLoopTarget(), () -> talon1.getEncoderPosition(),
                        () -> imu.getArmAngle(), () -> imu.getPidgeyAngles()[0], () -> imu.getPidgeyAngles()[1],
                        () -> imu.getPidgeyAngles()[2], () -> shouldSave(), ()->imu.imuOverride });
        wrist = Globals.wrist;

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
        // System.out.println(talon1.talon.getSelectedSensorPosition());
        /*SmartDashboard.putNumber("shoulderdiff", talon1.talon.getClosedLoopError());
        SmartDashboard.putNumber("shouldercurrent", talon1.talon.getOutputCurrent());
        */
        SmartDashboard.putNumber("angleShoulder", getAngle() * 180 / Math.PI);

        //SmartDashboard.putNumber("heightShoulder", getHeight());
        //SmartDashboard.putNumber("shoulder encu", talon1.getEncoderPosition());
        robotDataTable.setDouble("ShoulderAngle", getAngle());
        ArmHeight newPos = im.getArmPosition();
        if (newPos != ArmHeight.NO_CHANGE || desiredPos == ArmHeight.FULL_MANUAL
                || desiredPos == ArmHeight.POSITION_MANUAL) {
            desiredPos = im.getArmPosition();
        }
        //SmartDashboard.putNumber("IMUPOs", imu.getArmAngle());
        //SmartDashboard.putNumber("IMUPosDiff", imu.getArmAngle()-Math.toDegrees(getAngle()));
        if (shouldSave() && !imu.imuOverride) {
            System.out.println("STOPPING ARM");
            robotDataTable.setBoolean("armStop", true);
            desiredPos = ArmHeight.NO_MOVEMENT;
        } else {
            robotDataTable.setBoolean("armStop", false);
            if (imu.imuOverride) {
                robotDataTable.setBoolean("armOverride", true);
            } else {
                robotDataTable.setBoolean("armOverride", false);
            }
        }
        if (im.shoulderManual)

        {
            if (im.getArmSafetyButton()) {
                talon1.set(ControlMode.PercentOutput, .025 + .5 * im.getShoulderManualHeight());
            }
        } else {
            switch (desiredPos) {
            case BALL_CARGO:
                setHeight(FieldConstants.CARGO_BALL + 5.0 * im.getShoulderManualHeight());
                break;
            case NO_MOVEMENT:
                talon1.set(ControlMode.PercentOutput, 0);
                break;
            case HATCH_LOW:
                setHeight(FieldConstants.LOW_HATCH_HEIGHT + Constants.INTAKE_OFFSET_HATCH+ 4.0
                        + 5.0 * im.getShoulderManualHeight());
                break;
            case HATCH_MEDIUM:
                setHeight(FieldConstants.MID_HATCH_HEIGHT + Constants.INTAKE_OFFSET_HATCH + 5.0
                        + 5.0 * im.getShoulderManualHeight());
                break;
            case HATCH_HIGH:
                setHeight(FieldConstants.HIGH_HATCH_HEIGHT + Constants.INTAKE_OFFSET_HATCH
                        +4.0+ 5.0 * im.getShoulderManualHeight());
                break;
            case BALL_LOW:
                setHeight(FieldConstants.LOW_PORT_HEIGHT + Constants.INTAKE_OFFSET_BALL
                       +5.25 + 5.0 * im.getShoulderManualHeight());
                break;
            case BALL_MEDIUM:
                setHeight(FieldConstants.MID_PORT_HEIGHT + Constants.INTAKE_OFFSET_BALL + 3.0
                        +2.0+ 5.0 * im.getShoulderManualHeight());
                break;
            case BALL_HIGH:
                setHeight(Constants.SHOULDER_MAX_POSITION + 5.0 * im.getShoulderManualHeight());
                break;
            case GROUND_PICKUP:
                setHeight(Constants.SHOULDER_MIN_POSITION + 8.25 + Math.max(-2.5, 5.0 * im.getShoulderManualHeight()));
                break;
            case POSITION_MANUAL:
                setHeight(Constants.SHOULDER_MIN_POSITION
                        + ((1.0 + im.getShoulderManualHeight()) / 2.0 * (Constants.SHOULDER_RANGE)));
                break;
            case FULL_MANUAL:
                talon1.set(ControlMode.PercentOutput, .1 + .5 * im.getShoulderManualHeight());
                break;
            case STOW:
                if (getHeight() < 25.0 && wrist.getAngle() < 110.0 * Math.PI / 180.0) {
                    setHeight(getHeight());
                } else {
                    setHeight(Constants.SHOULDER_MIN_POSITION - 4.0);
                }
                break;
            }
        }
        currentPosition =getHeight();

    }

    private int badTick = 0;

    private boolean shouldSave() {
        //   System.out.println(talon1.talon.getMotorOutputPercent());

        if (talon1.talon.getMotorOutputPercent() > .75 && talon1.getEncoderVelocity() < 5) {
            badTick++;
        } else {
            badTick = 0;
        }
        return badTick > 15;
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