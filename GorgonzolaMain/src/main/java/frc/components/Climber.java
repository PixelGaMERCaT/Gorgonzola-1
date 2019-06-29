package frc.components;

import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.Globals;
import frc.robot.RobotMap;

/**
 * Defines the Climber (device that allows the robot to climb onto Hab 2).
 * This consists of a solenoid that is activated to drop two plastic "knives"
 * at the front of the robot, allowing us to drive up the Hab 2 wall.
 * 
 * In the past, it additionally consisted of a CAM that could move to push the robot forward onto HAB 3.
 * @author Jeff
 */ 
public class Climber implements Component {

    private InputManager inputManager;
    private Solenoid knifeDropper;
    private NetworkInterface robotDataTable;

    
    public Climber() {
        knifeDropper = new Solenoid(RobotMap.CLIMB_KNIFE_SOLENOID);
    }

    public void init() {
        robotDataTable = Globals.robotDataTable;
        inputManager = Globals.inputManager;
        robotDataTable.setNumber("CamAngle", 0); //The angle of the cam, always 0 now :'(

    }
    
    public void tick() {
        knifeDropper.set(inputManager.getClimbDeployButton());
    }
}