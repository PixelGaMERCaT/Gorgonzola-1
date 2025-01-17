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
 * Add your docs here.
 */
public class DriveStraight extends SandstormSection {
    double forward;
    Drivetrain drive;
    PIDController turnController;
    Gyro gyro;

    public DriveStraight(double forward, double time) {
        this.forward = forward;
        this.duration = time;
        drive = Globals.drivetrain;
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

    @Override public void init() {
        super.init();
        turnController.setSetpoint(0);
        turnController.enable();
    }

    @Override public void end() {
        turnController.disable();
    }

    @Override public void update() {
        drive.autoDrive(forward, turnController.get());
    }

}
