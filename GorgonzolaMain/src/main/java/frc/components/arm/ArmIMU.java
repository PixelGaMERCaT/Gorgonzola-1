
package frc.components.arm;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU.CalibrationMode;

import frc.components.Component;
import frc.components.Gyro;
import frc.components.InputManager;
import frc.robot.Constants;
import frc.robot.Globals;
import frc.robot.RobotMap;

/**
 *                    U N U S E D   F I L E 
 * Defines a gyro (IMU) mounted on the arm of the robot. 
 * Originally intended to check and improve readings from the encoder
 * REMOVED FROM CODE DUE TO LACK OF RELIABILITY AND DIFFICULTY OF MOUNTING
 * 
 */
@Deprecated
public class ArmIMU implements Component {
    private PigeonIMU pidgey;
    private Shoulder shoulder;
    private InputManager inputManager;
    public boolean imuOverride = false;  
    private Gyro gyro;
    private TalonSRX connectedTalon; //TalonSRX the Pidgey was attached to.
    public void init() {
        shoulder = Globals.shoulder;
        inputManager = Globals.inputManager;
        gyro = Globals.gyro;
        connectedTalon=new TalonSRX(RobotMap.ARM_IMU_TALON);
        pidgey = new PigeonIMU(connectedTalon); 
        pidgey.enterCalibrationMode(CalibrationMode.Temperature);
    }

    public void tick() {
        imuOverride = inputManager.getShoulderSaveOverride() ? !imuOverride : imuOverride;
    }
    /**
     * Gives the direct output of the PigeonIMU
     * @return An array of size 3 containing the degree-rotation of the pidgey in the three rotational axes
     */
    public double[] getPidgeyAngles() {
        double[] imuValues = new double[3];
        pidgey.getYawPitchRoll(imuValues);
        return imuValues;
    }
    /**
     * Returns the angle of the arm, in degrees
     * @return the angle, with 0 being horizontal and upwards being positive
     */
    public double getArmAngle() {
        return getPidgeyAngles()[2] - Constants.ARM_IMU_OFFSET;
    }
    /**
     * Returns whether the IMU is in calibration mode (meaning the values will be useless)
     * @return true if the IMU is calibrating, false otherwise.
     */
    public boolean isCalibrating() {
        double[] imuValues = getPidgeyAngles();
        return imuValues[0] == 0.0 && imuValues[1] == 0.0 && imuValues[2] == 0.0;
    }
    /**
     * Returns whether the robot program should halt the movement of the arm this tick.
     * @return true if the robot should save itself, false otherwise
     */
    public boolean shouldIMUSave() {
        double encoderAngle = shoulder.getAngle();
        if (imuOverride || isCalibrating()) { 
            //If the readings are bad or the driver has overriden the feature, return false
            return false;
        }
        return Math.abs(Math.toDegrees(encoderAngle) - getArmAngle()
                - gyro.getNormalizedPitch()) > Constants.ARM_EMERGENCY_THRESHOLD;
    }
}
