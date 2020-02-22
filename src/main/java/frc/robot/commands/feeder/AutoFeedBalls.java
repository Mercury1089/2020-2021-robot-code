/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.feeder;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Feeder;
import frc.robot.subsystems.Shooter;

public class AutoFeedBalls extends CommandBase {
  private Feeder feeder;
  private Shooter shooter;
  private DriveTrain driveTrain;

  /**
   * Creates a new AutoFeedBalls.
   */
  public AutoFeedBalls(Feeder feeder, Shooter shooter, DriveTrain driveTrain) {
    addRequirements(feeder);
    this.feeder = feeder;
    this.shooter = shooter;
    this.driveTrain = driveTrain;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if(shooter.atTargetRpm() && driveTrain.isAligned())
      feeder.setSpeed(feeder.getRunSpeed());
    else
      feeder.setSpeed(0.0);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    feeder.setSpeed(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
