package frc.robot.util;

import java.util.List;
import java.util.ArrayList;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.util.interfaces.IMercPIDTunable;
import frc.robot.util.interfaces.IMercShuffleBoardPublisher;
import frc.robot.Robot;

public class ShuffleDash {

    private NetworkTableInstance ntInstance;
    private SendableChooser<String> autonFirstStep;
    private List<IMercShuffleBoardPublisher> publishers;
    private List<IMercPIDTunable> pidTunables;
    private SendableChooser<IMercPIDTunable> subsystemPIDTuneChooser;

    public ShuffleDash() {
        new Notifier(this::updateDash).startPeriodic(0.020);

        ntInstance = NetworkTableInstance.getDefault();

        autonFirstStep = new SendableChooser<>();

        publishers = new ArrayList<IMercShuffleBoardPublisher>();
        pidTunables = new ArrayList<IMercPIDTunable>();

        subsystemPIDTuneChooser = new SendableChooser<IMercPIDTunable>();
        subsystemPIDTuneChooser.setDefaultOption("NONE", null);
    }

    public void addPublisher(IMercShuffleBoardPublisher publisher) {
        publishers.add(publisher);
    }

    public void addPIDTunable(IMercPIDTunable pidTunable){
        pidTunables.add(pidTunable);
        subsystemPIDTuneChooser.addOption(pidTunable.getPIDName(), pidTunable);
    }

    public void updateDash() {
        
        for(IMercShuffleBoardPublisher publisher: publishers) {
            publisher.publishValues();
        }

        SmartDashboard.putData("Auton First Step", autonFirstStep);
    }

    public String getFirstStep() {
        return autonFirstStep.getSelected();
        
    }
}
