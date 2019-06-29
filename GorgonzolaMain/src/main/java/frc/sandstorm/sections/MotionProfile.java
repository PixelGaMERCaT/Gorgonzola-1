/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.sandstorm.sections;

import frc.motionprofiling.MotionProfiler;
import frc.sandstorm.SandstormSection;
import jaci.pathfinder.Waypoint;

/**
 * A SandstormSection that follows a defined Motion Profile.
 * @author Jeff
 */ 
public class MotionProfile extends SandstormSection {
    private Waypoint[] points;
    private MotionProfiler mp;

    /**
    * Constructs a MotionProfile section.
    * @param points a list of Jaci WayPoint objects, each denoting an x, y, and angle value. 
    */
    public MotionProfile(Waypoint... points) {
        this.duration = -1;
        mp = new MotionProfiler();
        this.points = points;
    }

    

    @Override
    public void init() {
        super.init();
        mp.setPath(points);
        mp.init();
    }

    @Override
    public boolean customFinish() {
        return mp.isFinished();
    }

    @Override
    public void tick() {
        mp.run();
    }

}
