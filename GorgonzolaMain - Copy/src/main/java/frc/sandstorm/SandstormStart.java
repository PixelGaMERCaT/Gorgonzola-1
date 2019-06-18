/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.sandstorm;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.sandstorm.sections.MotionProfile;
import jaci.pathfinder.Waypoint;

/**
 * Add your docs here.
 */
public class SandstormStart {

    SandstormPath mpTestAuto;

    SandstormPath autoMode;
    SendableChooser<SandstormPath> autoChooser;

    public SandstormStart() {
    }

    /* 
    * Write auto modes in initSandstormPaths(), and then add them to the sendable chooser
    * ex:
    * -------------------------------------------
    * testAuto = new SandstormPath("Test Auto");
    * testAuto.add(new Drive(0.5, 0, 1));
    * testAuto.stopDrive();
    * -------------------------------------------
    * Would make an auto mode where the robot drives forward at 50% power for 1 second and then stops
    * Sendable Chooser code: autoChooser.addOption(testAuto.name, testAuto);
    */

    public void initSandstormPaths() {

        mpTestAuto = new SandstormPath("MP Test Auto");
        mpTestAuto.add(new MotionProfile(new Waypoint[] { new Waypoint(0, 0, 0), new Waypoint(150, 20, 0) }));
        mpTestAuto.stopDrive();

        autoChooser = new SendableChooser();
        autoChooser.addDefault(mpTestAuto.name, mpTestAuto);
        SmartDashboard.putData("Sandstorm Mode Chooser", autoChooser);

    }

    public SandstormPath start() { // Auto chooser logic should be written here
        autoMode = autoChooser.getSelected();
        return autoMode;
    }
}
