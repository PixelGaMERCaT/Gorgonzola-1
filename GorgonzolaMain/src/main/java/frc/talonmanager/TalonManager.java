/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                                                                                                                 */
/* Open Source Software - may be modified and shared by FRC teams. The code         */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                                                                                                                                                                                                                         */
/*----------------------------------------------------------------------------*/

package frc.talonmanager;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 * Wraps and sets up Talons and encoders
 */
public abstract class TalonManager {
    public TalonSRX talon;
    protected TalonType type;

    /**
     * Initializes the Talon
     * @param idx The index of the talon
     */
    public TalonManager(int idx) {
        talon = new TalonSRX(idx);
    }

    /**
    * initializes the encoders, including setting up PIDF constants for Motion Magic
    * @param P the Proportion constant
    * @param I the Integral constant
    * @param D the Derivative constant
    * @param F the Velocity Feedforward constant
    */
    public abstract void initEncoder(double P, double I, double D, double F);


    /**
     * Sets this talon's encoder positions to zero.
     */
    public void resetEncoder() {
        talon.setSelectedSensorPosition(0, 0, 0);
    }

    /**
     * Wraps TalonSRX.setInverted(); determines whether this talon is inverted
     * @param inverted true if the talon should be inverted, false otherwise
     */
    public void setInverted(boolean inverted) {
        talon.setInverted(inverted);
    }

    /**
     * Returns the encoder position in the context of the type of talon that this is.
     * DRIVETRAIN -- Converts distance travelled to inches
     * SHOULDER -- Converts absolute encoder position to the angle (in radians) of the arm
     * WRIST -- Converts absolute encoder position to angle (in radians) of the wrist
     * CLIMBER -- Converts encoder position to angle (in radians) of the climber, with its starting angle being 0.
     * @return one of the above values, depending on what this.type was set to. 
     */
    public abstract double getEncoderPositionContextual();

    /**
     * Returns the position of this Talon's encoder
     * @return the position of this Talon's encoder
     */
    public int getEncoderPosition() {
        return talon.getSelectedSensorPosition(0);
    }

    /**
     * Returns the velocity of this Talon's encoder
     * @return the velocity of this Talon's encoder
     */
    public int  getEncoderVelocity() {
        return talon.getSelectedSensorVelocity(0);
    }  
    
    /**
     * Returns the velocity of this Talon's encoder
     * @return the velocity of this Talon's encoder
     */
    public double getEncoderVelocityContextual() {
        return talon.getSelectedSensorVelocity(0);
    } 

    /**
     * Returns the current being applied by this Talon
     * @return the current being applied by this Talon
     */
    public double getOutputCurrent() {
        return talon.getOutputCurrent();
    }

    /**
     * Makes this Talon follow another Talon
     * @param othercontroller A TalonManager to follow
     */
    public void follow(TalonManager othercontroller) {
        talon.follow(othercontroller.talon);
    }

    /**
     * Wraps TalonSRX.set(); makes the talon output to the motor
     * @param mode the ControlMode that the talon should use to interpret the power
     * @param power the power to apply. For ControlMode.PercentOutput, this should be wihtin [-1, 1]
     */
    public void set(ControlMode mode, double power) {
        talon.set(mode, power);
    }

}
