package frc.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SPI;

public class Gyro implements Component, PIDSource {
    private AHRS navx;
    
    public Gyro() {
    }

    public void init() {
        try {
            navx = new AHRS(SPI.Port.kMXP);
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("problem in initialization of navx");
        }
        System.out.println("init Navx");
        //navx.zeroYaw();
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
        double yaw =0;
        try {
        yaw= navx.getYaw();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Problem in getNormalizedYaw");
        }
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