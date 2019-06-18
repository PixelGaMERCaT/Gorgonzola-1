package frc.components.arm;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.CheeseLog.Loggable;
import frc.CheeseLog.SQLType.Decimal;
import frc.CheeseLog.SQLType.Type;
import frc.components.ArmHeight;
import frc.components.Component;
import frc.components.InputManager;
import frc.components.LogInterface;
import frc.components.NetworkInterface;
import frc.robot.Constants;
import frc.robot.Globals;
import frc.robot.RobotMap;
import frc.talonmanager.WristTalonManager;

public class Wrist implements Component {
    public double height, angleSetpoint;
    private WristTalonManager talon1, talon2;
    private LogInterface logger;
    private InputManager inputManager;
    private Shoulder shoulder;
    private NetworkInterface robotDataTable;

    public Wrist() {
        talon1 = new WristTalonManager(RobotMap.WRIST_TALON_1);
        talon2 = new WristTalonManager(RobotMap.WRIST_TALON_2);
        if (Globals.isProto) {
            talon1.talon.setSensorPhase(true);
            talon2.talon.setSensorPhase(true);
        } else {
            talon1.talon.setSensorPhase(true);
            talon2.talon.setSensorPhase(true);
        }
        talon1.talon.configFeedbackNotContinuous(true, 0);
        talon2.talon.configFeedbackNotContinuous(true, 0);
        if (Globals.isProto) {
            talon1.setInverted(false);
            talon2.setInverted(false);
        } else {
            talon1.setInverted(true);
            talon2.setInverted(false);
        }
        talon1.initEncoder(Constants.WRIST_KP, Constants.WRIST_KI, Constants.WRIST_KD, Constants.WRIST_KF);

        talon2.follow(talon1);
        angleSetpoint = 0;
    }

    public void init() {
        robotDataTable = Globals.robotDataTable;
        /*SmartDashboard.putNumber("WristI", 0);
        SmartDashboard.putNumber("WristD", Constants.WRIST_KD);
        SmartDashboard.putNumber("WristF", Constants.WRIST_KF);
        */
        inputManager = Globals.inputManager;
        logger = Globals.logger;
        shoulder = Globals.shoulder;
        logger.wrist = LogInterface.table("Wrist",
                new String[] { "Velocity", "Angle", "desangle", "percentout", "difference", "Setpoint", "encu" },
                new Type[] { new Decimal(), new Decimal(), new Decimal(), new Decimal(), new Decimal(), new Decimal(),
                        new Decimal() },
                new Loggable[] { () -> talon1.getEncoderVelocity(), () -> getAngle(), () -> angleSetpoint,
                        () -> talon1.talon.getMotorOutputPercent(), () -> talon1.talon.getClosedLoopError(),
                        () -> talon1.talon.getClosedLoopTarget(), () -> talon1.getEncoderPosition() });
    }

    double maxVelocity = 0;
    boolean fromStow = false;
    boolean fromLow = false;

    public void tick() {
        //SmartDashboard.putNumber("WristP", Constants.WRIST_KP);
        robotDataTable.setDouble("WristAngle", getAngle());

        /*maxVelocity = Math.max(maxVelocity, Math.abs(talon1.getEncoderVelocity()));
        SmartDashboard.putNumber("maxvel", maxVelocity);
        SmartDashboard.putNumber("ffd ", talon1.talon.getActiveTrajectoryArbFeedFwd());
        SmartDashboard.putNumber("Derivative ", talon1.talon.getErrorDerivative());
        SmartDashboard.putNumber("error", talon1.talon.getClosedLoopError());
        SmartDashboard.putNumber("target", talon1.talon.getClosedLoopTarget());
        SmartDashboard.putNumber("target ??", talon1.talon.getActiveTrajectoryPosition(0));
        //System.out.println("wristps" + talon1.talon.getSelectedSensorPosition(0));
        SmartDashboard.putString("ControlMode", talon1.talon.getControlMode().toString());
        */
        SmartDashboard.putNumber("WristAngle", getAngle() * 180.0 / Math.PI);
        SmartDashboard.putNumber("wristencu", talon1.talon.getSelectedSensorPosition(0));

        if (fromStow && shoulder.desiredPos != ArmHeight.STOW) {
            talon1.talon.config_kP(0, Constants.WRIST_KP);
            if (shoulder.desiredPos == ArmHeight.HATCH_LOW) {
                //talon1.talon.config_kD(0, 10.0);
                fromLow = true;
            }
            fromStow = false;
        }
        if (fromLow && shoulder.desiredPos != ArmHeight.HATCH_LOW) {
            //talon1.talon.config_kD(0, Constants.WRIST_KD);
            fromLow = false;
        }
        if (inputManager.wristManual) {
            if (inputManager.getArmSafetyButton()) {
                //System.out.println("running this");
                talon1.set(ControlMode.PercentOutput, /*.1 +*/ .5 * inputManager.getWristManualPosition());
            }
        } else {
            switch (shoulder.desiredPos) {
            case STOW:

                if (shoulder.getHeight() > 50) {
                    setAngle(-shoulder.getAngle());
                } else if (talon1.getEncoderVelocity() > 5) {
                    talon1.talon.config_kP(0, 1);
                    setAngle(Constants.WRIST_STOW_POSITION);
                } else {
                    talon1.talon.config_kP(0, Constants.WRIST_KP);
                    setAngle(Constants.WRIST_STOW_POSITION);

                }
                fromStow = true;
                break;
            case NO_MOVEMENT:
                talon1.set(ControlMode.PercentOutput, 0);
                break;
            case POSITION_MANUAL:
                setAngle(inputManager.getWristManualPosition());
                break;
            case FULL_MANUAL:
                talon1.set(ControlMode.PercentOutput, .1 + .5 * inputManager.getWristManualPosition());
                break;
            case BALL_HIGH:
                if (shoulder.getHeight() > 30.0) {
                    setAngle(0.0 * Math.PI / 180.0 + Math.PI / 180.0 * 10.0 * inputManager.getWristManualPosition());
                } else {
                    setAngle(-shoulder.getAngle()); //TODO Math.PI / 180.0 * 10.0 * im.getWristManualPosition()
                }
                break;
            case BALL_LOW:
                setAngle(-shoulder.getAngle() + (15.0 + 10.0 * inputManager.getWristManualPosition()) * Math.PI / 180.0);
                break;

            case BALL_MEDIUM:
                setAngle(-shoulder.getAngle() + (25.0 + 10.0 * inputManager.getWristManualPosition()) * Math.PI / 180.0);
                break;
            case BALL_CARGO:
                if (shoulder.getHeight() > 25) {
                    setAngle(-shoulder.getAngle() - 35.0 * Math.PI / 180.0);
                } else {
                    setAngle(-shoulder.getAngle());
                }
                break;
            case GROUND_PICKUP:
                setAngle(-shoulder.getAngle() + Constants.WRIST_GEAR_OFFSET
                        + ((-3.0 + 10.0 * inputManager.getWristManualPosition()) * Math.PI / 180.0));
                break;
            case HATCH_HIGH:
                setAngle(-shoulder.getAngle() + Constants.WRIST_GEAR_OFFSET
                        + ((10.0 + 10.0 * inputManager.getWristManualPosition()) * Math.PI / 180.0));
                break;
            case NO_CHANGE:
                break;
            default:
                setAngle(-shoulder.getAngle() + Constants.WRIST_GEAR_OFFSET
                        + (10.0 * inputManager.getWristManualPosition() * Math.PI / 180.0));
            }
        }

    }

    /**
     * Sets the angle of the arm
     * 
     * @param angle
     *                  the angle to set the arm to (radians)
     */
    public void setAngle(double angle) {
        talon1.set(ControlMode.MotionMagic, angleToEncU(angle));
    }

    /**
     * Returns the angle of the shoulder
     * 
     * @return the angle in radians
     */
    public double getAngle() {
        return talon1.getEncoderPositionContextual(); // TODO better solution
    }

    /**
     * Returns the height of the end of the intake off of the ground
     * 
     * @return the height of the arm in inches
     */
    public double getHeight() {
        return shoulder.getHeight() + Constants.WRIST_LENGTH * Math.sin(getAngle());
    }

    /**
     * Converts wrist angle to Encoder units of the absolute encoder on the wrist
     * joint
     * 
     * @param angle
     *                  the angle in radians of the intake
     * @return the encoder units of that angle
     */
    public double angleToEncU(double angle) {
        return angle / (2 * Math.PI) * Constants.WRIST_TICKS_PER_ROTATION + Constants.WRIST_ENCU_ZERO;
    }

}