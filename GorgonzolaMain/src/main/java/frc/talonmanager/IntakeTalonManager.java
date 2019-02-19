package frc.talonmanager;

public class IntakeTalonManager extends TalonManager {
    public IntakeTalonManager(int idx){
        super(idx);
        this.type=TalonType.INTAKE;
    }
    @Override
    public void initEncoder(double P, double I, double D, double F) {
        System.err.println("Attempt to get config encoder on an intake talon");
    }

    @Override
    public double getEncoderPositionContextual() {
        System.err.println("Attempt to get encoder position on an intake talon");
        return -1;
    }

}