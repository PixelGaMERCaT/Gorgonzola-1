/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.sandstorm.sections;

import frc.components.Drivetrain;
import frc.robot.Globals;
import frc.sandstorm.SandstormSection;
/**
 * Add your docs here.
 */
public class DriveStraight extends SandstormSection {
    double forward;
    Drivetrain drive;

    public DriveStraight(double forward, double time) {
        this.forward = forward;
        this.duration = time;
        drive = Globals.drivetrain;
    }

    public void init() {
        super.init();
        drive.turnController.setSetpoint(0);
        drive.turnController.enable();
    }

    @Override public void end() {
        drive.turnController.disable();
    }

    @Override public void update() {
        drive.autoDrive(forward, drive.turnController.get());
    }

}
