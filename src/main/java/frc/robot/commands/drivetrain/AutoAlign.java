
package frc.robot.commands.drivetrain;

import frc.robot.subsystems.DriveTrain;

public class AutoAlign extends DegreeRotate {

    private DriveTrain driveTrain;
    private final double ANGLE_THRESHOLD;


    public AutoAlign(DriveTrain driveTrain) {
        super(0, driveTrain);

        this.setName("AutoAlign");
        this.driveTrain = driveTrain;

        ANGLE_THRESHOLD = 3.0; //CHANGE THIS VALUE 
    }

   // Called just before this Command runs the first time
   @Override   
   public void initialize() {
       this.driveTrain.resetEncoders();
       super.initialize();
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
        this.driveTrain.stop();
    }
}
