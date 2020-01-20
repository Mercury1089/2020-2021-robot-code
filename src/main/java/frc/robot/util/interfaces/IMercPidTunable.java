package frc.robot.util.interfaces;

import frc.robot.util.PIDGain;

public interface IMercPIDTunable {
    public String getPIDName();
    public PIDGain getPIDGain();
    public void setPIDGain(PIDGain gain);
}