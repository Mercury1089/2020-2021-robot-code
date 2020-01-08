package frc.robot.commands.drivetrain;

import com.ctre.phoenix.sensors.PigeonIMU;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

public class CalibrateGyro extends Command {

    public CalibrateGyro() {
        setRunWhenDisabled(true);
        setName("CalibrateGyro Command");
    }

    public void initialize() {
        //  System.out.println("Calibrating gyro...");
        Robot.driveTrain.getPigeon().enterCalibrationMode(PigeonIMU.CalibrationMode.BootTareGyroAccel);
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
