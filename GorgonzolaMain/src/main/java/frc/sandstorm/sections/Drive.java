/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.sandstorm.sections;
import frc.robot.*;
import frc.sandstorm.*;
import frc.components.*;
/**
 * A basic SandstormSection that drives at a constant forward and turn rate.
 */
public class Drive extends SandstormSection {
    private double forward, turn; //Constant forward and turn outputs
    private Drivetrain drivetrain;
    /**
     * Constructs a Drive object with the given parameters.
     * @param forward a constant forward power to be applied every tick for the duration of this section.
     * @param turn a constant turn power to be applied every tick for the duration of this section
     * @param time the amount of time this section should last.
     */
    public Drive(double forward, double turn, double time) {
        this.forward = forward;
        this.turn = turn;
        this.duration = time;
        drivetrain = Globals.drivetrain;
    }

    @Override public void end() {

    }

    @Override public boolean customFinish() {
        return false;
    }

    @Override public void tick() {
        drivetrain.autoDrive(forward, turn);
    }

}
