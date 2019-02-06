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
  public void resetEncoder(){
    talon.setSelectedSensorPosition(0, 0, 0);

  }
  public void setInverted(boolean inverted) {
    talon.setInverted(inverted);
  }
  public int getEncoderPosition(){
    return talon.getSelectedSensorPosition(0);
  }
  public int getEncoderVelocity(){
    return talon.getSelectedSensorVelocity(0);
  }
  public double getOutputCurrent(){
    return talon.getOutputCurrent();
  }
  public void follow(TalonManager othercontroller) {
    talon.follow(othercontroller.talon);
  }

  public void set(ControlMode mode, double power) {
    talon.set(mode, power);
  }

}
