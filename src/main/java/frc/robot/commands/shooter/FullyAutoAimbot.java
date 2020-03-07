/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.commands.drivetrain.RotateToTarget;
import frc.robot.commands.drivetrain.StayOnTarget;
import frc.robot.commands.feeder.AutoFeedBalls;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Feeder;
import frc.robot.subsystems.Hopper;
import frc.robot.subsystems.LimelightCamera;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.DriveTrain.ShootingStyle;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class FullyAutoAimbot extends ParallelCommandGroup {
  /**
   * Creates a new ShootFullyAutomatic.
   */
  public FullyAutoAimbot(DriveTrain driveTrain, Shooter shooter, Feeder feeder, Hopper hopper, ShootingStyle shootingStyle) {
    //Rotates to target and revs shooter to target rpm, THEN it runs the feeder and hopper
    super(new StayOnTarget(driveTrain, shootingStyle),
          new RunShooterRPMPID(shooter, driveTrain.getLimelight(), shootingStyle),
          new AutoFeedBalls(feeder, hopper, shooter, driveTrain, shootingStyle));
  }

  public FullyAutoAimbot(DriveTrain driveTrain, Shooter shooter, Feeder feeder, Hopper hopper) {
    //Rotates to target and revs shooter to target rpm, THEN it runs the feeder and hopper
    this(driveTrain, shooter, feeder, hopper, ShootingStyle.AUTOMATIC);
  }
}
