/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.feeder;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Feeder;

public class RunFeeder extends CommandBase {
  
  private Feeder feeder;

  /**
   * Creates a new RunFeeder.
   */
  public RunFeeder(Feeder feeder) {
    addRequirements(feeder);
    setName("RunFeeder");
    this.feeder = feeder;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    feeder.setSpeed(1.0);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    System.out.println("Running Feeder");
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
