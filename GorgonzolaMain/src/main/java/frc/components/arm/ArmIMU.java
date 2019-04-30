/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.components.arm;

import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU.CalibrationMode;

import frc.components.Component;
import frc.components.Gyro;
import frc.components.InputManager;
import frc.robot.Constants;
import frc.robot.Globals;

/**
 * Add your docs here.
 */
public class ArmIMU implements Component {
    private PigeonIMU pidgey;
    private Shoulder shoulder;
    private InputManager im;
    public boolean imuOverride = false;
    private Gyro gyro;

    public ArmIMU() {

    }
    public void init() {
        shoulder=Globals.shoulder;
        im=Globals.im;
        gyro=Globals.gyro;
        pidgey = new PigeonIMU(Globals.climber.talon2.talon);
        pidgey.enterCalibrationMode(CalibrationMode.Temperature);
    }
    public void tick(){
        imuOverride = im.getIMUOverride() ? !imuOverride : imuOverride;
    }

    public double[] getPidgeyAngles() {
        double[] imuValues = new double[3];
        pidgey.getYawPitchRoll(imuValues);
        // SmartDashboard.putNumber("imuyaw", imuValues[0]);
        // SmartDashboard.putNumber("imupitch", imuValues[1]);
        // SmartDashboard.putNumber("imuroll", imuValues[2]);
        return imuValues;
    }

    public double getArmAngle() {
        return getPidgeyAngles()[2]-Constants.ARM_IMU_OFFSET;
    }

    public boolean isCalibrating() {
        double[] imuValues = getPidgeyAngles();
        return imuValues[0] == 0.0 && imuValues[1] == 0.0 && imuValues[2] == 0.0;
    }

    public boolean shouldIMUSave(){
        double encoderAngle=shoulder.getAngle();
        if (imuOverride || isCalibrating()){
            return false;
        }
        return Math.abs(Math.toDegrees(encoderAngle)-getArmAngle()-gyro.getNormalizedPitch())>Constants.ARM_EMERGENCY_THRESHOLD;
    }
}