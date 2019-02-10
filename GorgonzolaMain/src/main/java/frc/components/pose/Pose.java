package frc.components.pose;

public class Pose {
    /**
     * An object that stores data about the robot at a certain timestamp
     */
    public long time;
    public double heading, encoderLeft, encoderRight; // Information to be stored
    public Pose(long time, double heading, double encoderLeft, double encoderRight){
        this.time=time;
        this.heading=heading;
        this.encoderLeft=encoderLeft;
        this.encoderRight=encoderRight;
    }
}