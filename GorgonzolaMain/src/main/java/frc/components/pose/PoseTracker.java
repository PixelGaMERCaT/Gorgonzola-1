package frc.components.pose;

import java.util.Arrays;

import frc.components.Component;
import frc.components.Drivetrain;
import frc.components.Gyro;
import frc.robot.Globals;

/**
* Keeps a log of poses from previous timestamps, acts as a stack (LIFO with self-overwriting)
* @author Grant Matteo
*/
public class PoseTracker implements Component {
    public Pose[] poses;
    public int size; //the size of the buffer of poses
    private int index; //the index of the oldest Pose (also <the index of the most recent pose>+1)
    private Gyro gyro;

    private Drivetrain drivetrain;
    public static long tickTime = 20000000; //The number of nanoseconds between ticks (estimated)

    /**
     * Constructor for a Posetracker
     * @param size the number of Poses it should store
     */
    public PoseTracker(int size) {
        index = 0;
        this.size = size;
        poses = new Pose[size];
    }

    public void init() {
        //gyro = Globals.gyro;
        drivetrain = Globals.drivetrain;
        Arrays.fill(poses, new Pose(System.nanoTime(), gyro.getYaw(), drivetrain.getLeftPosition(),
                drivetrain.getRightPosition()));
        index++;
    }
    /**
     * Adds a new pose to the top of the stack
     */
    public void tick() {
        poses[index] = new Pose(System.nanoTime(), gyro.getYaw(), drivetrain.getLeftPosition(),
                drivetrain.getRightPosition());

        //Take the weighted average of the past average tickTime and the most recent tickTime
        tickTime = (tickTime * (size - 1) + (getFromIndex(-1).time - getFromIndex(-2).time)) / size;
        index++;
        index %= size;

    }

    /**
     * Gets the closest Pose to a certain timestamp (in nanoseconds)
     * @param timestamp The timestamp of the desired pose (in nanoseconds)
     * @return The pose closest to that timestamp
     */
    public Pose get(long timestamp) {
        Pose mostRecentPose = getFromIndex(-1);
        long timeChange = mostRecentPose.time - timestamp;
        if (timeChange > tickTime * size) {
            //if it's too far back, return the farthest back we can get
            System.err.println("Requested pose out of bounds (too far back). Returning oldest pose");
            return poses[index];
        } else if (timeChange < 0) {
            //if it's too far forward, return the most recent Pose
            System.err.println("Requested pose out of bounds (too far forward). Returning most recent pose.");
            return mostRecentPose;
        }

        //Get the best guess we can based on how long ago the timestamp was
        int guessIndex = -1 * (int) (timeChange / tickTime) - 1;

        int direction = findBestDirection(guessIndex, timestamp);
        if (direction != 0) {
            do {
                guessIndex += direction;
            } while (compare(guessIndex, guessIndex + direction, timestamp));
        }
        Pose closestPose = getFromIndex(guessIndex);
        System.out.println(closestPose.time - timestamp);
        return closestPose;
    }

    /**
     * Compares two indeces within the stack; returns true if the second is closer to a given timestamp than the first
     * @param firstIndex The first index to compare
     * @param secondIndex The second index to compare
     * @param timestamp The timestamp to check against
     * @return true if the secondIndex is closer, false otherwise.
     */
    private boolean compare(int firstIndex, int secondIndex, long timestamp) {
        return Math.abs(getFromIndex(firstIndex).time - timestamp) > Math
                .abs(getFromIndex(secondIndex).time - timestamp);

    }

    /**
     * For finding based on timestamp, finds the direction to travel to get the closet pose to a timestamp
     * @param guessIndex the index from which the best direction will be determined
     * @param timestamp the timestamp we are trying to find the direction towards
     * @return -1 if the correct direction is the negative, 1 if the correct direction is positive, 0 if guessIndex is the best.
     */
    private int findBestDirection(int guessIndex, long timestamp) {
        Pose guessPose = getFromIndex(guessIndex);

        if (Math.abs(guessPose.time - timestamp) > Math.abs(getFromIndex(guessIndex - 1).time - timestamp)) {
            if (Math.abs(getFromIndex(guessIndex - 1).time - timestamp) > Math
                    .abs(getFromIndex(guessIndex + 1).time - timestamp)) {
                return 1;
            }
            return -1;
        }
        if (Math.abs(guessPose.time - timestamp) > Math.abs(getFromIndex(guessIndex + 1).time - timestamp)) {
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

        location = (location % size + index + size) % size;
        return poses[location];
    }
}