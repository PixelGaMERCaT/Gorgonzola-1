/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.sandstorm;

import java.util.ArrayList;

import frc.sandstorm.sections.Drive;

/**
 * A set of Sandstorm Sections that run sequentially.
 * @author Jeff
 */ 
public class SandstormPath extends SandstormSection {
    private ArrayList<SandstormSection> sections = new ArrayList<SandstormSection>();
    private String name;
    private int sectionNumber = 0; //iterator for sections
    private boolean finished = false;

    /**
     * Constructor for an empty SandstormPath.
     * @param name The name (used in dashboard and for debugging) of this path
     */
    public SandstormPath(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this path.
     * @return the name of this path
     */
    public String getName() {
        return name;
    }

    /**
     * Determines whether this SandstormPath has finished executing
     * @return true if it has finished, false otherwise.
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Appends a SandstormSection to the end of this path. It will be executed following the previous
     * addition to this path.
     * Since SandstormPath extends SandstormSection, SandStormPaths can also be added using this method.
     * @param newSection the SandstormSection to add
     */
    public void add(SandstormSection newSection) { // Adds a section to the path
        sections.add(newSection);
    }
    /**
     * Runs every section that was passed into add() while initializing.
     */
    public void tick() { // Iterates through the sections of the auto mode
        if (sectionNumber == 0 && sections.get(sectionNumber).startTime == 0) {
            sections.get(sectionNumber).init();
        } else {
            sections.get(sectionNumber).tick();
        }
        if (sections.get(sectionNumber).isFinished()) {
            sections.get(sectionNumber).end();
            sectionNumber++;
            if (sectionNumber < sections.size()) {
                sections.get(sectionNumber).init();
            } else {
                finished = true;
            }
        }
    }

    public void stopDrive() { // Adds a section that ends the robot's driving
        sections.add(new Drive(0, 0, 0));
    }

    @Override
    public void end() {
    }
}
