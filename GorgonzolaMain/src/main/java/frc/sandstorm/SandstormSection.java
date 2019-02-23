/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.sandstorm;

/**
 * Add your docs here.
 */
public abstract class SandstormSection {
    boolean finished = false;
    public double duration; // Duration of section in seconds
    public long startTime = 0;

    /*
    * By default, init() stores the time at which the section starts during the auto mode
    * It should be overriden in sections where things need to be initialized as soon as the section starts, such as in TurnToAngle and MotionProfile
    */
    public void init() { 
        startTime = System.currentTimeMillis();
        if (duration != -1) {
            duration = duration * 1000;
        }
    } 

    /*
    * update() is what will be run while the sectoin is active during the auto mode
    * It will often include motor powering and should always be overriden
    */
    public abstract void update(); 

    /*
    * end() should be overriden to include what the section should do when it ends
    * An example of this could be resetting encoders at the end of the section
    */

    public abstract void end();

    /*
    * By default, customFinish() returns false
    * It should be overriden in sections with a non-time based end condition, such as MotionProfile and turnToAngle
    */
    public boolean customFinish() {
        return false;
    } 
    
    /*
<<<<<<< HEAD
    * For a section that does not end based on time, set duration to -1
    * and add custom finish code to the customFinish() method
=======
    * End conditions are set up in isFinished()
    * It should NOT be overriden
>>>>>>> c1e2cdc2a78d40f2782817778289cb30a9ef2f1f
    */
    public boolean isFinished() { 
        return (System.currentTimeMillis() - startTime > duration && duration != -1) || customFinish() ? true : false; 
    }
}
