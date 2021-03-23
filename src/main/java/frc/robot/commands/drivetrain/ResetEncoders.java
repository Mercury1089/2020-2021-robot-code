package frc.robot.commands.drivetrain;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveTrain;

/**
 * https://youtu.be/NnP5iDKwuwk
 */
public class ResetEncoders extends CommandBase   {

    private DriveTrain driveTrain;
    
    public ResetEncoders(DriveTrain driveTrain) {
        super.addRequirements(driveTrain);
        this.driveTrain = driveTrain;
    }

    @Override
    public void initialize() {
        driveTrain.resetEncoders();
        driveTrain.resetPigeonYaw();
    }
    @Override
    public boolean isFinished(){
        return driveTrain.getPigeonYaw() == 0.0 && driveTrain.getLeftEncPositionInTicks() == 0.0 && driveTrain.getRightEncPositionInTicks() == 0.0;
    }
}