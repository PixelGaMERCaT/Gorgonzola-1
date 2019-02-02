/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.sandstorm;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.sandstorm.sections.*;
import frc.sandstorm.SandstormPath;
/**
 * Add your docs here.
 */
public class SandstormStart {

    SandstormPath testPath;
    SandstormPath testPath2;
    SandstormPath testPath3;

    SandstormPath autoMode;
    SendableChooser<SandstormPath> autoChooser;

    public SandstormStart() {}
    
    public void initSandstormPaths() { // Build sandstorm/auto modes here
        testPath = new SandstormPath("Test Path 1");
        testPath.add(new Drive(0.25, 0, 1));
        testPath.add(new Drive(-0.25, 0, 1));
        testPath.stopDrive();
        
        testPath2 = new SandstormPath("Turn 90 Degrees");
        testPath2.add(new TurnToAngle(90, -1));
        testPath2.stopDrive();

        testPath3 = new SandstormPath("Drive Straight");
        testPath3.add(new DriveStraight(0.25, 1));
        testPath3.stopDrive();

        autoChooser = new SendableChooser();
        autoChooser.addOption(testPath2.name, testPath2);
        autoChooser.addOption(testPath3.name, testPath3);
        autoChooser.setDefaultOption(testPath.name, testPath);
        SmartDashboard.putData("Sandstorm Mode Chooser", autoChooser);
        
    }

    public SandstormPath start() { // Auto chooser logic should be written here
        autoMode = autoChooser.getSelected();
        return autoMode;
    }
}

