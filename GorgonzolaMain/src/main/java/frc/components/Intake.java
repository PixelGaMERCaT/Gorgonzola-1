package frc.components;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.Globals;
import frc.robot.RobotMap;
import frc.talonmanager.IntakeTalonManager;

public class Intake implements Component{
    private Solenoid leftSuction, rightSuction, suctionActuator;
    private InputManager im;
    private LogInterface logger;
    private Compressor compressor;
    private IntakeTalonManager intakeTalon1, intakeTalon2;
    public Intake(){
        
        intakeTalon1 = new IntakeTalonManager(RobotMap.INTAKE_TALON_1);
        intakeTalon2 = new IntakeTalonManager(RobotMap.INTAKE_TALON_2);
        intakeTalon1.setInverted(false);
        intakeTalon2.setInverted(false);
        intakeTalon2.follow(intakeTalon1);

        leftSuction=new Solenoid(RobotMap.INTAKE_LEFT_SOLENOID);
        rightSuction=new Solenoid(RobotMap.INTAKE_RIGHT_SOLENOID);
        suctionActuator=new Solenoid(RobotMap.INTAKE_ACTUATOR_SOLENOID);
    }
    public void init(){
        im=Globals.im;
        logger=Globals.logger;   
    }
    public void tick(){
        
        leftSuction.set(im.getHatchOutputButton());
        rightSuction.set(im.getHatchOutputButton());
        suctionActuator.set(im.getHatchIntakeButton());
        if (im.getIntakeOutButton()){
            intakeTalon1.set(ControlMode.PercentOutput, -1);
        } else if (im.getIntakeInButton()) {
            intakeTalon1.set(ControlMode.PercentOutput, 1);
        } else {
            intakeTalon1.set(ControlMode.PercentOutput, 0);
        }
    }
}