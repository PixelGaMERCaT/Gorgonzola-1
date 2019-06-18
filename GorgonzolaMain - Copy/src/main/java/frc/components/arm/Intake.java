package frc.components.arm;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Solenoid;
import frc.components.Component;
import frc.components.InputManager;
import frc.robot.Globals;
import frc.robot.RobotMap;
import frc.talonmanager.IntakeTalonManager;

public class Intake implements Component {
    private Solenoid leftSuction, rightSuction, venturi, hatchActiveOut;
    private InputManager inputManager;
    private IntakeTalonManager intakeTalon1, intakeTalon2;
    private Shoulder shoulder;

    public Intake() {

        intakeTalon1 = new IntakeTalonManager(RobotMap.INTAKE_TALON_1);
        intakeTalon2 = new IntakeTalonManager(RobotMap.INTAKE_TALON_2);
        intakeTalon1.setInverted(false);
        intakeTalon2.setInverted(false);

        leftSuction = new Solenoid(RobotMap.INTAKE_LEFT_SOLENOID);
        rightSuction = new Solenoid(RobotMap.INTAKE_RIGHT_SOLENOID);
        venturi = new Solenoid(RobotMap.INTAKE_ACTUATOR_SOLENOID);
        hatchActiveOut = new Solenoid(RobotMap.HATCH_DEPLOY_SOLENOID);
    }

    public void init() {
        inputManager = Globals.inputManager;
        shoulder = Globals.shoulder;
    }

    public void tick() {
        boolean hatchIntake = inputManager.getHatchIntakeButton();

        if (hatchIntake) { //picking up a hatch
            leftSuction.set(true);
            rightSuction.set(true);
            venturi.set(true);
            hatchActiveOut.set(false);
        } else if (inputManager.getHatchOutputButton()) {
            leftSuction.set(true);
            rightSuction.set(true);
            venturi.set(false);
            hatchActiveOut.set(true);
        } else {
            venturi.set(false);
            leftSuction.set(false);
            rightSuction.set(false);
            hatchActiveOut.set(false);
        }
        if (inputManager.getBallIntakeOutButton()) {
            switch (shoulder.desiredPos) {
            case FULL_MANUAL:
                intakeTalon1.set(ControlMode.PercentOutput, -1);
                break;
            case BALL_HIGH:
                intakeTalon1.set(ControlMode.PercentOutput, -1);
                break;
            default:
                intakeTalon1.set(ControlMode.PercentOutput, -.8);
                break;

            }
        } else if (inputManager.getBallIntakeInButton()) {
            intakeTalon1.set(ControlMode.PercentOutput, .8);
        } else {
            intakeTalon1.set(ControlMode.PercentOutput, 0.17);
        }
    }
}