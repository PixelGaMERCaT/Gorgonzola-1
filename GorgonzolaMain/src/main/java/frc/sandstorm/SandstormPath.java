/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.sandstorm;

import java.util.ArrayList;
import frc.sandstorm.*;
import frc.sandstorm.sections.*;
/**
 * Add your docs here.
 */
public class SandstormPath {
    ArrayList<SandstormSection> sections = new ArrayList<SandstormSection>();
    public String name;
    int section = 0;
    public boolean over = false;

    public SandstormPath(String name) {
        this.name = name;
    }

    public void add(SandstormSection newSection) { // Adds a section to the path
        sections.add(newSection);
    }

    public void tick() { // Iterates through the sections of the auto mode
        if (section == 0 && sections.get(section).startTime == 0) {
            sections.get(section).init();
        } else {
            sections.get(section).update(); 
        }
        if (sections.get(section).isFinished()) {
            sections.get(section).end();
            section++;
            if (!(section + 1 > sections.size())) {
                sections.get(section).init();
            } else {
                over = true;
            }
        }
    }

    public void stopDrive() { // Adds a section that ends the robot's driving
        sections.add(new Drive(0, 0, 0));
    }
}
