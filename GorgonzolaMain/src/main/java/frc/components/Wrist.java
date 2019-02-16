package frc.components;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.CheeseLog.Loggable;
import frc.CheeseLog.SQLType.Bool;
import frc.CheeseLog.SQLType.Decimal;
import frc.CheeseLog.SQLType.Type;
import frc.robot.Constants;
import frc.robot.Globals;
import frc.robot.RobotMap;
import frc.talonmanager.WristTalonManager;

public class Wrist implements Component {
    public double height, angleSetpoint;
    private WristTalonManager talon1, talon2, intakeTalon1, intakeTalon2;
    private double currentPosition;
    private LogInterface logger;
    private InputManager im;
    private Shoulder shoulder;

    public Wrist() {
        talon1 = new WristTalonManager(RobotMap.WRIST_TALON_1);
        talon2 = new WristTalonManager(RobotMap.WRIST_TALON_2);
        intakeTalon1 = new WristTalonManager(RobotMap.INTAKE_TALON_1);
        intakeTalon2 = new WristTalonManager(RobotMap.INTAKE_TALON_2);
        talon1.talon.setSensorPhase(true);
        talon2.talon.setSensorPhase(true);
        talon1.setInverted(true);
        talon2.setInverted(true );
        intakeTalon2.follow(intakeTalon1);
        talon1.initEncoder(Constants.WRIST_KP, Constants.WRIST_KI, Constants.WRIST_KD, Constants.WRIST_KF);
        
        talon2.follow(talon1);
        angleSetpoint = 0; 
    }

    public void init() {
        im = Globals.im;
        logger = Globals.logger;
        logger.wrist = LogInterface.table("Wrist",
                new String[] { "ButtonPressed", "Velocity", "Angle", "desangle", "percentout", "difference", "Setpoint",
                        "encu" },
                new Type[] { new Bool(), new Decimal(), new Decimal(), new Decimal(), new Decimal(), new Decimal(),
                        new Decimal(), new Decimal() },
                new Loggable[] { () -> im.getWristButton(), () -> talon1.getEncoderVelocity(), () -> getAngle(),
                        () -> angleSetpoint, () -> talon1.talon.getMotorOutputPercent(),
                        () -> talon1.talon.getClosedLoopError(), () -> talon1.talon.getClosedLoopTarget(),
                        () -> talon1.getEncoderPosition() });
    }

    double maxVelocity = 0;

    public void tick() {
        maxVelocity = Math.max(maxVelocity, Math.abs(talon1.getEncoderVelocity()));
        SmartDashboard.putNumber("maxvel", maxVelocity);
        //TODO: DELETE THIS TEST CODE
        //System.out.println("setpoint " + angleSetpoint * 180.0 / Math.PI);
        SmartDashboard.putNumber("ffd ", talon1.talon.getActiveTrajectoryArbFeedFwd());
        SmartDashboard.putNumber("Derivative ", talon1.talon.getErrorDerivative());
        SmartDashboard.putNumber("error", talon1.talon.getClosedLoopError());
        SmartDashboard.putNumber("target", talon1.talon.getClosedLoopTarget());
        SmartDashboard.putNumber("target ??", talon1.talon.getActiveTrajectoryPosition(0));
        SmartDashboard.putNumber("position", talon1.talon.getSelectedSensorPosition());
        System.out.println("position "+ talon1.talon.getSelectedSensorPosition());
        System.out.println("angle"+ getAngle()*180/Math.PI);
        SmartDashboard.putString("ControlMode", talon1.talon.getControlMode().toString());
        SmartDashboard.putNumber("Angle", getAngle() * 180.0 / Math.PI);
        if (im.getWristButton()) {
            
            //talon1.set(ControlMode.PercentOutput, .1 + im.getShoulderHeight() * 2.0 - 1.0);

            angleSetpoint = (im.getShoulderHeight() * 2.0 - 1.0) * Constants.WRIST_ANGLE_RANGE / 2.0;
            setAngle(angleSetpoint);

        } else {
            //talon1.set(ControlMode.PercentOutput, 0);
            setAngle(0);
        }

        /*if (im.getIntakeOutButton()){
            intakeTalon1.set(ControlMode.PercentOutput, -1);
        } else if (im.getIntakeInButton()) {
            intakeTalon1.set(ControlMode.PercentOutput, 1);
        } else {
            intakeTalon1.set(ControlMode.PercentOutput, 0);
        }*/
        //setAngle(-shoulder.getAngle());
        //currentPosition = getHeight();

    }

    /**
     * Sets the angle of the arm
     * @param angle the angle to set the arm to (radians)
     */
    public void setAngle(double angle) {
        talon1.set(ControlMode.MotionMagic, angleToEncU(angle));
    }

    /**
     * Returns the angle of the shoulder
     * @return the angle in radians
     */
    public double getAngle() {
        return -talon1.getEncoderPositionContextual(); //TODO better solution
    }

    /**
     * Returns the height of the end of the intake off of the ground
     * @return the height of the arm in inches
     */
    public double getHeight() {
        return shoulder.getHeight() + Constants.WRIST_LENGTH * Math.sin(getAngle());
    }

    /**
     * Converts wrist angle to Encoder units of the absolute encoder on the wrist joint
     * @param angle the angle in radians of the intake
     * @return the encoder units of that angle
     */
    public double angleToEncU(double angle) {
        return angle / (2 * Math.PI) * Constants.WRIST_TICKS_PER_ROTATION + Constants.WRIST_ENCU_ZERO;
    }

}