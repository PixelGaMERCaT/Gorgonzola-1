/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.sandstorm.SandstormPath;
import frc.sandstorm.SandstormStart;

/**
 * The main class, all code execution begins here. Depending on game phase, one of
 * Robot.java's methods is always being run on the robot.
 * @author Jeff
 */
public class Robot extends TimedRobot {
    public SandstormPath selectedMode; //Selected auto
    public SandstormStart sandstormStarter;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    @Override
    public void robotInit() {
        Globals.init();
        //Init auto paths:
        sandstormStarter = new SandstormStart();
        sandstormStarter.initSandstormPaths();

    }

    /**
     * This function is called every robot packet, no matter the mode. 
     */
    @Override
    public void robotPeriodic() {
    }

    /**
     * This code is run at the beginning of autonomous 
     */
    @Override
    public void autonomousInit() {
        selectedMode = sandstormStarter.start();
        SmartDashboard.putString("Sandstorm Path: ", selectedMode.getName());
        Globals.drivetrain.resetEncoders();
    }

    /**
     * This is run at the start of teleop (driver controlled period)
     */
    public void teleopInit() {
        Globals.drivetrain.resetEncoders();
    }

    /**
     * This is run 50 times per second during autonomous.
     */
    @Override
    public void autonomousPeriodic() {
        if (!selectedMode.isFinished()) {
            Globals.gearShifter.tick();
            selectedMode.tick();
        }
        //teleopPeriodic();
    }

    /**
     * This is called 50 times per second during teleop (Driver control)
     */
    @Override
    public void teleopPeriodic() {
        Globals.tick();
    }
}
