package frc.robot.commands.drivetrain;

import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.LimelightCamera;

public class StayOnTarget extends RotateToTarget {
    public StayOnTarget(DriveTrain driveTrain, LimelightCamera limelightCamera){
        super(driveTrain, limelightCamera);
    }

    @Override
    public boolean isFinished(){
        return false;
    }
}