package frc.components.lights;

import java.awt.Color;

import frc.components.Component;
import com.mach.LightDrive.*;

public class LightController implements Component {
    private LightDriveCAN lDriveCAN;

    public LightController() {
        lDriveCAN = new LightDriveCAN();
    }

  /**
   * Sets the LED colors. However, changes need to be 'pushed' to the LEDs
   */

    public void setColor(int strip, int r, int g, int b) {
        lDriveCAN.SetColor(strip, new Color(r, g, b));
    }

    public void setColor(int strip, Color color) {
        lDriveCAN.SetColor(strip, color);
    }

  /**
   * 'Pushes' any changes to the LED strip(s)
   */
    
    public void tick() {
        lDriveCAN.Update();
    }

}