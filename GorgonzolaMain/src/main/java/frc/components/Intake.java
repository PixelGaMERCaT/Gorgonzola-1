package frc.components;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Globals;
import frc.robot.RobotMap;
import frc.talonmanager.IntakeTalonManager;

public class Intake implements Component {
    private Solenoid leftSuction, rightSuction, venturi, hatchActiveOut;
    private InputManager im;
    private LogInterface logger;
    private Compressor compressor;
    private IntakeTalonManager intakeTalon1, intakeTalon2;

    public Intake() {

        intakeTalon1 = new IntakeTalonManager(RobotMap.INTAKE_TALON_1);
        intakeTalon2 = new IntakeTalonManager(RobotMap.INTAKE_TALON_2);
        intakeTalon1.setInverted(false);
        intakeTalon2.setInverted(false);
        intakeTalon2.follow(intakeTalon1);

        leftSuction = new Solenoid(RobotMap.INTAKE_LEFT_SOLENOID);
        rightSuction = new Solenoid(RobotMap.INTAKE_RIGHT_SOLENOID);
        venturi = new Solenoid(RobotMap.INTAKE_ACTUATOR_SOLENOID);
        hatchActiveOut = new Solenoid(6);
    }

    public void init() {
        im = Globals.im;
        logger = Globals.logger;
    }

    int venturiTick = 200;
    int hatchOutputTimer=-1;
    public void tick() {
        boolean hatchIntake = im.getHatchButton();

        if (hatchIntake) {
            SmartDashboard.putString("intake", "intaking hatch");
            //picking up a hatch
            if (venturiTick > 0) {
                leftSuction.set(true);
                rightSuction.set(true);
                venturi.set(true);
            } else {
                leftSuction.set(false);
                rightSuction.set(false);
                venturi.set(false);
                
            }
            venturiTick--;
            hatchActiveOut.set(false);
            hatchOutputTimer=25;
        } else if (hatchOutputTimer>0){
            SmartDashboard.putString("intake", "Outputting hatch");
            leftSuction.set(true);
            rightSuction.set(true);
            venturi.set(false);
            hatchActiveOut.set(true);
            hatchOutputTimer--;
            venturiTick=200;
        } else {
            SmartDashboard.putString("intake", "Default State");
            venturi.set(false);
            leftSuction.set(false);
            rightSuction.set(false);
            hatchActiveOut.set(false);
        }
        if (im.getIntakeOutButton()) {
            intakeTalon1.set(ControlMode.PercentOutput, -1);
        } else if (im.getBallIntakeInButton()) {
            intakeTalon1.set(ControlMode.PercentOutput, 1);
        } else {
            intakeTalon1.set(ControlMode.PercentOutput, 0);
        }
    }
}