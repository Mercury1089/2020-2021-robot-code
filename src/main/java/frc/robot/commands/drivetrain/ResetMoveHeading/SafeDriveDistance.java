// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.drivetrain.ResetMoveHeading;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.drivetrain.ResetEncoders;
import frc.robot.commands.drivetrain.MoveHeadingDerivatives.DriveDistance;
import frc.robot.subsystems.DriveTrain;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class SafeDriveDistance extends SequentialCommandGroup {
  /** Creates a new SafeDriveDistance. */
  public SafeDriveDistance(double distance, DriveTrain driveTrain) {
    addCommands(new ResetEncoders(driveTrain), new DriveDistance(distance, driveTrain));
  }
}
