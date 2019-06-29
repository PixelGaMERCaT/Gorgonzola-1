package frc.components.arm;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Solenoid;
import frc.components.Component;
import frc.components.InputManager;
import frc.robot.Globals;
import frc.robot.RobotMap;
import frc.talonmanager.IntakeTalonManager;

/**
 * Defines the intake of the robot (the thing on the end of the arm).
 * This contains two mechanisms:
 * 1. A pneumatic intake that uses suction to grab, hold, and release large disks (hatches)
 * 2. A motor intake that uses a rolling wheel to pick up, hold, and release balls (cargo)
 * @author Jeff
 */
public class Intake implements Component {
    /**
    * The Intake consists of 4 solenoids:
    *    leftSuctionActuator and rightSuctionActuator  - allow or disallow airflow through two suction cups
    *    hatchActiveIn - generates an inward flow of air (sucks air in the suction cups)
    *    hatchActiveOut - generates an outward flow of air (blows air out of the suction cups)    
    */
    private Solenoid leftSuctionActuator, rightSuctionActuator, hatchActiveIn, hatchActiveOut;
    private InputManager inputManager;
    private IntakeTalonManager ballIntakeTalon;
    private Shoulder shoulder;

    public Intake() {
        ballIntakeTalon = new IntakeTalonManager(RobotMap.INTAKE_TALON_1);
        ballIntakeTalon.setInverted(false);
        leftSuctionActuator = new Solenoid(RobotMap.LEFT_SUCTION_CUP);
        rightSuctionActuator = new Solenoid(RobotMap.RIGHT_SUCTION_CUP);
        hatchActiveIn = new Solenoid(RobotMap.HATCH_INTAKE_SOLENOID);
        hatchActiveOut = new Solenoid(RobotMap.HATCH_DEPLOY_SOLENOID);
    }

    public void init() {
        inputManager = Globals.inputManager;
        shoulder = Globals.shoulder;
    }

    public void tick() {
        tickHatchIntake();
        tickBallIntake();
    }
    /**
     * Ticks the ball intake according to input from the player.
     */
    public void tickBallIntake(){
        if (inputManager.getBallOutputButton()) {
            //A request from drivers: changes speed of ball output based on arm position
            switch (shoulder.desiredPosition) {
            case FULL_MANUAL:
                ballIntakeTalon.set(ControlMode.PercentOutput, -1);
                break;
            case BALL_HIGH:
                ballIntakeTalon.set(ControlMode.PercentOutput, -1);
                break;
            default:
                ballIntakeTalon.set(ControlMode.PercentOutput, -.8);
                break;
            }
        } else if (inputManager.getBallIntakeButton()) {
            ballIntakeTalon.set(ControlMode.PercentOutput, .8);
        } else {
            //If neither intaking nor outputting a ball, provide a constant stall current.
            ballIntakeTalon.set(ControlMode.PercentOutput, 0.17);
        }
    }
    /**
    * Ticks the Pneumatic intake, updating the solenoids based on the current player input
    */
    public void tickHatchIntake() {

        if (inputManager.getHatchIntakeButton()) {
            //When intaking a hatch, open both suction cups and activate inward airflow
            leftSuctionActuator.set(true);
            rightSuctionActuator.set(true);
            hatchActiveIn.set(true);
            hatchActiveOut.set(false);
        } else if (inputManager.getHatchOutputButton()) {
            //When outputting a hatch, open both suction cups and activate outward airflow
            leftSuctionActuator.set(true);
            rightSuctionActuator.set(true);
            hatchActiveIn.set(false);
            hatchActiveOut.set(true);
        } else {
            //When neither intaking nor outputting a hatch, turn off all airflow.
            hatchActiveIn.set(false);
            leftSuctionActuator.set(false);
            rightSuctionActuator.set(false);
            hatchActiveOut.set(false);
        }

    }
}