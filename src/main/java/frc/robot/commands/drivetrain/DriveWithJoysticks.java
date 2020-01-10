package frc.robot.commands.drivetrain;

import java.util.Set;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.Robot;
import frc.robot.RobotMap.DS_USB;
import frc.robot.subsystems.DriveTrain;
import frc.robot.util.*;

/**
 * Command that puts the drive train into a manual control mode.
 * This puts the robot in arcade drive.
 */
public class DriveWithJoysticks extends CommandBase {
    private DriveAssist tDrive;
    //private DelayableLogger everySecond = new DelayableLogger(log, 10, TimeUnit.SECONDS);
    private DriveType driveType;

    public DriveWithJoysticks(DriveType type) {
        super();
        super.addRequirements(Robot.driveTrain);
        driveType = type;
        System.out.println("DriveWithJoysticks init");
    }

    // Called just before this Command runs the first time
    @Override
    public void initialize() {
        Robot.driveTrain.configVoltage(DriveTrain.NOMINAL_OUT, DriveTrain.PEAK_OUT);
        tDrive = Robot.driveTrain.getDriveAssist();
        Robot.driveTrain.setNeutralMode(NeutralMode.Brake);
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    public void execute() {
        if (tDrive != null) {
            switch (driveType) {
                case TANK:
                    tDrive.tankDrive(Robot.oi.getJoystickY(DS_USB.LEFT_STICK), Robot.oi.getJoystickY(DS_USB.RIGHT_STICK));
                    break;
                case ARCADE:
                    tDrive.arcadeDrive(-Robot.oi.getJoystickY(DS_USB.LEFT_STICK), Robot.oi.getJoystickX(DS_USB.RIGHT_STICK), true);
                    break;
            }
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    public boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    @Override
    public void end(boolean interrupted) {
        Robot.driveTrain.setNeutralMode(NeutralMode.Brake);
        Robot.driveTrain.stop();
    }

    public enum DriveType {
        TANK,
        ARCADE
    }
}
