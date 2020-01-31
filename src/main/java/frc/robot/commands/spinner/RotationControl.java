/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.spinner;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.sensors.REVColor.ControlPanelColor;
import frc.robot.subsystems.Spinner;

public class RotationControl extends CommandBase {
  
  private Spinner spinner;
  private ControlPanelColor previousColor, currentColor;

  private int colorsCrossed;

  private final int MINIMUM_COLORS_CROSSED = 25;

  public RotationControl(Spinner spinner) {
    addRequirements(spinner);
    this.spinner = spinner;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    spinner.setSpeed(spinner.getRunSpeed());
    colorsCrossed = 0;
    currentColor = spinner.getColorSensor().get();
    previousColor = currentColor;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    updateColor();
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

  public void updateColor() {
    ControlPanelColor sensorColor = spinner.getColorSensor().get();
    if (previousColor == ControlPanelColor.YELLOW) {
      if (sensorColor == ControlPanelColor.RED) {
        previousColor = currentColor;
        currentColor = ControlPanelColor.RED;
        colorsCrossed++;
      }
    } 
    else if (previousColor == ControlPanelColor.RED) {
      if (sensorColor == ControlPanelColor.GREEN) {
        previousColor = currentColor;
        currentColor = ControlPanelColor.GREEN;
        colorsCrossed++;
      }
    }
    else if(previousColor == ControlPanelColor.GREEN) {
      if (sensorColor == ControlPanelColor.BLUE) {
        previousColor = currentColor;
        currentColor = ControlPanelColor.BLUE;
        colorsCrossed++;
      }
    }
    else if(previousColor == ControlPanelColor.BLUE) {
      if(sensorColor == ControlPanelColor.YELLOW) {
        previousColor = currentColor;
        currentColor = ControlPanelColor.YELLOW;
        colorsCrossed++;
      }
    }
  }
}
