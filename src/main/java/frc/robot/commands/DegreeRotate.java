/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import frc.robot.Robot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.util.MercMath;

public class DegreeRotate extends MoveHeading {
  public DegreeRotate(double angleToTurn) {
    super(0, angleToTurn);

    requires(Robot.driveTrain);

    MOVE_THRESHOLD = 100;
    ANGLE_THRESHOLD = 2;
    ON_TARGET_MINIMUM_COUNT = 3;
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    super.initialize();
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    super.execute();
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    double angleError = right.getClosedLoopError(DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);

    angleError = MercMath.pigeonUnitsToDegrees(angleError);

    boolean isFinished = false;

    boolean isOnTarget = (Math.abs(angleError) < ANGLE_THRESHOLD);

    if (isOnTarget) {
      onTargetCount++;
    } else {
      if (onTargetCount > 0)
        onTargetCount = 0;
    }

    if (onTargetCount > ON_TARGET_MINIMUM_COUNT) {
      isFinished = true;
      onTargetCount = 0;
    }

    return isFinished;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    super.end();
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    this.end();
  }
}
