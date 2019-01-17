package frc.robot;

public class Globals {
    public static InputManager im;
    public static Drivetrain drivetrain;
    
    public static void init(){
        
        im=new InputManager();
        drivetrain=new Drivetrain();
    }
    /** Ticks all components in globals
     *  
    */
    public static void tick() {
        im.tick();
        drivetrain.tick();
    }
    
}