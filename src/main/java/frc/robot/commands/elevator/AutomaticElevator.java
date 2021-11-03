/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.elevator;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.ElevatorPosition;
import frc.robot.util.interfaces.IMercMotorController;

public class AutomaticElevator extends CommandBase {

  private Elevator elevator;
  private final double ELEVATOR_THRESHOLD = Elevator.ElevatorPosition.TOP.encPos;
  private ElevatorPosition targetPos;
  private boolean endable;
  private boolean down;


  /**
   * Creates a new GoToSetPosition.
   */
  public AutomaticElevator(Elevator elevator, ElevatorPosition pos) {
    addRequirements(elevator);
    setName("AutomaticElevator");
    this.elevator = elevator;
    targetPos = pos;
    endable = true;
    if(elevator.getEncTicks() > pos.encPos)
      down = true;
    else
      down = false;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    if(down) {
      elevator.getElevatorLeader().set(ControlMode.MotionMagic, targetPos.encPos);
    }
    else {
      elevator.getElevatorLeader().setPosition(targetPos.encPos);
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (endable && ELEVATOR_THRESHOLD >= Math.abs(targetPos.encPos - elevator.getCurrentHeight())) {
      return true;
  }
  if (targetPos == Elevator.ElevatorPosition.BOTTOM) {
      if (elevator.getElevatorLeader().isLimitSwitchClosed(IMercMotorController.LimitSwitchDirection.REVERSE)) {
          elevator.getElevatorLeader().setPosition(targetPos.encPos);
          return true;
      }
  }
  return false;
  }
}
