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

    SandstormPath autoMode;
    SendableChooser<SandstormPath> autoChooser;

    public SandstormStart() {}
    
    public void initSandstormPaths() { // Build sandstorm/auto modes here

        autoChooser = new SendableChooser();
        SmartDashboard.putData("Sandstorm Mode Chooser", autoChooser);
        
    }

    public SandstormPath start() { // Auto chooser logic should be written here
        autoMode = autoChooser.getSelected();
        return autoMode;
    }
}

