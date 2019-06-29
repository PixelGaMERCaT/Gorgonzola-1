package frc.components;

import java.awt.Color;

import frc.components.lights.LightController;
import frc.robot.Globals;
import frc.sandstorm.SandstormPath;
import frc.sandstorm.sections.MotionProfile;
import jaci.pathfinder.Waypoint;

/**
 * Manages camera data sent from Raspberry Pi over NetworkTables.
 * @author Jeff
 */ 
public class CameraManager implements Component {
    private NetworkInterface cameraDataTable, robotDataTable;
    private SandstormPath mpTestAuto;
    private Gyro gyro;
    LightController lightController;

    public CameraManager() {
        cameraDataTable = new NetworkInterface("CameraData");
        robotDataTable = Globals.robotDataTable;
    }

    public void tick() {
        //Turn the arm green if we have a sighting, red otherwise.
        try {
            if (getSightingCount() > 0) {
                lightController.setArmColor(Color.GREEN);
            } else {
                lightController.setArmColor(Color.RED);
            }
        } catch (Exception e) {
            lightController.setArmColor(Color.RED);
        }

    }

    public void init() {
        gyro = Globals.gyro;
        lightController = Globals.lightController;
    }

    /**
     * Gets the angle of a given sighting taking into account the gyro's current angle
     * assuming no latency has ocurred
     * @return the angle in degrees, RELATIVE TO THE FIELD, of the target from the robot.
     */
    public double getPrimarySightingAngleAbsolute() throws Exception {
        double sightingAngle = getPrimarySightingAngle();
        double robotHeading = gyro.getNormalizedYaw();
        return robotHeading + sightingAngle;
    }

    /**
     * Gives the timestamp that the primary sighting was taken at.
     * @return the timestamp, in nanoseconds of raspberry pi time.
     * @throws Exception if there are no sightings
     */
    public long getPrimarySightingTimestamp() throws Exception {
        return Long.parseLong(cameraDataTable.getString("Timestamp0"));
    }

    /**
     * Gets the angle of the primary sighting returned by the pi, 
     * relative to the front of the robot, not taking into account the gyro.
     * @return the angle (in degrees)
     * @throws Exception if there are no sightings
     */
    public double getPrimarySightingAngle() throws Exception {
        return cameraDataTable.getDoubleArray("Sighting 0")[1];
    }

    /**
     * Gets the distance of the primary sighting returned by the pi.
     * @return the distance (in inches)
     * @throws Exception if there are no sightings
     */
    public double getPrimarySightingDistance() throws Exception {
        return cameraDataTable.getDoubleArray("Sighting 0")[0];
    }

    /**
     * Returns the number of sightings the pi has returned
     * @return the number of sightings
     */
    public double getSightingCount() {
        try {
            return cameraDataTable.getDouble("numberOfEntries");
        } catch (Exception e) {
            return 0;
        }
    }

    /**                                   UNUSED METHOD 
    * Inits and returns a new Motion Profile path that would move the robot to the given vision target.
    */
    public SandstormPath initPath() {
        mpTestAuto = new SandstormPath("Camera Automove");
        try {
            double distance = getPrimarySightingDistance();
            double angle = Math.toRadians(getPrimarySightingAngle());
            CameraTargetType targetType = getPrimarySightingIdentity();
            mpTestAuto.add(new MotionProfile(new Waypoint[] { new Waypoint(0, 0, gyro.getNormalizedYaw()), new Waypoint(
                    distance * Math.cos(angle), distance * Math.sin(angle), targetType.getTargetAngle()) }));
            robotDataTable.setBoolean("CameraErrorFlag", false);
        } catch (Exception e) {
            e.printStackTrace();
            robotDataTable.setBoolean("CameraErrorFlag", true);
        }
        return mpTestAuto;

    }

    /**                    UNUSED METHOD
     * Returns the target type (See CameraTargetType) of the primary sighting
     */
    public CameraTargetType getPrimarySightingIdentity() {
        return CameraTargetType.PLAYER_STATION;
    }

}