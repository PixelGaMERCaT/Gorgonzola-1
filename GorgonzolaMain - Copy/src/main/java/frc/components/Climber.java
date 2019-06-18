package frc.components;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.Globals;
import frc.robot.RobotMap;
import frc.talonmanager.ClimbTalonManager;

public class Climber implements Component {
    public ClimbTalonManager talon1, talon2;
    private InputManager inputManager;
    Solenoid knives;
    private NetworkInterface robotDataTable;
    public Climber() {
        knives = new Solenoid(3);
        talon1 = new ClimbTalonManager(RobotMap.CLIMB_TALON_1);
        talon1.initEncoder(Constants.CLIMB_KP, Constants.CLIMB_KI, Constants.CLIMB_KD, Constants.CLIMB_KF);
        talon2 = new ClimbTalonManager(RobotMap.CLIMB_TALON_2);
        talon2.follow(talon1);
    }

    public void init() {
        robotDataTable=Globals.robotDataTable;
        inputManager = Globals.inputManager;
    }

    public void tick() {
        robotDataTable.setNumber("CamAngle", 0);//talon1.getEncoderPositionContextual());
        knives.set(inputManager.getClimbKnives());
        //SmartDashboard.putNumber("climbps", talon1.getEncoderPosition());
        //SmartDashboard.putNumber("Climbvel", talon1.getEncoderVelocity());

        if (inputManager.getAutoClimbButton()) {
            talon1.set(ControlMode.MotionMagic, 1024);
        } else if (inputManager.getManualClimbButton()) {
            talon1.set(ControlMode.PercentOutput, inputManager.getClimb());
        } else {
            talon1.set(ControlMode.PercentOutput, 0);
        }

    }
}