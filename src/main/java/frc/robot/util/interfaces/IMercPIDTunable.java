package frc.robot.util.interfaces;

import frc.robot.util.PIDGain;

public interface IMercPIDTunable {
    public PIDGain getPIDGain(String slot);
    public void setPIDGain(String slot, PIDGain gain);
}