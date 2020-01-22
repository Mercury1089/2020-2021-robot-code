/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.spinner;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Spinner;

public class RotationControl extends CommandBase {
  
  private Spinner spinner;
  private Color previousColor, currentColor;

  private int colorsCrossed;

  private final int MINIMUM_COLORS_CROSSED = 25;
  private final double SPINNER_SPEED = 0.25;

  public RotationControl(Spinner spinner) {
    addRequirements(spinner);
    this.spinner = spinner;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    previousColor = spinner.getDetectedColor();
    currentColor = previousColor;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    currentColor = spinner.getDetectedColor();

    if(!currentColor.equals(previousColor)) {
      colorsCrossed++;
      previousColor = currentColor;
    }

    spinner.setSpeed(SPINNER_SPEED);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    spinner.setSpeed(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return colorsCrossed > MINIMUM_COLORS_CROSSED;
  }
}
