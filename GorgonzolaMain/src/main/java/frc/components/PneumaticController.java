package frc.components;

import edu.wpi.first.hal.CompressorJNI;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import frc.robot.Globals;
import frc.robot.RobotMap;

public class PneumaticController implements Component{
    private Solenoid intakeLeft, intakeRight, intakeActuator;
    private InputManager im;
    private LogInterface logger;
    private Compressor compressor;
    public PneumaticController(){
        
        compressor=new Compressor(RobotMap.COMPRESSOR);
        
        compressor.setClosedLoopControl(false);
        intakeLeft=new Solenoid(RobotMap.INTAKE_LEFT_SOLENOID);
        intakeRight=new Solenoid(RobotMap.INTAKE_RIGHT_SOLENOID);
        intakeActuator=new Solenoid(RobotMap.INTAKE_ACTUATOR_SOLENOID);
    }
    public void init(){
        im=Globals.im;
        logger=Globals.logger;   
    }
    public void tick(){
        intakeLeft.set(im.getHatchIntakeButton());
        intakeRight.set(im.getHatchIntakeButton());
        intakeActuator.set(im.getIntakeActuatorButton());
        
    }
}