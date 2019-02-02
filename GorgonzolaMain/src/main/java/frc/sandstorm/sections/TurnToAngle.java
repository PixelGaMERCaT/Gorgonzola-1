/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.sandstorm.sections;
import frc.robot.*;
import frc.sandstorm.*;
/**
 * Add your docs here.
 */
public class TurnToAngle extends SandstormSection {
    double angle;
    Drivetrain drive;

    public TurnToAngle(double angle, double time) {
        this.angle = angle;
        this.duration = time;
        drive = Globals.drivetrain;
    }

    public void init() {
        super();
        drive.turnController.setSetpoint(this.angle);
        drive.turnController.enable();
    }

    @Override public void end() {
        drive.turnController.disable();
    }

    @Override public boolean customFinish() {
        return drive.turnController.onTarget();
    }

    @Override public void update() {
        drive.autoDrive(0, drive.turnController.get());
    }

}
