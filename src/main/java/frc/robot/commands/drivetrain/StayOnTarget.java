package frc.robot.commands.drivetrain;

import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.LimelightCamera;
import frc.robot.subsystems.DriveTrain.ShootingStyle;

public class StayOnTarget extends RotateToTarget {

    private ShootingStyle shootingStyle;
    
    public StayOnTarget(DriveTrain driveTrain, ShootingStyle shootingStyle){
        super(driveTrain);
        this.shootingStyle = shootingStyle;
    }

    public StayOnTarget(DriveTrain driveTrain) {
        this(driveTrain, ShootingStyle.AUTOMATIC);
    }
    
    @Override
    public boolean isFinished(){
        super.isFinished();
        return shootingStyle == ShootingStyle.MANUAL;
    }
}