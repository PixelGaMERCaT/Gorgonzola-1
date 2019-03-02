/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                                                                                                                 */
/* Open Source Software - may be modified and shared by FRC teams. The code         */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                                                                                                                                                                                                                         */
/*----------------------------------------------------------------------------*/

package frc.talonmanager;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import frc.robot.Constants;

/**
 * Wraps and sets up Talons and encoders
 */
public class WristTalonManager extends TalonManager {
   
    /**
     * Initializes the Talon
     * @param idx The index of the talon
     */
    public WristTalonManager(int idx) {
        super(idx);

        this.type = type.WRIST;
    }

    /**
     * initializes the encoders, including setting up PIDF constants for Motion Magic
     * @param P the Proportion constant
     * @param I the Integral constant
     * @param D the Derivative constant
     * @param F the Velocity Feedforward constant
    */
    public void initEncoder(double P, double I, double D, double F) {
        talon.configFactoryDefault();
        talon.configSelectedFeedbackSensor(FeedbackDevice.Analog, 0, 0);
        talon.configMotionCruiseVelocity((int)Constants.WRIST_MAX_VELOCITY);
        talon.configMotionAcceleration((int)Constants.WRIST_MAX_VELOCITY); //accelerate to max speed in a second
        talon.configNominalOutputForward(0);
        talon.configNominalOutputReverse(0);
        talon.configPeakOutputForward(1);
        talon.configPeakOutputReverse(-1);
        talon.config_kP(0, P);
        
        talon.config_kI(0, I);
        talon.config_kD(0, D);
        talon.config_kF(0, F);
        talon.configMotionSCurveStrength(2);
    }

    
    /**
     * Returns the encoder position in the context of the type of talon that this is.
     * DRIVETRAIN -- Converts distance travelled to inches
     * SHOULDER -- Converts absolute encoder position to the angle (in radians) of the arm
     * WRIST -- Converts absolute encoder position to angle (in radians) of the wrist
     * CLIMBER -- Converts encoder position to angle (in radians) of the climber, with its starting angle being 0.
     * @return one of the above values, depending on what this.type was set to. 
     */
    public double getEncoderPositionContextual() {
        /*
        The angle calculations can be explained by:
        getEncoderPosition() -- Start with position
        % TICKS_PER_ROTATION -- normalize the value to the range (-TICKS_PER_ROTATION, TICKS_PER_ROTATION)
        - ZERO_VALUE         -- contextualize values based on some zero value. This contextualizes values based on this zero.
        / TICKS_PER_ROTATION -- normalizes values based on percentage of a rotation they are from the zero
        * 2pi (τ)            -- converts the values from "percentage of a rotation" to radians
        
        */
        return ((getEncoderPosition() % Constants.WRIST_TICKS_PER_ROTATION - Constants.WRIST_ENCU_ZERO)
                / Constants.WRIST_TICKS_PER_ROTATION * Math.PI * 2)%(2*Math.PI);
    }

}
