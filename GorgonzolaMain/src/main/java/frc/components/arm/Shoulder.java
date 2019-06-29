package frc.components.arm;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.CheeseLog.Loggable;
import frc.CheeseLog.SQLType.Bool;
import frc.CheeseLog.SQLType.Decimal;
import frc.CheeseLog.SQLType.Int;
import frc.CheeseLog.SQLType.Type;
import frc.components.Component;
import frc.components.InputManager;
import frc.components.LogInterface;
import frc.components.NetworkInterface;
import frc.robot.Constants;
import frc.robot.Globals;
import frc.robot.RobotMap;
import frc.talonmanager.ShoulderTalonManager;

/**
 * Controls the shoulder (stationary joint) of the arm.
 * Uses button input to convert desired setpoint heights to encoder values, then uses magic profiling 
 * on the TalonSRX to move the shoulder to that point.
 * @author Jeff
 */ 
public class Shoulder implements Component {
    private ShoulderTalonManager talon1, talon2;
    private LogInterface logger;
    private InputManager inputManager;
    public ArmPosition desiredPosition; //the desired position of the arm
    private Wrist wrist;
    private NetworkInterface robotDataTable;
    private boolean armSaveOverride; //Indicates whether the automatic arm save has been overridden
    private int unresponsiveTickCount; //Indicates the number of ticks the shoulder has been unresponsive

    public Shoulder() {
        unresponsiveTickCount = 0;
        desiredPosition = ArmPosition.NO_MOVEMENT;
        talon1 = new ShoulderTalonManager(RobotMap.SHOULDER_TALON_1);
        talon2 = new ShoulderTalonManager(RobotMap.SHOULDER_TALON_2);
        talon1.initEncoder(Constants.SHOULDER_KP, Constants.SHOULDER_KI, Constants.SHOULDER_KD, Constants.SHOULDER_KF);
        if (Globals.isProto) {
            talon1.setSensorPhase(true);
            talon2.setSensorPhase(true);
            talon1.setInverted(false);
            talon2.setInverted(false);
        } else {
            talon1.setSensorPhase(true);
            talon2.setSensorPhase(true);
            talon1.setInverted(false);
            talon2.setInverted(false);
        }
        talon2.follow(talon1);
    }

    public void init() {
        robotDataTable = Globals.robotDataTable;
        inputManager = Globals.inputManager;
        logger = Globals.logger;
        wrist = Globals.wrist;

        logger.shoulder = LogInterface.createTable("Shoulder",
                new String[] { "ShoulderVelocity", "ShoulderAngle", "ShoulderHeight", "Shoulderpercentout",
                        "ShoulderError", "ShoulderEncuSetpoint", "ShoulderEncu", "ShoulderSaveTick",
                        "ShouldShoulderSave", "ShoulderSaveOverride" },
                new Type[] { new Decimal(), new Decimal(), new Decimal(), new Decimal(), new Decimal(), new Decimal(),
                        new Decimal(), new Int(), new Bool(), new Bool() },
                new Loggable[] { () -> talon1.getEncoderVelocity(), () -> getAngle(), () -> getHeight(),
                        () -> talon1.getMotorOutputPercent(), () -> talon1.getClosedLoopError(),
                        () -> talon1.getClosedLoopTarget(), () -> talon1.getEncoderPosition(),
                        () -> unresponsiveTickCount, () -> shouldSave(), () -> armSaveOverride });

    }

    public void tick() {
        SmartDashboard.putNumber("Shoulder Angle", Math.toDegrees(getAngle()));
        armSaveOverride = inputManager.getShoulderSaveOverride();
        robotDataTable.setDouble("ShoulderAngle", getAngle());
        ArmPosition newPosition = inputManager.getArmPosition();

        if (newPosition != ArmPosition.NO_CHANGE || desiredPosition == ArmPosition.FULL_MANUAL) {
            //Only update the position if there is a change (between auto states or from manual to auto)
            desiredPosition = newPosition;
        }

        if (shouldSave() && !armSaveOverride) {
            //If we detect something wrong with the arm, stop moving
            System.out.println("STOPPING ARM");
            robotDataTable.setBoolean("armStop", true);
            desiredPosition = ArmPosition.NO_MOVEMENT;
        } else {
            robotDataTable.setBoolean("armStop", false);
        }
        robotDataTable.setBoolean("armOverride", armSaveOverride);

        switch (desiredPosition) {
        case NO_CHANGE:
        case NO_MOVEMENT:
            talon1.set(ControlMode.PercentOutput, 0);
            break;
        case FULL_MANUAL:
            talon2.set(ControlMode.PercentOutput, /*.1*/ + .5 * inputManager.getShoulderManualPosition());
            break;
        case STOW:
            if (getHeight() < 25.0 && wrist.getAngle() < 110.0 * Math.PI / 180.0) {
                setHeight(getHeight());
            } else {
                setHeight(Constants.SHOULDER_MIN_POSITION - 4.0);
            }
            break;
        default:
            setHeight(desiredPosition.getShoulderHeight() + 5.0 * inputManager.getShoulderManualPosition());
        }

    }

    /**
     * Determines whether the encoder is likely broken by comparing the speed of the shoulder to the power the shoulder is being given
     */
    private boolean shouldSave() {
        if (Math.abs(talon1.getMotorOutputPercent()) > .75 && Math.abs(talon1.getEncoderVelocity()) < 5) {
            unresponsiveTickCount++;
        } else {
            unresponsiveTickCount = 0;
        }
        return unresponsiveTickCount > 15;
    }

    /**
     * Sets the shoulder's desired height to a position between its lowest height and its
     * highest height
     * 
     * @param height the height to set the shoulder to
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
     * @param height the height in inches of the end of the arm off the ground
     * @return the encoder units of that height
     */
    private double heightToEncU(double height) {
        return Math.asin((height - Constants.ARM_JOINT_HEIGHT) / Constants.ARM_LENGTH) / (2 * Math.PI)
                * Constants.SHOULDER_ENCU_PER_ROTATION + Constants.SHOULDER_ENCU_ZERO;
    }

}