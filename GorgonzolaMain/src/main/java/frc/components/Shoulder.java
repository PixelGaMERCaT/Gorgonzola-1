package frc.components;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.Globals;
import frc.robot.RobotMap;
import frc.talonmanager.ShoulderTalonManager;

public class Shoulder implements Component {
    public double height, armlength;
    private ShoulderTalonManager talon1, talon2;
    private double currentPosition;
    private LogInterface logger;
    private InputManager im;

    public Shoulder() {
        talon1 = new ShoulderTalonManager(RobotMap.SHOULDER_TALON_1);
        talon2 = new ShoulderTalonManager(RobotMap.SHOULDER_TALON_2);

        talon1.initEncoder(Constants.SHOULDER_KP, Constants.SHOULDER_KI, Constants.SHOULDER_KD, Constants.SHOULDER_KF);
        talon1.talon.setSensorPhase(true);
        talon2.talon.setSensorPhase(true);

        talon2.follow(talon1);
        currentPosition = getHeight();
    }

    public void init() {
        im = Globals.im;
        logger = Globals.logger;

    }

    double maxVelocity = 0;

    public void tick() {
        // maxVelocity=Math.max(maxVelocity,
        // talon1.getEncoderVelocity()/1024.0*10.0*Math.PI*2);
        maxVelocity = Math.max(maxVelocity, Math.abs(talon1.getEncoderVelocity()));
        // System.out.println("angle " + Math.toDegrees(getAngle()));
        System.out.println(talon1.talon.getSelectedSensorPosition());
        SmartDashboard.putNumber("angleShoulder", getAngle() * 180 / Math.PI);
        SmartDashboard.putNumber("heightShoulder", getHeight());
        SmartDashboard.putNumber("maxvelShoulder", maxVelocity);
        if (im.getShoulderButton()) {
            talon1.set(ControlMode.PercentOutput, -.1 - ((im.getShoulderHeight() * 2.0) - 1.0));

            /*
             * setHeight(Constants.SHOULDER_MIN_POSITION + (im.getShoulderHeight() *
             * (Constants.SHOULDER_RANGE))); currentPosition = getHeight();
             */
        } else {
            talon1.set(ControlMode.PercentOutput, -0.1);
            // setHeight(currentPosition);
        }
    }

    /**
     * Sets the shoulder height to a position between its lowest height and its
     * highest height
     * 
     * @param height
     */
    public void setHeight(double height) {
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
                * Constants.SHOULDER_TICKS_PER_ROTATION;
    }

}