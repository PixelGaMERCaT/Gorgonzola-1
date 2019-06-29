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
import frc.sandstorm.SandstormSection;
import frc.robot.Globals;

/**
 * A SandstormSection that drives forward while maintaining a certain yaw angle
 * @author Jeff
 */ 
public class DriveStraight extends SandstormSection {
    private Drivetrain drivetrain;
    private PIDController turnController;
    private Gyro gyro;

    private double forward; //constant forward power
    private double targetYaw;

    /**
     * Constructs a DriveStraight object with the given parameters.
     * @param forward a constant forward power to be applied every tick for the duration of this section.
     * @param targetYaw the desired angle, in degrees, for the robot to travel at during this section.
     * @param time the amount of time this section should last.
     */

    public DriveStraight(double forward, double targetYaw, double time) {
        this.forward = forward;
        this.targetYaw = targetYaw;
        this.duration = time;
        drivetrain = Globals.drivetrain;
        gyro = Globals.gyro;
        turnController = new PIDController(Constants.TURN_KP, Constants.TURN_KI, Constants.TURN_KD, gyro, o -> {
        });
        turnController.setAbsoluteTolerance(0);
        turnController.setInputRange(-180, 180);
        turnController.setOutputRange(-1, 1);
        turnController.setContinuous(true);
        turnController.setInputRange(-180, 180);
        turnController.setOutputRange(-1, 1);
        turnController.setContinuous(true);
    }

    @Override
    public void init() {
        super.init();
        turnController.setSetpoint(targetYaw);
        turnController.enable();
    }

    @Override
    public void end() {
        turnController.disable();
    }

    @Override
    public void tick() {
        drivetrain.autoDrive(forward, turnController.get());
    }

}
