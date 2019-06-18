package frc.components;

import java.awt.Color;

import frc.components.lights.LightController;
import frc.robot.Constants;
import frc.robot.Globals;

/**
 * Corrects tipping robots in case of bad drivers; drive the robot backward if tipping backward and vice versa.
 * IMPORTANT: Defines Backwards tipping as positive pitch.
 */
public class TipCorrector implements Component {
    private Drivetrain drivetrain;
    private Gyro gyro;
    private InputManager inputManager;
    private NetworkInterface robotDataTable;
    private LightController lightController;

    private boolean override; //whether or not the tip code has been overriden by drivers
    private boolean correcting; //whether or not the tip code is active

    public TipCorrector() {
        override = false;
        correcting = false;
    }

    public void init() {
        inputManager = Globals.inputManager;
        drivetrain = Globals.drivetrain;
        gyro = Globals.gyro;
        lightController = Globals.lightController;
        robotDataTable = Globals.robotDataTable;
    }

    public void tick() {
        robotDataTable.setDouble("TipActive", override ? 0 : correcting ? 1 : 2);
        //0 indicates tip code overridden, 1 indicates tip code active, 2 indicates tip code ready
        if (inputManager.getTipOverrideButton()) {
            override = true;
        } else if (inputManager.getTipEnableButton()) {
            override = false;
        }
        if (!override) {
            if (gyro.getNormalizedPitch() > Constants.TIP_PITCH_THRESHOLD) {
                //tipping backward
                correcting = true;
                drivetrain.autoDrive(-1, 0);
                System.out.println("OVERRIDING PITCH " + gyro.getNormalizedPitch());
                lightController.setMastColor(Color.RED);
            } else if (gyro.getNormalizedPitch() < -Constants.TIP_PITCH_THRESHOLD) {
                //tipping forward
                correcting = true;
                drivetrain.autoDrive(1, 0);
                System.out.println("OVERRIDING PITCH " + gyro.getNormalizedPitch());
                lightController.setMastColor(Color.RED);
            } else {
                //not tipping
                correcting = false;
                lightController.setMastColor(Color.YELLOW);
            }

        } else {
            //overridden
            correcting = false;
            lightController.setMastColor(Color.BLUE);
        }
    }
    /**
     * Determines whether this TipCorrector is correcting (meaning pitch angle is too high)
     * @return true if this TipCorrector is active, false otherwise. 
     */
    public boolean isCorrecting() {
        return correcting;
    }
}
