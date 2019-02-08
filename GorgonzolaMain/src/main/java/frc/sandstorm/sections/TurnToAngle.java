/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.sandstorm.sections;
import frc.robot.*;
import frc.sandstorm.*;
import edu.wpi.first.wpilibj.PIDController;

/**
 * Add your docs here.
 */
public class TurnToAngle extends SandstormSection {
    double angle;
    Drivetrain drive;
    PIDController turnController;
    Gyro gyro;

    public TurnToAngle(double angle, double time) {
        this.angle = angle;
        this.duration = time;
        drive = Globals.drivetrain;
        gyro = Globals.gyro;
        turnController = new PIDController(Constants.TURN_KP, Constants.TURN_KI, Constants.TURN_KD, gyro, o -> {
        });
        turnController.setAbsoluteTolerance(1);
        turnController.setInputRange(-180, 180);
        turnController.setOutputRange(-1, 1);
        turnController.setContinuous(true);
        turnController.setInputRange(-180, 180);
        turnController.setOutputRange(-1, 1);
        turnController.setContinuous(true);
    }

    public void init() {
        super.init();
        turnController.setSetpoint(this.angle);
        turnController.enable();
    }

    @Override public void end() {
        turnController.disable();
    }

    public boolean customFinish() {
        return turnController.onTarget();
    }

    @Override public void update() {
        drive.autoDrive(0, turnController.get());
    }

}
