/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.sensors.Limelight;
import frc.robot.sensors.Limelight.LimelightLEDState;
import frc.robot.subsystems.LimelightAssembly;

public class SetLED extends Command {

  private LimelightLEDState state;

  public SetLED(LimelightLEDState state) {
    requires(Robot.limelightAssembly);
    this.state = state;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    Robot.limelightAssembly.getLimeLight().setLEDState(this.state);
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return true;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    super.end();
  }

  // Called when another command which requires one or more of the satme
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    this.end();
  }
}
