/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auton;

import java.io.FileNotFoundException;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.drivetrain.MoveOnTrajectory;
import frc.robot.subsystems.DriveTrain;
import frc.robot.util.MercMotionProfile;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class TargetZoneToTrenchAndShoot extends SequentialCommandGroup {
  public TargetZoneToTrenchAndShoot(MercMotionProfile fTargetZoneToTrench, 
    MercMotionProfile fTrenchBall, MercMotionProfile bTrenchBall,
    MercMotionProfile fTrenchOtherBall, MercMotionProfile bTrenchOtherBall,
    MercMotionProfile bTrenchToTargetZone, DriveTrain driveTrain) throws FileNotFoundException {
      super(
        new MoveOnTrajectory(fTargetZoneToTrench, driveTrain), 
        new MoveOnTrajectory(fTrenchBall, driveTrain),
        new MoveOnTrajectory(bTrenchBall, driveTrain),
        new MoveOnTrajectory(fTrenchOtherBall, driveTrain),
        new MoveOnTrajectory(bTrenchOtherBall, driveTrain),
        new MoveOnTrajectory(bTrenchToTargetZone, driveTrain)
      );
  }
}
