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

    public void init() { // Things to run when the section starts
        startTime = System.currentTimeMillis();
        if (duration != -1) {
            duration = duration * 1000;
        }
    } 

    public abstract void update(); // Things to run while the section is running

    public abstract void end(); // Things to run when the section ends

    public boolean customFinish() { // Custom finish code; for a section that ends based on time, return false
        return false;
    } 
    
    /*
    * For a section that does not end based on time, set duration to -1
    * and add custom finish code to the customFinish() method
    */
    public boolean isFinished() { // End conditions
        return (System.currentTimeMillis() - startTime > duration && duration != -1) || customFinish() ? true : false; 
    }
}
