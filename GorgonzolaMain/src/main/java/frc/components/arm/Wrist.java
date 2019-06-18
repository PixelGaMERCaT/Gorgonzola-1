package frc.components.arm;

import com.ctre.phoenix.motorcontrol.ControlMode;

import frc.CheeseLog.Loggable;
import frc.CheeseLog.SQLType.Decimal;
import frc.CheeseLog.SQLType.Type;
import frc.components.Component;
import frc.components.InputManager;
import frc.components.LogInterface;
import frc.components.NetworkInterface;
import frc.robot.Constants;
import frc.robot.Globals;
import frc.robot.RobotMap;
import frc.talonmanager.WristTalonManager;

/**
 * Controls the Wrist (joint on the end of the arm) based off the state of the Shoulder
 */
public class Wrist implements Component {
    private WristTalonManager talon1, talon2;
    private LogInterface logger;
    private InputManager inputManager;
    private Shoulder shoulder;
    private NetworkInterface robotDataTable;
    private boolean fromStow = false; // Indicates whether our last position was stow

    public Wrist() {
        talon1 = new WristTalonManager(RobotMap.WRIST_TALON_1);
        talon2 = new WristTalonManager(RobotMap.WRIST_TALON_2);
        if (Globals.isProto) {
            talon1.setSensorPhase(true);
            talon2.setSensorPhase(true);
            talon1.setInverted(false);
            talon2.setInverted(false);
        } else {
            talon1.setSensorPhase(true);
            talon2.setSensorPhase(true);
            talon1.setInverted(true);
            talon2.setInverted(false);
        }
        talon1.initEncoder(Constants.WRIST_KP, Constants.WRIST_KI, Constants.WRIST_KD, Constants.WRIST_KF);
        talon2.follow(talon1);
    }

    public void init() {
        robotDataTable = Globals.robotDataTable;
        inputManager = Globals.inputManager;
        logger = Globals.logger;
        shoulder = Globals.shoulder;
        logger.wrist = LogInterface.createTable("Wrist",
                new String[] { "Velocity", "Angle", "PercentOut", "Difference", "Setpoint", "Encu" },
                new Type[] { new Decimal(), new Decimal(), new Decimal(), new Decimal(), new Decimal(), new Decimal() },
                new Loggable[] { () -> talon1.getEncoderVelocity(), () -> getAngle(),
                        () -> talon1.getMotorOutputPercent(), () -> talon1.getClosedLoopError(),
                        () -> talon1.getClosedLoopTarget(), () -> talon1.getEncoderPosition() });
    }

    public void tick() {
        robotDataTable.setDouble("WristAngle", getAngle());

        if (fromStow && shoulder.desiredPosition != ArmPosition.STOW) {
            //If we are leaving stow, reset changes to kP
            talon1.config_kP(Constants.WRIST_KP);
            fromStow = false;
        }

        if (inputManager.isWristManual()) {
            //Manual Control:
            if (inputManager.getArmSafetyButton()) {
                talon1.set(ControlMode.PercentOutput, .5 * inputManager.getWristManualPosition());
            }
        } else {
            //Automatic control (Note: setAngle(-shoulder.getAngle) orients the wrist horizontally, regardless of shoulder angle)
            ArmPosition shoulderPosition = shoulder.desiredPosition;
            switch (shoulderPosition) {
            default:
                //-shoulder.getAngle() -- normalized to whatever angle makes the wrist horizontal
                //shoulderPosition.wristAngle -- adjust based on current setpoint's desired absolute angle
                //10.0 * inputManager.getWristManualPosition --adjust based on movement of joysticks (manual adjustment of joysticks).
                setAngle(-shoulder.getAngle()
                        + (shoulderPosition.getWristAngle() + 10.0 * inputManager.getWristManualPosition()) * Math.PI
                                / 180.0);

            case NO_CHANGE:
            case NO_MOVEMENT:
                talon1.set(ControlMode.PercentOutput, 0);
                break;

            case STOW:
                if (shoulder.getHeight() > 50) {
                    //Stay level with the ground until the shoulder is below a given height threshold
                    setAngle(-shoulder.getAngle());
                } else if (talon1.getEncoderVelocity() > 5) {
                    //If we are still moving, we are not yet in stow. Therefore reduce kP to make transition smoother
                    talon1.config_kP(1);
                    setAngle(shoulderPosition.getWristAngle()*Math.PI/180.0);
                } else {
                    //If we are not moving at sufficiently large speed, we are in stow. Increase kP to keep stow from slipping.
                    talon1.config_kP(Constants.WRIST_KP);
                    setAngle(shoulderPosition.getWristAngle()*Math.PI/180.0);
                }
                fromStow = true;
                break;

            case FULL_MANUAL:
                if (shoulder.getHeight() > 20) {
                    setAngle(-shoulder.getAngle() + (10.0 * inputManager.getWristManualPosition()) * Math.PI / 180.0);
                } else {
                    //If we're below a certain threshold, just drop.
                    talon1.set(ControlMode.PercentOutput, 0);
                }
                break;

            case BALL_HIGH:
                if (shoulder.getHeight() > 30.0) {
                    setAngle((10.0 * inputManager.getWristManualPosition()) * Math.PI / 180.0);
                } else {
                    setAngle(-shoulder.getAngle());
                }
                break;

            case BALL_CARGO:
                if (shoulder.getHeight() > 25) {
                    setAngle(-shoulder.getAngle() - 35.0 * Math.PI / 180.0);
                } else {
                    setAngle(-shoulder.getAngle());
                }
                break;
            }
        }

    }

    /**
     * Sets the desired angle of the wrist, NOT ACCOUNTING FOR THE ANGLE OF THE SHOULDER.
     * TO SET THE ANGLE OF THE WRIST ABSOLUTELY, use 
     *         setAngle(-shoulder.getAngle+ desiredAngleInRadians)
     * 
     * @param angle the angle to set the wrist to (radians)
     */
    public void setAngle(double angle) {
        talon1.set(ControlMode.MotionMagic, angleToEncU(angle));
    }

    /**
     * Returns the angle of the wrist NOT ACCOUNTING FOR THE ANGLE OF THE SHOULDER.
     * THE ANGLE THE WRIST IS RELATIVE TO THE GROUND IS 
     *        wrist.getAngle()+shoulder.getAngle()
     * @return the angle in radians
     */
    public double getAngle() {
        return talon1.getEncoderPositionContextual(); 
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
     * @param angle the angle in radians of the intake
     * @return the encoder units of that angle
     */
    public double angleToEncU(double angle) {
        return angle / (2 * Math.PI) * Constants.WRIST_ENCU_PER_ROTATION + Constants.WRIST_ENCU_ZERO;
    }

}