package frc.components;

import com.ctre.phoenix.motorcontrol.ControlMode;

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
        talon1.initEncoder(0, 0, 0, 0);
        talon2.follow(talon1);
        currentPosition = getHeight();
    }

    public void init() {
        im = Globals.im;
        logger = Globals.logger;

    }

    double maxVelocity = 0;

    public void tick() {
        maxVelocity=Math.max(maxVelocity, talon1.getEncoderVelocity()/1024.0*10.0*Math.PI*2);
        //System.out.println("angle " + Math.toDegrees(getAngle()));
        //System.out.println("maxVelocity "+ maxVelocity);
        System.out.println("encu "+ talon1.getEncoderPosition());
        if (im.getShoulderButton()) {
            System.out.println("applying power" +( im.getShoulderHeight()*2.0-1.0) + "\n"+ talon1.talon.getMotorOutputPercent());
            talon1.set(ControlMode.PercentOutput, -.15-((im.getShoulderHeight()*2.0)-1.0));
            
            /*setHeight(Constants.SHOULDER_MIN_POSITION + (im.getShoulderHeight() * (Constants.SHOULDER_RANGE)));
            currentPosition = getHeight();
            */
        } else {
            talon1.set(ControlMode.PercentOutput, -0.15);
            //setHeight(currentPosition);
        }
    }

    /**
     * Sets the shoulder height to a position between its lowest height and its highest height
     * @param height
     */
    public void setHeight(double height) {
        talon1.set(ControlMode.MotionMagic, heightToEncU(height));
    }

    /**
     * Returns the angle of the shoulder
     * @return the angle in radians
     */
    public double getAngle() {
        return talon1.getEncoderPositionContextual();
    }

    /**
     * Returns the height of the end of the arm (not counting the intake) off of the ground
     * @return the height of the arm in inches
     */
    public double getHeight() {
        return Constants.ARM_JOINT_HEIGHT + Constants.ARM_LENGTH * Math.sin(getAngle());
    }

    /**
     * Converts arm height to Encoder units of the absolute encoder on the shoulder joint
     * @param height the height in inches of the end of the arm off the ground
     * @return the encoder units of that height
     */
    public double heightToEncU(double height) {
        return Math.asin((height - Constants.ARM_JOINT_HEIGHT) / Constants.ARM_LENGTH) / (2 * Math.PI)
                * Constants.SHOULDER_TICKS_PER_ROTATION;
    }

}