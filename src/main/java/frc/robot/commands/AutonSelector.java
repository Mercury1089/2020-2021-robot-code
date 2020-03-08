/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.util.ShuffleDash;
import frc.robot.RobotContainer;

public class AutonSelector extends InstantCommand {
  /**
   * Creates a new AutonSelector.
   */
  public AutonSelector() {
    super();
    setName("AutonSelector");
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    //ShuffleDash.Autons auton = RobotContainer.getShuffleDash().getAuton();
  }

  public boolean runsWhenDisabled() {
    return true;
  }
}
