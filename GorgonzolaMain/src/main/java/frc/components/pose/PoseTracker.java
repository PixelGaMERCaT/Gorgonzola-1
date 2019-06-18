package frc.components.pose;

import java.util.Arrays;

import frc.components.Component;
import frc.components.Drivetrain;
import frc.components.Gyro;
import frc.robot.Globals;

/**
* Keeps a log of poses from previous timestamps, can be thought of as a record of past robot positions.
* acts as a stack (LIFO with self-overwriting).
* @author Jeff
*/
public class PoseTracker implements Component {
    private Gyro gyro;
    private Drivetrain drivetrain;

    private Pose[] poses;
    private int size; //the size of the buffer of poses
    private int oldestPoseIndex; //the index of the oldest Pose (also <the index of the most recent pose>+1)
    private long tickTime; //The number of nanoseconds between ticks (predicted)

    /**
     * Constructor for a PoseTracker. Assumes 50 poses stored per second
     * @param size the number of Poses it should store (at a rate of 50 poses per second)
     */
    public PoseTracker(int size) {
        oldestPoseIndex = 0;
        this.size = size;
        this.tickTime = 20000000; //50 ticks per second
        poses = new Pose[size];
    }

    /**
     * Constructor for a PoseTracker
     * @param size the number of Poses it should store
     * @param tickTime the number of nanoSeconds between ticks. For robots, that's roughly 20000000.
     */
    public PoseTracker(int size, long tickTime) {
        oldestPoseIndex = 0;
        this.size = size;
        this.tickTime = tickTime;
        poses = new Pose[size];
    }

    public void init() {
        gyro = Globals.gyro;
        drivetrain = Globals.drivetrain;
        Arrays.fill(poses, new Pose(System.nanoTime(), gyro.getYaw(), drivetrain.getLeftPosition(),
                drivetrain.getRightPosition()));
        oldestPoseIndex++;
    }

    /**
     * Adds a new pose to the top of the stack
     */
    public void tick() {
        poses[oldestPoseIndex] = new Pose(System.nanoTime(), gyro.getYaw(), drivetrain.getLeftPosition(),
                drivetrain.getRightPosition());

        //Take the weighted average of the past average tickTime and the most recent tickTime
        tickTime = (tickTime * (size - 1) + (getFromIndex(-1).timestamp - getFromIndex(-2).timestamp)) / size;
        oldestPoseIndex++;
        oldestPoseIndex %= size;

    }

    /**
     * Gets the closest Pose to a certain timestamp (in nanoseconds)
     * @param timestamp The timestamp of the desired pose (in nanoseconds)
     * @return The pose closest to that timestamp
     */
    public Pose get(long timestamp) {
        Pose mostRecentPose = getFromIndex(-1);
        long timeChange = mostRecentPose.timestamp - timestamp;
        if (timeChange > tickTime * size) {
            //if it's too far back, return the farthest back we can get
            System.err.println("Requested pose out of bounds (too far back). Returning oldest pose");
            return poses[oldestPoseIndex];
        } else if (timeChange < 0) {
            //if it's too far forward, return the most recent Pose
            System.err.println("Requested pose out of bounds (too far forward). Returning most recent pose.");
            return mostRecentPose;
        }

        //Get the best guess we can based on how long ago the timestamp was
        int guessIndex = -1 * (int) (timeChange / tickTime) - 1;

        //Determine which way we need to move from our guess to find the right timestamp
        int direction = findBestDirection(guessIndex, timestamp);

        if (direction != 0) {
            do {
                // Move in that direction until we've found the best Pose
                guessIndex += direction;
            } while (compare(guessIndex, guessIndex + direction, timestamp));
        }
        return getFromIndex(guessIndex);
        
    }

    /**
     * Compares two indeces within the stack; returns true if the second is closer to a given timestamp than the first
     * @param firstIndex The first index to compare
     * @param secondIndex The second index to compare
     * @param timestamp The timestamp to check against
     * @return true if the secondIndex is closer, false otherwise.
     */
    private boolean compare(int firstIndex, int secondIndex, long timestamp) {
        return Math.abs(getFromIndex(firstIndex).timestamp - timestamp) > Math
                .abs(getFromIndex(secondIndex).timestamp - timestamp);

    }

    /**
     * For finding based on timestamp, finds the direction to travel to get the closet pose to a timestamp
     * @param guessIndex the index from which the best direction will be determined
     * @param timestamp the timestamp we are trying to find the direction towards
     * @return -1 if the correct direction is the negative, 1 if the correct direction is positive, 0 if guessIndex is the best.
     */
    private int findBestDirection(int guessIndex, long timestamp) {
        Pose guessPose = getFromIndex(guessIndex);

        if (Math.abs(guessPose.timestamp - timestamp) > Math.abs(getFromIndex(guessIndex - 1).timestamp - timestamp)) {
            if (Math.abs(getFromIndex(guessIndex - 1).timestamp - timestamp) > Math
                    .abs(getFromIndex(guessIndex + 1).timestamp - timestamp)) {
                return 1;
            }
            return -1;
        }
        if (Math.abs(guessPose.timestamp - timestamp) > Math.abs(getFromIndex(guessIndex + 1).timestamp - timestamp)) {
            return 1;
        }
        return 0;
    }

    /**
     * Returns the Pose at a given index, relative to the end of the stack. Accepts negative indeces:
     * EX: getFromIndex(0) returns the oldest pose, getFromIndex(-1) returns the newest pose,
     * and getFromIndex(1) and getFromIndex(-2) return the second oldest and second newest poses respectively. 
     * @param location the index to get
     * @return the Pose at that index
     */

    private Pose getFromIndex(int location) {
        location = (location % size + oldestPoseIndex + size) % size;
        return poses[location];
    }
}