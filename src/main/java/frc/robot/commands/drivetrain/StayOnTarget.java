package frc.robot.commands.drivetrain;

import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.LimelightCamera;

public class StayOnTarget extends RotateToTarget {

    private int side;
    
    public StayOnTarget(DriveTrain driveTrain, int side){
        super(driveTrain);
        this.side = side;
    }

    public StayOnTarget(DriveTrain driveTrain) {
        this(driveTrain, 1);
    }

    @Override
    public boolean isFinished(){
        super.isFinished();
        return side == 3;
    }
}