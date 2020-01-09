package frc.robot.commands.drivetrain;

import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Set;
import frc.robot.Robot;
import frc.robot.util.Requirements;

public class CalibrateGyro implements Command {

    private Set<Subsystem> requirements;

    public CalibrateGyro(){
        requirements = new Requirements();

    }
    
    public void initialize() {
        //  System.out.println("Calibrating gyro...");
        Robot.driveTrain.getPigeon().enterCalibrationMode(PigeonIMU.CalibrationMode.BootTareGyroAccel);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
    
    @Override
    public boolean runsWhenDisabled(){
        return true;
    }

    public Set<Subsystem> getRequirements(){
        return this.requirements;
    }

    public void setRequirements(Set<Subsystem> requirements){
        this.requirements = requirements;
    }
}
