
package frc.robot.commands.shooter;

import java.util.Set;

import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.Robot;
import frc.robot.commands.drivetrain.MoveHeading;


    
public class AutoAlign extends MoveHeading {

    Set<Subsystem> subsystems;

    public AutoAlign() {
        super(0,0);
    }

   // Called just before this Command runs the first time
   @Override
   public void initialize() {
       Robot.driveTrain.resetEncoders();
   }

    // Called repeatedly when this Command is scheduled to run
    @Override
    public void execute() {

    }
    // Make this return true when this Command no longer needs to run execute()
    @Override
    public boolean isFinished() {
        return false;  
    }

    // Called once after isFinished returns true
    @Override
    public void end(boolean interrupted) {
        Robot.driveTrain.stop();
        
    }

    @Override
    public void setRequirements(Set<Subsystem> requirements) {
        super.setRequirements(requirements);
    }
}