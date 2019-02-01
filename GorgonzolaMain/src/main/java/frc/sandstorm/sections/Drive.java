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
public class Drive extends SandstormSection {
    double forward, turn;
    Drivetrain drive;

    public Drive(double forward, double turn, double time) {
        this.forward = forward;
        this.turn = turn;
        this.duration = time;
        drive = Globals.drivetrain;
    }

    @Override public void end() {

    }

    @Override public boolean customFinish() {
        return true;
    }

    @Override public void update() {
        System.out.println("Drive");
        drive.autoDrive(forward, turn);
    }

}
