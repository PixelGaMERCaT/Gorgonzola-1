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

    public SandstormStart() {}
    
    public void initSandstormPaths() { // Build sandstorm/auto modes here
        testPath = new SandstormPath();
        testPath.addSection(new Drive(1, 0, 10));
        testPath.stopDrive();
        
        testPath2 = new SandstormPath();
        testPath2.addSection(new Drive(-1, 0, 10));
        testPath.stopDrive();
    }

    public SandstormPath start() { // Auto chooser logic should be written here
        SendableChooser<SandstormPath> autoChooser = new SendableChooser();
        autoChooser.addDefault("Testing Mode", testPath);
        autoChooser.addObject("Testing Mode 2", testPath2);
        SmartDashboard.putData("Sandstorm Mode Chooser", autoChooser);
        return autoChooser.getSelected();
    }
}
