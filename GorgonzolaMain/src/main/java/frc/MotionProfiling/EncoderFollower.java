package frc.motionprofiling;

import jaci.pathfinder.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Runs PID loop on tread position for motion profiling
 * "Stolen" from Jaci's Pathfinder.
 * Modified to log progress along the way and calculate time remaining.
 * @author Jeff
 */ 
public class EncoderFollower {
    private File logFile; //logfile to write to
    private BufferedWriter logWriter; //BufferedWriter to write with
    private double kp, kd, kv, ka;

    private double last_error, heading;

    int segment;
    Trajectory trajectory;

    public EncoderFollower(Trajectory traj, String filePath) {
        this(traj);
        logFile = new File(filePath);
        try {
            logWriter = new BufferedWriter(new FileWriter(logFile));
            logWriter.write("Tick, Distance, Position, Velocity, acceleration, Enc_Velocity, Output, Yaw, turnOut, heading");
            logWriter.newLine();
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculate the desired output for the motors, based on the amount of ticks the encoder has gone through.
     * This does not account for heading of the robot. To account for heading, add some extra terms in your control
     * loop for realignment based on gyroscope input and the desired heading given by this object.
     * @param encoder_tick The amount of ticks the encoder has currently measured.
     * @param vel The current velocity of the encoder
     * @return  The desired output for your motor controller
     */
    public double calculate(int encoder_tick, double vel, double yaw, double turnOut) {
        Trajectory.Segment seg = trajectory.get(segment);
        double calculate_value = calculate(encoder_tick);
        double distance_covered = encoder_tick;//((encoder_tick - encoder_offset) / encoder_tick_count) * wheel_circumference;
        
        try {
            String line = segment + ", " + distance_covered + ", " + seg.position + ", " + seg.velocity + ", "
                    + seg.acceleration + ", " + vel + ", " + calculate_value +", "+yaw+ ", "+turnOut+", "+Pathfinder.r2d(seg.heading)+"\n";
            logWriter.write(line);
            logWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return calculate_value;
    }

    public void closeFile() {
        try {
            logWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private EncoderFollower(Trajectory traj) {
        this.trajectory = traj;
    }

    /**
     * Set a new trajectory to follow, and reset the cumulative errors and segment counts
     */
    public void setTrajectory(Trajectory traj) {
        this.trajectory = traj;
        reset();
    }

    /**
     * Configure the PID/VA Variables for the Follower
     * @param kp The proportional term. This is usually quite high (0.8 - 1.0 are common values)
     * @param ki The integral term. Currently unused.
     * @param kd The derivative term. Adjust this if you are unhappy with the tracking of the follower. 0.0 is the default
     * @param kv The velocity ratio. This should be 1 over your maximum velocity @ 100% throttle.
     *           This converts m/s given by the algorithm to a scale of -1..1 to be used by your
     *           motor controllers
     * @param ka The acceleration term. Adjust this if you want to reach higher or lower speeds faster. 0.0 is the default
     */
    public void configurePIDVA(double kp, double ki, double kd, double kv, double ka) {
        this.kp = kp;
        this.kd = kd;
        this.kv = kv;
        this.ka = ka;
    }

    /**
     * Configure the Encoders being used in the follower.
     * @param initial_position      The initial 'offset' of your encoder. This should be set to the encoder value just
     *                              before you start to track
     * @param ticks_per_revolution  How many ticks per revolution the encoder has
     * @param wheel_diameter        The diameter of your wheels (or pulleys for track systems) in meters
     */
    public void configureEncoder(int initial_position, int ticks_per_revolution, double wheel_diameter) {
    }

    /**
     * Reset the follower to start again. Encoders must be reconfigured.
     */
    public void reset() {
        last_error = 0;
        segment = 0;
    }

    /**
     * Calculate the desired output for the motors, based on the amount of ticks the encoder has gone through.
     * This does not account for heading of the robot. To account for heading, add some extra terms in your control
     * loop for realignment based on gyroscope input and the desired heading given by this object.
     * @param encoder_tick The amount of ticks the encoder has currently measured.
     * @return             The desired output for your motor controller
     */
    public double calculate(int encoder_tick) {
        double distance_covered = encoder_tick;
        if (segment < trajectory.length()) {
            Trajectory.Segment seg = trajectory.get(segment);
            double error = seg.position - distance_covered;
            double calculated_value = kp * error + // Proportional
                    kd * ((error - last_error) / seg.dt) + // Derivative
                    (kv * seg.velocity + ka * seg.acceleration); // V and A Terms
            last_error = error;
            heading = seg.heading;
            segment++;

            return calculated_value;
        } else
            return 0;
    }

    /**
     * @return the desired heading of the current point in the trajectory
     */
    public double getHeading() {
        return heading;
    }

    /**
     * @return the current segment being operated on
     */
    public Trajectory.Segment getSegment() {
        return trajectory.get(segment);
    }

    /**
     * @return whether we have finished tracking this trajectory or not.
     */
    public boolean isFinished() {
        return segment >= trajectory.length();
    }

    /**
     * @return the remaining duration in the current motion profile
     */
    public double getRemainingDuration() {
        return 1000 * (trajectory.length() - segment) * MPConstants.DELTA_TIME;
    }
}