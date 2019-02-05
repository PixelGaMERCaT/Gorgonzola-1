package frc.pose;

import java.util.Arrays;

import frc.robot.Component;
import frc.robot.Drivetrain;
import frc.robot.Globals;
import frc.robot.Gyro;

/**
* Keeps a log of poses from previous timestamps
*/

public class PoseTracker implements Component {
    public Pose[] poses;
    public int size; //the size of the buffer of poses
    private int index;
    private Gyro gyro;
    private Drivetrain drivetrain;

    public PoseTracker(int size) {
        index = 0;
        this.size = size;
        poses = new Pose[size];
    }

    public void init() {
        gyro = Globals.gyro;
        drivetrain = Globals.drivetrain;
        Arrays.fill(poses, new Pose(System.nanoTime(), gyro.getYaw(), drivetrain.getLeftPosition(),
                drivetrain.getRightPosition()));
        index++;
    }

    public void tick() {
        poses[index] = new Pose(System.nanoTime(), gyro.getYaw(), drivetrain.getLeftPosition(),
                drivetrain.getRightPosition());

        index++;
        index %= size;

    }

    public Pose get(long timestamp) {
        Pose closestPose = getFromIndex(-1);
        int change=size/2;
        for (int i = -2; i > -size;) {
            if (getFromIndex(i).time - timestamp >= closestPose.time - timestamp) {
                return closestPose;
            }
            closestPose = getFromIndex(-1);

        }
        return closestPose;
    }

    /*
    * Returns the Pose at a given index, relative to the starting position of the pose[]. Accepts negative indexes
    */
    private Pose getFromIndex(int location) {
        location = (location + index + size) % size;
        return poses[location];
    }
}