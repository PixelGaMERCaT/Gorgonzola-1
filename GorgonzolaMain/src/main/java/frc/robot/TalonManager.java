/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 * Wraps and sets up Talons and encoders
 */
public class TalonManager {
  public TalonSRX talon;

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
  public void initEncoders(double P, double I, double D, double F) {

    talon.configNominalOutputForward(0);
    talon.configNominalOutputReverse(0);
    talon.configPeakOutputForward(1);
    talon.configPeakOutputReverse(-1);
    talon.configNominalOutputForward(0);
    talon.configNominalOutputReverse(0);
    talon.configPeakOutputForward(1);
    talon.configPeakOutputReverse(-1);

    talon.config_kP(0, P);
    talon.config_kI(0, I);
    talon.config_kD(0, D);
    talon.config_kF(0, F);

  }
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
  public int getEncoderVelocity() {
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
