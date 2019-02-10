package frc.components;

import com.ctre.phoenix.motorcontrol.ControlMode;

import frc.robot.Constants;
import frc.robot.Globals;
import frc.robot.RobotMap;
import frc.talonmanager.TalonManager;
import frc.talonmanager.WristTalonManager;

public class Wrist implements Component {
    public double height, armlength;
    private TalonManager talon1, talon2;
    private double currentPosition;
    private LogInterface logger;
    private InputManager im;
    private Shoulder shoulder;

    public Wrist() {
        talon1 = new WristTalonManager(RobotMap.WRIST_TALON_1);
        talon2 = new WristTalonManager(RobotMap.WRIST_TALON_2);
        talon1.initEncoder(0, 0, 0, 0);
        talon2.follow(talon1);
        currentPosition = getHeight();
    }

    public void init() {
        im = Globals.im;
        logger = Globals.logger;

    }

    public void tick() {

        setAngle(-shoulder.getAngle());
        currentPosition = getHeight();

    }

    /**
     * Sets the angle of the arm
     * @param angle the angle to set the arm to (radians)
     */
    public void setAngle(double angle) {
        talon1.set(ControlMode.MotionMagic, angleToEncU(height));
    }

    /**
     * Returns the angle of the shoulder
     * @return the angle in radians
     */
    public double getAngle() {
        return talon1.getEncoderPositionContextual();
    }

    /**
     * Returns the height of the end of the intake off of the ground
     * @return the height of the arm in inches
     */
    public double getHeight() {
        return shoulder.getHeight()+Constants.WRIST_LENGTH*Math.sin(getAngle());
    }

    /**
     * Converts wrist angle to Encoder units of the absolute encoder on the wrist joint
     * @param angle the angle in radians of the intake
     * @return the encoder units of that angle
     */
    public double angleToEncU(double angle) {
        return angle/(2*Math.PI)*Constants.WRIST_TICKS_PER_ROTATION;
      }

}