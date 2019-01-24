package frc.CheeseLog;

public interface LogCondition {
    /**
     * Retrieves whether frc.CheeseLog should log the value
     * @return true if the frc.CheeseLog should log it; false otherwise.
     */
    boolean getCondition();
}
