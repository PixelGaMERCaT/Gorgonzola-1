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
public class Parallel extends SandstormSection {
    SandstormSection section1, section2;
    double endTime1, startTime2;
    Drivetrain drive;

    // Section for running different sections at the same time
    public Parallel(SandstormSection section1, double endTime1, SandstormSection section2, double startTime2, double endTime2) {
        this.section1 = section1;
        this.section2 = section2;
        this.endTime1 = endTime1;
        this.startTime2 = startTime2;
        this.duration = endTime2;
        drive = Globals.drivetrain;
    }

    @Override public void init() {
        section1.init();
    }

    @Override public void end() {
        section2.end();
    }

    @Override public boolean customFinish() {
        return section1.customFinish() && section2.customFinish();
    }

    @Override public void update() {
        if ((System.currentTimeMillis() - this.startTime) / 1000.0 < endTime1) {
            section1.update();
        }
        if ((System.currentTimeMillis() - this.startTime) / 1000.0 > startTime2) {
            if ((System.currentTimeMillis() - this.startTime) / 1000.0 == startTime2) {
                section2.init();
            } else {
                section2.update();
            }
        }
    }
}
