/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.sandstorm;

import java.util.ArrayList;

/**
 * Add your docs here.
 */
public class SandstormPath {
    ArrayList<SandstormSection> sections = new ArrayList<SandstormSection>;
    int section = 0;
    public boolean over = false;

    public SandstormPath() {}

    public void add(SandstormSection newSection) { // Adds a section to the path
        sections.add(newSection);
    }

    public void tick() { 
        if (section = 0 && sections[section].startTime = 0) {
            sections[section].init();
        } else {
            sections[section].update();        
        }
        if (sections[section].isFinished()) {
            sections[section].end();
            section++;
            if (!section + 1 > sections.size()) {
                sections[section].init();
            } else {
                over = true;
            }
        }
    }

    public void stopDrive() { // Adds a section that ends the robot's driving
        sections.add(new Drive(0, 0, 0));
    }
}
