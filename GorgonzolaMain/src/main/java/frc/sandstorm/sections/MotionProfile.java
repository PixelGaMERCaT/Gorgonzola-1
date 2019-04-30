/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.sandstorm.sections;

import frc.components.Drivetrain;
import frc.MotionProfiling.MotionProfiler;
import frc.robot.Globals;
import frc.sandstorm.SandstormSection;
import jaci.pathfinder.Waypoint;
/**
 * Add your docs here.
 */
public class MotionProfile extends SandstormSection {
    Waypoint[] points;
    Drivetrain drive;
    MotionProfiler mp;

    public MotionProfile(Waypoint[] points) {
        this.duration = -1;
        drive = Globals.drivetrain;
        mp = new MotionProfiler();
        this.points = points;
    }

    @Override public void end() {
        drive.resetEncoders();
    }

    @Override public void init() {
        super.init();
        mp.setPath(points);
        mp.init();
    }

    @Override public boolean customFinish() {
        return mp.isFinished();
    }

    @Override public void update() {
        mp.run();
    }

}
