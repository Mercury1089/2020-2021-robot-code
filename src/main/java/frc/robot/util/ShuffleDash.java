package frc.robot.util;

import java.util.List;
import java.util.ArrayList;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.util.interfaces.IMercShuffleBoardPublisher;
import frc.robot.Robot;

public class ShuffleDash {

    private NetworkTableInstance ntInstance;
    private SendableChooser<String> autonFirstStep;
    private List<IMercShuffleBoardPublisher> publishers;

    public ShuffleDash() {
        new Notifier(this::updateDash).startPeriodic(0.020);

        ntInstance = NetworkTableInstance.getDefault();

        autonFirstStep = new SendableChooser<>();

        publishers = new ArrayList<IMercShuffleBoardPublisher>();
    }

    public void updateDash() {
        
        for(IMercShuffleBoardPublisher publisher: publishers) {
            publisher.publishValues();
        }
        
        // SmartDashboard.putString("Alliance Color", DriverStation.getInstance().getAlliance().toString());

        // SmartDashboard.putNumber("Left Enc in ticks", Robot.driveTrain.getLeftLeader().getEncTicks());
        // SmartDashboard.putNumber("Right Enc in ticks", Robot.driveTrain.getRightLeader().getEncTicks());
        
        SmartDashboard.putString("direction", Robot.driveTrain.getDirection().name());

        SmartDashboard.putNumber("Left Enc in feet", Robot.driveTrain.getLeftEncPositionInFeet());
        SmartDashboard.putNumber("Right Enc in feet", Robot.driveTrain.getRightEncPositionInFeet());

        SmartDashboard.putNumber("Left Wheel RPM", MercMath.ticksPerTenthToRevsPerMinute(Robot.driveTrain.getLeftLeader().getEncVelo()));
        SmartDashboard.putNumber("Right Wheel RPM", MercMath.ticksPerTenthToRevsPerMinute(Robot.driveTrain.getRightLeader().getEncVelo()));


        SmartDashboard.putNumber("Gyro Angle", Robot.driveTrain.getPigeonYaw());

        SmartDashboard.putString("FrontCamera", (Robot.driveTrain.getDirection() == DriveAssist.DriveDirection.HATCH) ? "Panel" : "Cargo");
        SmartDashboard.putString("BackCamera", (Robot.driveTrain.getDirection() == DriveAssist.DriveDirection.HATCH) ? "Cargo" : "Panel");

        SmartDashboard.putData("Auton First Step", autonFirstStep);

        

    }

    public void addPublisher(IMercShuffleBoardPublisher publisher) {
        publishers.add(publisher);
    }

    public String getFirstStep() {
        return autonFirstStep.getSelected();
    }
}
