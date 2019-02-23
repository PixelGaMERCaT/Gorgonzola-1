package frc.components;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.Globals;
import frc.robot.RobotMap;
import frc.talonmanager.ClimbTalonManager;

public class Climber implements Component {
    private ClimbTalonManager talon1, talon2;
    private InputManager im;
    Solenoid leftKnife, right;

    public Climber() {
        leftKnife = new Solenoid(3);
        talon1 = new ClimbTalonManager(RobotMap.CLIMB_TALON_1);
        talon2 = new ClimbTalonManager(RobotMap.CLIMB_TALON_2);
        talon2.follow(talon1);
    }

    public void init() {
        im = Globals.im;
    }

    public void tick() {
        leftKnife.set(im.getAuxButton(6));

        talon1.set(ControlMode.PercentOutput, im.getClimb());

    }
}