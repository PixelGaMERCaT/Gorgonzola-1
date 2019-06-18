package frc.components.lights;

import java.awt.Color;

import frc.components.Component;
import frc.components.InputManager;
import frc.components.NetworkInterface;
import frc.robot.Globals;

import com.mach.LightDrive.*;
/**
 * A class meant to control the lights on the robot to indicate various
 * pieces of information to the drivers.
 */
public class LightController implements Component {
    private LightDriveCAN lDriveCAN;
    private InputManager inputManager;
    private NetworkInterface robotDataTable;

    //Used for rainbow button (just a fun addition for use at demos and at the end of matches)
    private int rainbowTick;

    public LightController() {
        lDriveCAN = new LightDriveCAN();
    }

    public void init() {
        inputManager = Globals.inputManager;
        rainbowTick = 0;
        robotDataTable = Globals.robotDataTable;
    }

    

    /**
     * Sets the color of a given strip to a predefined Color
     * @param strip The index of the strip (based on where the strip is plugged into LightDrive)
     * @param color The desired color of the strip
     */
    private void setColor(int strip, Color color) {
        lDriveCAN.SetColor(strip, color);
    }
    /**
     * Sets the color of the two LED strips on the mast to a predefined color
     * @param color the desired mast color
     */
    public void setMastColor(Color color) {
        setColor(1, color);
        setColor(2, color);
    }
    /**
     * Sets the color of the two LED strips on the arm to a predefined color
     * @param color the desired arm color
     */
    public void setArmColor(Color color) {
        setColor(3, color);
        setColor(4, color);
    }

    
    public void tick() {
        //Rainbow (for showing off):
        if (inputManager.getRainbowButton()) {
            robotDataTable.setBoolean("rainbow", true);
            rainbowTick++;
            if (rainbowTick % 75 < 25) {
                setColor(1, Color.RED);
                setColor(2, Color.GREEN);
                setColor(3, Color.BLUE);
                setColor(4, Color.ORANGE);
            }else if (rainbowTick % 75 < 50) {
                setColor(1, Color.ORANGE);
                setColor(2, Color.RED);
                setColor(3, Color.GREEN);
                setColor(4, Color.BLUE);
            }else {
                setColor(1, Color.BLUE);
                setColor(2, Color.ORANGE);
                setColor(3, Color.RED);
                setColor(4, Color.GREEN);
            }
        } else {
            robotDataTable.setBoolean("rainbow", false);
        }
        //Update lights, regardless of rainbow:
        lDriveCAN.Update();
        
    }

    

}