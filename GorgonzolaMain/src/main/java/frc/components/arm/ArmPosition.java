package frc.components.arm;

import frc.robot.Constants;

/**
 * Encompasses all the possible setpoints, positions, and states the Arm can be in.
 */
public enum ArmPosition {
    //Setpoints (known height and wrist position):
    BALL_CARGO, HATCH_LOW, HATCH_MEDIUM, HATCH_HIGH, BALL_LOW, BALL_MEDIUM, BALL_HIGH, GROUND_PICKUP, STOW,
    //If the arm should maintain its current position:
    NO_CHANGE, 
    //If no power should be given to any motor on the arm:
    NO_MOVEMENT,
    //If setpoints have been overriden in favor of manual arm control
    FULL_MANUAL;


    /**
     * The height of the shoulder for a given ArmPosition
     */
    private double shoulderHeight;
    /**
     * The angle of the wrist for a given ArmPosition
     */
    private double wristAngle;
    static {
        //shoulder heights:
        GROUND_PICKUP.shoulderHeight = 1.0 * 12.0 + 8.25;
        BALL_LOW.shoulderHeight = 3.0 * 12.0 + 0.65;
        BALL_MEDIUM.shoulderHeight = 5.0 * 12.0 + 3.5;
        BALL_HIGH.shoulderHeight = 6.0 * 12.0 + 11.5;
        BALL_CARGO.shoulderHeight = 4.0 * 12.0 + 11.0;

        HATCH_LOW.shoulderHeight = 1.0 * 12.0 + 6.8;
        HATCH_MEDIUM.shoulderHeight = 4.0 * 12.0 + 8.7;
        HATCH_HIGH.shoulderHeight = 6.0 * 12.0 + 9.8;

        STOW.shoulderHeight=Constants.SHOULDER_MIN_POSITION;

        
        //Wrist Angles:
        BALL_LOW.wristAngle=15.0;
        BALL_MEDIUM.wristAngle=25.0;
        GROUND_PICKUP.wristAngle=13.6;

        HATCH_HIGH.wristAngle=13.6;
        HATCH_MEDIUM.wristAngle=13.6;
        HATCH_LOW.wristAngle=13.6;

        STOW.wristAngle = 160.0;

    }
    /**
     * Returns the angle the wrist should be at for this ArmPosition
     * @return The angle, in degrees
     */
    public double getWristAngle(){
        return this.wristAngle;
    }
    /**
     * Returns height the shoulder should be at for this ArmPosition
     * @return The height, in inches.
     */
    public double getShoulderHeight(){
        return this.shoulderHeight;
    }
}
