package frc.motionprofiling;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import frc.components.Component;
import frc.components.Drivetrain;
import frc.components.Gyro;
import frc.robot.Globals;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Config;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.modifiers.TankModifier;

public class MotionProfiler implements Component {
    private Waypoint[] path;
    private EncoderFollower leftFollower, rightFollower;
    private PIDController turnController;
    private Gyro gyro;
    private Drivetrain drivetrain;
    private double startHeading, angleError, turnOutput;
    public MotionProfiler() {
        drivetrain = Globals.drivetrain;
        gyro = Globals.gyro;
        turnController = new PIDController(MPConstants.TURN_KP, MPConstants.TURN_KI, MPConstants.TURN_KD,
                new PIDSource() {
                    @Override
                    public void setPIDSourceType(PIDSourceType pidSource) {
                    }

                    @Override
                    public PIDSourceType getPIDSourceType() {
                        return PIDSourceType.kDisplacement;
                    }

                    @Override
                    public double pidGet() {
                        return angleError;
                    }
        }, o -> {});
        turnController.setInputRange(-180, 180);
        turnController.setOutputRange(-1, 1);
        turnController.setContinuous(true);
        //A basic default path for testing.
        path = new Waypoint[] { new Waypoint(0, 0, 0), new Waypoint(39.4 * 2, 0, 0) };

    }

    /**
     * Sets the path of this motion profiler
     * @param path The path that this motion profiler should follow
     */
    public void setPath(Waypoint... path) {
        this.path = path;
    }

    public void init() {

        //Generate the trajectory the robot will be following
        Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Config.SAMPLES_FAST, .02,
                MPConstants.MAX_VELOCITY, MPConstants.MAX_ACCELERATION, MPConstants.MAX_JERK);
        Trajectory mpTrajectory = Pathfinder.generate(path, config);
        
        //Generate the trajectory the left and right treads will be following
        TankModifier modifier = new TankModifier(mpTrajectory).modify(MPConstants.WHEELBASE_WIDTH);
        leftFollower=new EncoderFollower(modifier.getLeftTrajectory());
        rightFollower=new EncoderFollower(modifier.getRightTrajectory());

        //Configure with Constants from MPConstants.java
        leftFollower.configureEncoder(drivetrain.getLeftPosition(), MPConstants.TICKS_PER_ROTATION, MPConstants.INCHES_PER_ROTATION);
        rightFollower.configureEncoder(drivetrain.getRightPosition(), MPConstants.TICKS_PER_ROTATION, MPConstants.INCHES_PER_ROTATION);
        leftFollower.configurePIDVA(MPConstants.PATH_KP, MPConstants.PATH_KI, MPConstants.PATH_KD, MPConstants.PATH_KV, MPConstants.PATH_KA);
        rightFollower.configurePIDVA(MPConstants.PATH_KP, MPConstants.PATH_KI, MPConstants.PATH_KD, MPConstants.PATH_KV, MPConstants.PATH_KA);
        turnController.enable();

    }
    public void run(){
        double leftSpeed= leftFollower.calculate(drivetrain.getLeftPosition(), drivetrain.getLeftVelocity());
        double rightSpeed= rightFollower.calculate(drivetrain.getRightPosition(), drivetrain.getRightVelocity());
        double currentHeading= gyro.getNormalizedYaw()-startHeading;
        angleError= Pathfinder.boundHalfDegrees(leftFollower.getHeading()-currentHeading);
        turnOutput=turnController.get();
        drivetrain.driveMP(leftSpeed, rightSpeed, turnOutput);
    }


    public boolean isFinished(){
        return leftFollower.isFinished() && rightFollower.isFinished();
    }

    public double getRemainingDuration(){
        return leftFollower == null || rightFollower == null ? -1 : Math.max(leftFollower.getRemainingDuration(), rightFollower.getRemainingDuration());
    }
}