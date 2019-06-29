/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.sandstorm;

/**
 * A specific action to be completed during autonomous. Can be run for a certain
 * amount of time or rely on a custom finishing criterion.
 * @author Jeff
 */
public abstract class SandstormSection {
    protected boolean finished = false;
    protected double duration; // Duration of section in seconds
    protected long startTime = 0;

    /**
     * Initializes this Section's values.
     * By default, init() stores the time at which the section starts during the auto mode
     * It should be overriden in sections where things need to be initialized as soon as the section starts, such as in TurnToAngle and MotionProfile
     */
    public void init() {
        startTime = System.currentTimeMillis();
        if (duration != -1) {
            duration = duration * 1000;
        }
    }

    /** 
    * tick() is what will be run every tick while the section is active during the auto mode
    * It will often include motor powering and should always be overriden
    */
    public abstract void tick();

    /**
    * end() should be overriden to include what the section should do when it ends
    * An example of this could be resetting encoders at the end of the section
    */

    public void end() {
    }

    /**
    * By default, customFinish() returns false
    * It should be overriden in sections with a non-time based end condition, such as MotionProfile and turnToAngle
    */
    public boolean customFinish() {
        return false;
    }

    /**
    * For a section that does not end based on time, set duration to -1
    * and add custom finish code to the customFinish() method
    * End conditions are set up in isFinished()
    * It should NOT be overriden
    */
    public boolean isFinished() {
        return (System.currentTimeMillis() - startTime > duration && duration != -1) || customFinish() ? true : false;
    }
}
