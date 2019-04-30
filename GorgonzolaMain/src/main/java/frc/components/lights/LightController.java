package frc.components.lights;

import java.awt.Color;

import frc.components.Component;
import frc.components.InputManager;
import frc.components.NetworkInterface;
import frc.robot.Globals;

import com.mach.LightDrive.*;

public class LightController implements Component {
    private LightDriveCAN lDriveCAN;
    private InputManager im;
    private int tick;
    private double cycle = 100.0;
    private NetworkInterface robotDataTable;

    public LightController() {
        lDriveCAN = new LightDriveCAN();
    }

    public void init() {
        im = Globals.im;
        tick = 0;
        robotDataTable = Globals.robotDataTable;
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

    public void setMastColor(Color color) {
        lDriveCAN.SetColor(1, color);
        lDriveCAN.SetColor(2, color);
    }

    public void setArmColor(Color color) {
        lDriveCAN.SetColor(3, color);
        lDriveCAN.SetColor(4, color);
    }

    /**
     * 'Pushes' any changes to the LED strip(s)
     */

    public void tick() {
        if (im.getRainbowButton()) {
            robotDataTable.setBoolean("rainbow", true);
            tick++;
            /*lDriveCAN.SetColor(1, rainbow((tick % cycle) / cycle));
            lDriveCAN.SetColor(2, rainbow((tick % cycle) / cycle));
            lDriveCAN.SetColor(3, rainbow((tick % cycle) / cycle));
            lDriveCAN.SetColor(4, rainbow((tick % cycle) / cycle));*/
            if (tick % 75 < 25) {
                lDriveCAN.SetColor(1, Color.RED);
                lDriveCAN.SetColor(2, Color.RED);
                lDriveCAN.SetColor(3, Color.RED);
                lDriveCAN.SetColor(4, Color.RED);
            }else if (tick % 75 < 50) {
                lDriveCAN.SetColor(1, Color.GREEN);
                lDriveCAN.SetColor(2, Color.GREEN);
                lDriveCAN.SetColor(3, Color.GREEN);
                lDriveCAN.SetColor(4, Color.GREEN);
            }else {
                lDriveCAN.SetColor(1, Color.BLUE);
                lDriveCAN.SetColor(2, Color.BLUE);
                lDriveCAN.SetColor(3, Color.BLUE);
                lDriveCAN.SetColor(4, Color.BLUE);
            }
            
            /*lDriveCAN.SetLevel(1, (byte) 0);
            lDriveCAN.SetLevel(2, (byte) 0);
            lDriveCAN.SetLevel(3, (byte) 0);
            lDriveCAN.SetLevel(4, (byte) 0);*/
        } else {
            robotDataTable.setBoolean("rainbow", false);
        }
        lDriveCAN.Update();
        
    }

    private Color rainbow(double t) {
        if (t < 0 || t > 1) {
            t -= Math.floor(t);
        }

        t = (t + 0.265) % 1;

        final double ts = Math.abs(t - 0.5);
        final double h = (360 * t + 20) * Math.PI / 180;
        final double l = 0.8 - ts * 0.9;
        final double a = (1.5 - ts * 1.5) * l * (1 - l);
        return new Color(Math.min((float) (l + a * (-0.14861 * Math.cos(h) + 1.78277 * Math.sin(h))), 1.0f),
                Math.min((float) (l + a * (-0.29227 * Math.cos(h) + -0.90649 * Math.sin(h))), 1.0f),
                Math.min((float) (l + a * (1.97294 * Math.cos(h))), 1.0f));
    }

}