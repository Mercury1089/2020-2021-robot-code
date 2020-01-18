package frc.robot.util.interfaces;

import frc.robot.util.PIDGain;

public interface IMercPidTunable {

    public PIDGain getPIDGain();
    public void setPIDGain(PIDGain pidGain);
}