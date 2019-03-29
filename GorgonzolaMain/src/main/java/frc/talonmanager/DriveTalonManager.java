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
public class DriveTalonManager extends TalonManager {

    /**
     * Initializes the Talon
     * @param idx The index of the talon
     */
    public DriveTalonManager(int idx) {
        super(idx);
        this.type = TalonType.DRIVETRAIN;
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
        talon.configMotionAcceleration((int) (Constants.MAX_DRIVE_VELOCITY_LOW * Constants.DRIVE_ENCU_PER_INCH / 10.0));
        talon.configMotionCruiseVelocity(
                (int) (Constants.MAX_DRIVE_VELOCITY_LOW * Constants.DRIVE_ENCU_PER_INCH / 10.0));
        talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
        talon.setSensorPhase(true);
        talon.configNominalOutputForward(0);
        talon.configNominalOutputReverse(0);
        talon.configPeakOutputForward(1);
        talon.configPeakOutputReverse(-1);
        talon.config_kP(0, P);
        talon.config_kI(0, I);
        talon.config_kD(0, D);
        talon.config_kF(0, F);
        resetEncoder();
    }

    /**
     * Returns the encoder position in the context of the type of talon that this is.
     * Converts distance travelled to inches
     * @return This encoder's signed movement so far, in inches.
     */
    public double getEncoderPositionContextual() {

        return getEncoderPosition() / Constants.DRIVE_ENCU_PER_INCH;

    }

    
    /**
     * Returns the velocity of this Talon's encoder in inches
     * @return the velocity of this Talon's encoder
     */
    public double getEncoderVelocityContextual() {
        return (((double)talon.getSelectedSensorVelocity(0))/Constants.DRIVE_ENCU_PER_INCH)*10.0;

    }
}
