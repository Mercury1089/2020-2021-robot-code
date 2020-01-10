/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drivetrain;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Robot;
import frc.robot.util.Requirements;
import frc.robot.util.DriveAssist.DriveDirection;
import java.util.Set;
import edu.wpi.first.wpilibj2.command.Subsystem;

public class SwitchDrive extends CommandBase {
    private DriveDirection dd;
    private Set<Subsystem> requirements;

    public SwitchDrive(DriveDirection driveDir) {
        dd = driveDir;
        requirements = new Requirements();
    }

    // Called just before this Command runs the first time
    @Override
    public void initialize() {
        Robot.driveTrain.setDirection(dd);
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    public void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    public boolean isFinished() {
        return true;
    }

    // Called once after isFinished returns true
    @Override
    public void end(boolean interrupted) {
    }

    public Set<Subsystem> getRequirements(){
        return this.requirements;
    }

    public void setRequirements(Set<Subsystem> requirements){
        this.requirements = requirements;
    }
}
