package frc.components;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.CheeseLog.Loggable;
import frc.CheeseLog.SQLType.Decimal;
import frc.CheeseLog.SQLType.Type;
import frc.robot.Globals;

public class Shooter implements Component {
    public TalonSRX talon1, talon2;
    InputManager inputManager;

    public Shooter() {
        talon1 = new TalonSRX(3);
        talon2 = new TalonSRX(2);
        talon1.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
        talon2.follow(talon1);
        talon1.setSensorPhase(true);
        talon1.configMotionAcceleration(5600);
        talon1.configMotionCruiseVelocity(25800);
        talon1.config_kF(0, .695 * (1023.0 / 25800.0));
        talon1.config_kP(0, (3.0 * 1023.0 / 4000.0));
        talon1.config_kD(0, (190.0 * 1023.0 / 4000.0));
    }

    public void init() {
        inputManager = Globals.inputManager;
        Globals.logger.shooter = LogInterface.createTable("Shooter",
                new String[] { "RPS", "RawVelocity", "DesVel", "Revs", "RawPosition", "OutputPower" },
                new Type[] { new Decimal(), new Decimal(), new Decimal(), new Decimal(), new Decimal(), new Decimal() },
                new Loggable[] { () -> this.getVelRPS(), () -> talon1.getSelectedSensorVelocity(0),
                        () -> talon1.getClosedLoopTarget(0), () -> getPos(), () -> talon1.getSelectedSensorPosition(0),
                        () -> talon1.getMotorOutputPercent() });
        System.out.println(Globals.logger.shooter + " awejfoiawjefoiajwefoijawoeifj;aoweifjaw;eojfio");
    }

    private double maxVel = 0;

    public void tick() {
        //talon1.set(ControlMode.PercentOutput, im.getWristManualPosition());
        if (inputManager.getArmSafetyButton()) {
            run(-40.0);
        } else {
            talon1.set(ControlMode.PercentOutput, 0);
        }
        maxVel = Math.max(Math.abs(talon1.getSelectedSensorVelocity(0) / 4096.0 * 10.0), maxVel);
        SmartDashboard.putNumber("Velocity", talon1.getSelectedSensorVelocity(0) / 4096.0 * 10.0);
    }

    public double getVelRPS() {
        return talon1.getSelectedSensorVelocity(0) / 4096.0 * 10.0;
    }

    public double getPos() {
        return talon1.getSelectedSensorPosition() / 4096.0;
    }

    public void run(double rps) {
        talon1.set(ControlMode.Velocity, rps * 4096.0 / 10.0);
    }
}