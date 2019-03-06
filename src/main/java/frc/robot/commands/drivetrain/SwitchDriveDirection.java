/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drivetrain;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.limelight.RotateLimelight;
import frc.robot.subsystems.LimelightAssembly.LimelightPosition;
import frc.robot.util.DriveAssist.DriveDirection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SwitchDriveDirection extends CommandGroup {
  private final Logger LOG = LogManager.getLogger(SwitchDriveDirection.class);
  /**
   * Add your docs here.
   */
  public SwitchDriveDirection(DriveDirection driveDir) {
    // Add Commands here:
    // e.g. addSequential(new Command1());
    // addSequential(new Command2());
    // these will run in order.

    // To run multiple commands at the same time,
    // use addParallel()
    // e.g. addParallel(new Command1());
    // addSequential(new Command2());
    // Command1 and Command2 will run in parallel.

    // A command group will require all of the subsystems that each member
    // would require.
    // e.g. if Command1 requires chassis, and Command2 requires arm,
    // a CommandGroup containing them would require both the chassis and the
    // arm.

    addParallel(new SwitchDrive(driveDir));
    addSequential(new RotateLimelight(driveDir == DriveDirection.HATCH ? 
            LimelightPosition.FACING_HATCH_PANEL : LimelightPosition.FACING_CARGO));
            
    setName("SwitchDriveDirection CommandGroup");
    LOG.info(getName() + " Constructed");
  }
}