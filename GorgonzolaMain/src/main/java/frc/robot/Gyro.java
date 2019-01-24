package frc.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SerialPort;

public class Gyro implements Component, PIDSource {
    private AHRS navx;
    
    public Gyro() {
        navx = new AHRS(SerialPort.Port.kMXP);
    }

    public void init() {
        navx.zeroYaw();
    }

    /**
     * A method which returns the relative yaw (turn angle) of the robot
     * 
     * @return the yaw of the robot relative to its starting position
     */
    public double getYaw() {
        return navx.getYaw();
    }

    /**
     * A method which returns the relative yaw as a double between -180 and 180
     * 
     * @return the normalized yaw of the robot
     */
    public double getNormalizedYaw() {
        double yaw = navx.getYaw();
        yaw = (yaw + 360) % 360;
        return Math.abs(yaw) <= 180 ? yaw : Math.signum(yaw) * -360 + yaw;
    }

    /**
     * Should do nothing; this is always a kDisplacement source
     */
    public void setPIDSourceType(PIDSourceType pidSource) {
    }

    /**
     * Returns the PIDSourceType of this Gyro
     * @return always PIDSourceType.kDisplacement 
     */
    public PIDSourceType getPIDSourceType() {
        return PIDSourceType.kDisplacement;
    }

    /**
     * returns the current normalized relative yaw for use in PID Control loops
     * @return the normalized relative yaw, according to the gyro
     */
    public double pidGet() {
        return getNormalizedYaw();
    }
}