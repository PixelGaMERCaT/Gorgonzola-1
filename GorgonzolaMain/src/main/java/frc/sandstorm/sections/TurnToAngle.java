/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.sandstorm.sections;

import edu.wpi.first.wpilibj.PIDController;
import frc.components.Drivetrain;
import frc.components.Gyro;
import frc.robot.Constants;
import frc.robot.Globals;
import frc.sandstorm.SandstormSection;

/**
 * A SandstormSection allowing the robot to turn to a given angle using a PID loop.
 */
public class TurnToAngle extends SandstormSection {
    private double angle;
    private Drivetrain drivetrain;
    private PIDController turnController;
    private Gyro gyro;

    /**
     * Constructs a TurnToAngle object with the given parameters.
     * @param targetYaw the desired angle, in degrees, for the robot to turn to
     * @param time the amount of time this section should last.
     */

    public TurnToAngle(double targetYaw, double time) {
        this.angle = targetYaw;
        this.duration = time;
        drivetrain = Globals.drivetrain;
        gyro = Globals.gyro;
        turnController = new PIDController(Constants.TURN_KP, Constants.TURN_KI, Constants.TURN_KD, gyro, o -> {
        });
        turnController.setAbsoluteTolerance(1);
        turnController.setInputRange(-180, 180);
        turnController.setOutputRange(-1, 1);
        turnController.setContinuous(true);
        turnController.setInputRange(-180, 180);
        turnController.setOutputRange(-1, 1);
        turnController.setContinuous(true);
    }

    public void init() {
        super.init();
        turnController.setSetpoint(this.angle);
        turnController.enable();
    }

    @Override
    public void end() {
        turnController.disable();
    }

    public boolean customFinish() {
        return turnController.onTarget();
    }

    @Override
    public void tick() {
        drivetrain.autoDrive(0, turnController.get());
    }

}
