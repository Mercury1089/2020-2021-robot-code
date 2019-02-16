/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.hatchpanel;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.RobotMap.GAMEPAD_AXIS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ManualHatchPanelArticulator extends Command {
  private final Logger LOG = LogManager.getLogger(ManualHatchPanelArticulator.class);
  public ManualHatchPanelArticulator() {
    requires(Robot.hatchManipulator);
    setName("ManualHatchPanelArticulator Command");
    LOG.info(getName() + " Constructed");
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    Robot.hatchManipulator.setArticulatorSpeed(Robot.oi.getGamepadAxis(GAMEPAD_AXIS.rightY));
    LOG.info(getName() + " Executed");
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    LOG.info(getName() + " Ended");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    LOG.info(getName() + " Interrupted");
    this.end();
  }
}
