package frc.robot.util;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import frc.robot.subsystems.Shooter.ShooterMode;
import frc.robot.util.interfaces.IMercMotorController.LimitSwitchDirection;

public class ShuffleDash {

    private NetworkTableInstance ntInstance;
    private SendableChooser<String> sandstormFirstStep;
    private SendableChooser<Boolean> safetyMode;

    public ShuffleDash() {
        //new Notifier(this::updateDash).startPeriodic(0.020);

        ntInstance = NetworkTableInstance.getDefault();

        sandstormFirstStep = new SendableChooser<>();
        sandstormFirstStep.addOption("Left Close", "LeftClose");
        sandstormFirstStep.addOption("Left Middle", "LeftMiddle");
        sandstormFirstStep.addOption("Left Far", "LeftFar");
        sandstormFirstStep.addOption("Left Rocket Close", "LeftRocketClose");
        sandstormFirstStep.addOption("Left Rocket Far", "LeftRocketFar");
        sandstormFirstStep.addOption("Mid Left", "MidLeft");
        sandstormFirstStep.addOption("Mid Right", "MidRight");
        sandstormFirstStep.addOption("Right Close", "RightClose");
        sandstormFirstStep.addOption("Right Middle", "RightMiddle");
        sandstormFirstStep.addOption("Right Far", "RightFar");
        sandstormFirstStep.setDefaultOption("Straight", "StraightProfile");

        safetyMode = new SendableChooser<Boolean>();
        safetyMode.addOption("Safety Mode Enabled", true);
        safetyMode.addOption("Safety Mode Disabled", false);   
        safetyMode.setDefaultOption("Competition Mode", false);
    }

    public void updateDash() {
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

        SmartDashboard.putData("Sandstorm First Step", sandstormFirstStep);

        SmartDashboard.putData("Safety Mode", safetyMode);
    }

    public String getFirstStep() {
        return sandstormFirstStep.getSelected();
    }

    public boolean isSafetyModeEnabled() {
        return safetyMode.getSelected();
    } 
}
