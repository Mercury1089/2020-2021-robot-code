package frc.robot.util;

import java.util.List;
import java.util.ArrayList;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.util.interfaces.IMercPIDTunable;
import frc.robot.util.interfaces.IMercShuffleBoardPublisher;

public class ShuffleDash {

    private static final String PID_TUNER = "Tune PID";
    private static final String PID_TUNER_P = "Tune PID kP";
    private static final String PID_TUNER_I = "Tune PID kI";
    private static final String PID_TUNER_D = "Tune PID kD";
    private static final String PID_TUNER_F = "Tune PID kF";

    private class TunablePIDSlot {
        public IMercPIDTunable tunable;
        public int slot;
        public TunablePIDSlot(IMercPIDTunable tunable, int slot) {
            this.tunable = tunable;
            this.slot = slot;
        }
    }
    private TunablePIDSlot tunableSlot = null;

    private NetworkTableInstance ntInstance;
    private SendableChooser<String> autonFirstStep;
    private List<IMercShuffleBoardPublisher> publishers;
    private SendableChooser<TunablePIDSlot> tunablePIDChooser;

    public ShuffleDash() {
        new Notifier(this::updateDash).startPeriodic(0.020);

        ntInstance = NetworkTableInstance.getDefault();

        autonFirstStep = new SendableChooser<>();

        publishers = new ArrayList<IMercShuffleBoardPublisher>();

        tunablePIDChooser = new SendableChooser<TunablePIDSlot>();
        tunablePIDChooser.setDefaultOption("NONE", null);
        SmartDashboard.putData(PID_TUNER, tunablePIDChooser);
        SmartDashboard.putNumber(PID_TUNER_P, 0.0);
        SmartDashboard.putNumber(PID_TUNER_I, 0.0);
        SmartDashboard.putNumber(PID_TUNER_D, 0.0);
        SmartDashboard.putNumber(PID_TUNER_F, 0.0);
    }

    public void addPublisher(IMercShuffleBoardPublisher publisher) {
        publishers.add(publisher);
    }

    public void addPIDTunable(IMercPIDTunable pidTunable, String pidName){
        for(int slot : pidTunable.getSlots()) {
            tunablePIDChooser.addOption(pidName + "." + Integer.toString(slot), new TunablePIDSlot(pidTunable, slot));
        }
    }

    public void updateDash() {
        
        for(IMercShuffleBoardPublisher publisher: publishers) {
            publisher.publishValues();
        }

        // PID Tuner
        TunablePIDSlot tunableSlot = tunablePIDChooser.getSelected();
        if (tunableSlot != null) {
            if (tunableSlot != this.tunableSlot) {
                PIDGain pid = tunableSlot.tunable.getPIDGain(tunableSlot.slot);
                SmartDashboard.putNumber(PID_TUNER_P, pid.kP);
                SmartDashboard.putNumber(PID_TUNER_I, pid.kI);
                SmartDashboard.putNumber(PID_TUNER_D, pid.kD);
                SmartDashboard.putNumber(PID_TUNER_F, pid.kF);
            } else {
                PIDGain pid = new PIDGain(
                    SmartDashboard.getNumber(PID_TUNER_P, 0.0),
                    SmartDashboard.getNumber(PID_TUNER_I, 0.0),
                    SmartDashboard.getNumber(PID_TUNER_D, 0.0),
                    SmartDashboard.getNumber(PID_TUNER_F, 0.0));
                tunableSlot.tunable.setPIDGain(tunableSlot.slot, pid);
            }
            this.tunableSlot = tunableSlot;
        }
        SmartDashboard.putData("Auton First Step", autonFirstStep);
    }

    public String getFirstStep() {
        return autonFirstStep.getSelected();
        
    }
}
