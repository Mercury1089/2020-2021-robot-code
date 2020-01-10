package frc.robot.commands.drivetrain;

import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Set;
import frc.robot.Robot;
import frc.robot.util.Requirements;

public class CalibrateGyro extends CommandBase {

    public CalibrateGyro(){
        
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

}
