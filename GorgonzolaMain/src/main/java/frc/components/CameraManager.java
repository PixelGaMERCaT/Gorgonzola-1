package frc.components;

import frc.robot.Globals;
import frc.sandstorm.SandstormPath;
import frc.sandstorm.sections.MotionProfile;
import jaci.pathfinder.Waypoint;

public class CameraManager implements Component {
    NetworkInterface cameraDataTable, robotDataTable;
    SandstormPath mpTestAuto;
    Gyro gyro;

    public CameraManager() {
        cameraDataTable = new NetworkInterface("CameraData");
        robotDataTable = Globals.robotDataTable;
    }
    public void init(){
        gyro = Globals.gyro;
    }
    public void initPath() {
        mpTestAuto = new SandstormPath("Camera Automove");
        try {
            double distance = getPrimarySightingDistance();
            double angle = Math.toRadians(getPrimarySightingAngle());
            CameraTargetType targetType = getPrimarySightingIdentity();
            mpTestAuto.add(
                    new MotionProfile(new Waypoint[] { 
                        new Waypoint(0, 0, gyro.getNormalizedYaw()), 
                        new Waypoint(distance * Math.cos(angle),
                            distance * Math.sin(angle), targetType.getTargetAngle()) }));
            robotDataTable.setBoolean("CameraErrorFlag", false);
        } catch (Exception e) {
            e.printStackTrace();
            robotDataTable.setBoolean("CameraErrorFlag", true);
        }

    }

    public CameraTargetType getPrimarySightingIdentity() {
        return CameraTargetType.PLAYER_STATION;
    }

    public double getPrimarySightingAngle() throws Exception {
        return cameraDataTable.get("Sighting0").getDoubleArray()[1];
    }

    public double getPrimarySightingDistance() throws Exception {
        return cameraDataTable.get("Sighting0").getDoubleArray()[1];
    }
}