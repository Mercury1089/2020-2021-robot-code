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
import frc.robot.subsystems.Hopper;
import frc.robot.subsystems.Shooter;

public class AutoFeedBalls extends CommandBase {
  private Feeder feeder;
  private Hopper hopper;
  private Shooter shooter;
  private DriveTrain driveTrain;
  private int side;

  /**
   * Creates a new AutoFeedBalls.
   */
  public AutoFeedBalls(Feeder feeder, Hopper hopper, Shooter shooter, DriveTrain driveTrain, int side) {
    addRequirements(feeder);
    this.feeder = feeder;
    this.hopper = hopper;
    this.shooter = shooter;
    this.driveTrain = driveTrain;
    this.side = side;
  }

  public AutoFeedBalls(Feeder feeder, Hopper hopper, Shooter shooter, DriveTrain driveTrain) {
    this(feeder, hopper, shooter, driveTrain, 1);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if(side != 3) {
      if(shooter.atTargetRpm() && driveTrain.isAligned()) {
        feeder.setSpeed(feeder.getRunSpeed());
        hopper.setSpeed(hopper.getRunSpeed());
      }
      else {
        feeder.setSpeed(0.0);
        hopper.setSpeed(0.0);
      }
    }
    else{
      if(shooter.atTargetRpm()){
        feeder.setSpeed(feeder.getRunSpeed());
        hopper.setSpeed(hopper.getRunSpeed());
      }
    }
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
