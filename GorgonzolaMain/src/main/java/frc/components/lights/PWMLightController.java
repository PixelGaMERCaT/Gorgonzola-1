package frc.components.lights;

import java.awt.Color;

import com.mach.LightDrive.LightDrivePWM;

import edu.wpi.first.wpilibj.Servo;
import frc.components.Component;

public class PWMLightController implements Component {
    private LightDrivePWM lightDrive;
    public static final int ARMLEFT = 3;
    public static final int ARMRIGHT = 4;
    public static final int MASTLEFT = 2;
    public static final int MASTRIGHT = 1;

    public PWMLightController(int PWMChannel1, int PWMChannel2) {
        lightDrive = new LightDrivePWM(new Servo(PWMChannel1), new Servo(PWMChannel2));
    }

    /**
     * Only use the constants already set; do not create a new Color(r,g,b);
     */
    public void setColor(int strip, Color color) {
        lightDrive.SetColor(strip, color);
        lightDrive.Update();
    }

    public void tick(){
        setColor(0, Color.BLUE);
    }

    @Override
    public void init() {
        lightDrive.SetColor(1, Color.YELLOW);
        lightDrive.SetColor(2, Color.YELLOW);
        lightDrive.SetColor(3, Color.YELLOW);
        lightDrive.SetColor(4, Color.YELLOW);
    }
}