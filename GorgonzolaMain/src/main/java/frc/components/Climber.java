package frc.components;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.Constants;
import frc.robot.Globals;
import frc.robot.RobotMap;
import frc.talonmanager.ClimbTalonManager;

public class Climber implements Component {
    private ClimbTalonManager talon1, talon2;
    private InputManager im;
    Solenoid leftKnife, rightKnife;

    public Climber() {
        //leftKnife = new Solenoid(3);
        //rightKnife = new Solenoid(4);
        talon1 = new ClimbTalonManager(RobotMap.CLIMB_TALON_1);
        talon1.initEncoder(Constants.CLIMB_KP, Constants.CLIMB_KI, Constants.CLIMB_KD, Constants.CLIMB_KF);
        talon2 = new ClimbTalonManager(RobotMap.CLIMB_TALON_2);
        talon2.follow(talon1);
    }

    public void init() {
        im = Globals.im;
    }

    public void tick() {
        //leftKnife.set(im.getAuxButton(6));
        //rightKnife.set(im.getAuxButton(6));
        if (im.getAutoClimbButton()) {
            //talon1.set(ControlMode.MotionMagic, 1024);
        } else if (im.getManualClimbButton()) {
            talon1.set(ControlMode.PercentOutput, im.getClimb());
        } else {
            talon1.set(ControlMode.PercentOutput, 0);
        }
        talon1.set(ControlMode.PercentOutput, im.getClimb());

    }
}