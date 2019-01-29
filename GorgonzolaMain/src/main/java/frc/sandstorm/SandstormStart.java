/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.sandstorm;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
/**
 * Add your docs here.
 */
public class SandstormStart {

    SandstormPath testPath;

    public SandstormStart() {}
    
    public void initSandstormPaths() { // Build sandstorm/auto modes here
        testPath = new SandstormPath();
        testPath.add(new Drive(0.1, 0, 1));
        testPath.stopDrive();
    }

    public SandstormPath start() { // Auto chooser logic should be written here
        SendableChooser autoChooser = new SendableChooser();
        autoChooser.addDefault("Testing Mode", testPath);
        SmartDashboard.putData("Sandstorm Mode Chooser", autoChooser);
        return autoChooser.getSelected();
    }
}
