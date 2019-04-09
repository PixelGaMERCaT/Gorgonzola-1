package frc.components;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SPI;
import frc.CheeseLog.Loggable;
import frc.CheeseLog.SQLType.Decimal;
import frc.CheeseLog.SQLType.Type;
import frc.robot.Globals;

public class Gyro implements Component, PIDSource {
    private AHRS navx;
    private LogInterface logger;
    private double pitchZero;
    private NetworkInterface robotDataTable;

    public Gyro() {
        try {
            navx = new AHRS(SPI.Port.kMXP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        robotDataTable = Globals.robotDataTable;
        logger = Globals.logger;
        try {
            logger.gyro = LogInterface.table("Gyro", new String[] { "yaw", "pitch" }, new Type[] { new Decimal(), new Decimal() },
                    new Loggable[] { () -> getNormalizedYaw(), ()->getNormalizedPitch() });// getNormalizedYaw() });

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("problem in initialization of navx");
        }
        zeroYaw();
        if (Globals.isNSP) {
            pitchZero = navx.getRoll();
        } else {
            pitchZero = navx.getPitch();
        }
    }
    public void zeroYaw(){
        navx.zeroYaw();
    }
    public void tick() {
        robotDataTable.setDouble("yaw", getNormalizedYaw());
        //SmartDashboard.putNumber("navxyaw",getNormalizedYaw());
        robotDataTable.setDouble("pitch", getNormalizedPitch()); //Tilt forward and back
        robotDataTable.setDouble("roll", navx.getRoll());
        //SmartDashboard.putNumber("pitch", getNormalizedPitch());
    }

    /**
     * A method which returns the relative yaw (turn angle) of the robot
     * 
     * @return the yaw of the robot relative to its starting position
     */
    public double getYaw() {
        return Globals.isProto ? -navx.getYaw() : -navx.getYaw();
    }
    

    /**
     * A method which returns the relative yaw as a double between -180 and 180
     * 
     * @return the normalized yaw of the robot
     */
    public double getNormalizedYaw() {
        double yaw = 0;
        try {
            yaw = getYaw();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Problem in getNormalizedYaw");
        }
        yaw = (yaw + 360) % 360;
        return Math.abs(yaw) <= 180 ? yaw : Math.signum(yaw) * -360 + yaw;
    }

    /**
     * A method which returns a relative pitch as a double between -180 and 180
     * @return the normalized pitch of the robot
     */
    public double getNormalizedPitch() {
        double pitch = 0;
        try {
            if (Globals.isNSP) {
                pitch = navx.getRoll() - pitchZero;
            } else {
                pitch = navx.getPitch() - pitchZero;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Problem in getNormalizedPitch");
        }
        pitch = (pitch + 360) % 360;
        return Math.abs(pitch) <= 180 ? pitch : Math.signum(pitch) * -360 + pitch;

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