package frc.components.pose;

/**
* An object that stores data about the robot's state at a certain timestamp
* @author Jeff
 */ 
public class Pose {

    public long timestamp; //The time the Pose was recorded
    public double heading, encoderLeft, encoderRight; // Information to be stored

    public Pose(long timestamp, double heading, double encoderDistanceLeft, double encoderDistanceRight) {
        this.timestamp = timestamp;
        this.heading = heading;
        this.encoderLeft = encoderDistanceLeft;
        this.encoderRight = encoderDistanceRight;
    }
}