package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

public class InputManager implements Component {

    private Joystick left, right, aux;
    public InputManager(){
        left=new Joystick(RobotMap.LEFT_STICK);
        right=new Joystick(RobotMap.RIGHT_STICK);
        aux=new Joystick(RobotMap.AUX_STICK);
    }

    
    public double getForward(){
        return getSafetyButton() ? left.getY() : 0;
    }
    public double getTurn(){
        return getSafetyButton() ? right.getX() : 0;
    }
    public boolean getSafetyButton(){
        return left.getRawButton(ButtonMap.SAFETY);
    }
}