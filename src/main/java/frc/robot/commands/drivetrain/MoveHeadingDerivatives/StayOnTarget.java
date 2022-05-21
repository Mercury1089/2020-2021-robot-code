package frc.robot.commands.drivetrain.MoveHeadingDerivatives;

import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.DriveTrain.ShootingStyle;

/**
 * https://youtu.be/NnP5iDKwuwk
 */
public class StayOnTarget extends RotateToTarget {

    private ShootingStyle shootingStyle;
    
    public StayOnTarget(DriveTrain driveTrain, ShootingStyle shootingStyle){
        super(driveTrain);
        addRequirements(driveTrain);
        this.shootingStyle = shootingStyle;
        driveTrain.setShootingStyle(shootingStyle);
    }

    public StayOnTarget(DriveTrain driveTrain) {
        this(driveTrain, ShootingStyle.AUTOMATIC);
    }
    
    @Override
    public void execute() {
        if(shootingStyle == ShootingStyle.AUTOMATIC) {
            super.execute();
        }
    }

    @Override
    public boolean isFinished(){
        return(false);
    }
}