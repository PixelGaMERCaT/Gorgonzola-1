package frc.components;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.components.pose.PoseTracker;
import frc.robot.Globals;
import frc.sandstorm.SandstormPath;
import frc.sandstorm.sections.MotionProfile;
import jaci.pathfinder.Waypoint;

public class CameraManager implements Component {
    private NetworkInterface cameraDataTable, robotDataTable;
    private SandstormPath mpTestAuto;
    private Gyro gyro;
    private PoseTracker poseTracker;

    public CameraManager() {
        cameraDataTable = new NetworkInterface("CameraData");

        robotDataTable = Globals.robotDataTable;
    }

    public void init() {
        gyro = Globals.gyro;
        poseTracker = Globals.poseTracker;
    }

    public void initPath() {

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

    }

    public CameraTargetType getPrimarySightingIdentity() {
        return CameraTargetType.PLAYER_STATION;
    }


    /**
     * Uses posetracker to get the angle of the primary sighting relative to the robot when the frame was taken.
     * @return the angle in degrees
     */
    public double getPrimarySightingAnglePose() throws Exception {
        long timestamp = getPrimarySightingTimestamp();
        double sightingAngle = getPrimarySightingAngle();
        double robotPastHeading = gyro.getNormalizedYaw();//poseTracker.get(timestamp).heading;
        SmartDashboard.putNumber("camera2Angle",poseTracker.get(timestamp).heading+sightingAngle);
        System.out.println("st"+timestamp);
        System.out.println("pt"+System.nanoTime());//poseTracker.get(timestamp).time);
        SmartDashboard.putNumber("timestampdiff", ((double)(timestamp-System.nanoTime()))*Math.pow(10.0,-9));
        //System.out.println("gyro pose: " +robotPastHeading+ "\n"+"sightingPose: "+(robotPastHeading+sightingAngle));
        return robotPastHeading + sightingAngle;
    }

    public int getPrimarySighting() throws Exception {
        //return (int) (cameraDataTable.get("PrimarySighting").getDouble());
        return 0;
    }

    public long getPrimarySightingTimestamp() throws Exception {
        try {
            return Long.parseLong(cameraDataTable.get("Timestamp" + getPrimarySighting()).getString());
        } catch (Exception e) {
            //e.printStackTrace();
            return Long.parseLong(cameraDataTable.get("Timestamp0").getString());

        }
    }

    public double getPrimarySightingAngle() throws Exception {
        try {
            return cameraDataTable.get("Sighting " + getPrimarySighting()).getDoubleArray()[1];
        } catch (Exception e) {
            // e.printStackTrace();
            return cameraDataTable.get("Sighting 0").getDoubleArray()[1];

        }
    }

    public double getPrimarySightingDistance() throws Exception {
        try {
            return cameraDataTable.get("Sighting " + getPrimarySighting()).getDoubleArray()[0];
        } catch (Exception e) {
            e.printStackTrace();
            return cameraDataTable.get("Sighting 0").getDoubleArray()[0];

        }
    }
}