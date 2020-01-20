package frc.robot.util.interfaces;

import frc.robot.util.PIDGain;

public interface IMercPIDTunable {
    public String getPIDName();
    public void putPIDGain();
    public void checkPIDGain();
}