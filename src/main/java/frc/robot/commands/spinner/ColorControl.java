/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.spinner;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.sensors.REVColor.ControlPanelColor;
import frc.robot.subsystems.Spinner;

public class ColorControl extends CommandBase {

  private Spinner spinner;
  private ControlPanelColor color;
  private String fmsColor;

  private int timeWithoutColor;
  private final int UNKNOWN_THRESHOLD = 5;

  private int ticksAtTarget;
  private final int TARGET_THRESHOLD = 1;
  
  /**
   * Creates a new ColorControl.
   */
  public ColorControl(Spinner spinner) {
    addRequirements(spinner);
    this.spinner = spinner;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    spinner.setSpeed(spinner.getRunSpeed());
    color = getColor();
    ticksAtTarget = 0;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
  }
  
  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    spinner.setSpeed(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (ticksAtTarget >= TARGET_THRESHOLD) {
      ticksAtTarget = 0;
      return true;
    }
    if (spinner.getColorSensor().get() == color) 
      ticksAtTarget++;
    return false;
  }

  public ControlPanelColor getColor() {
    fmsColor = DriverStation.getInstance().getGameSpecificMessage();
        if(fmsColor.length() > 0)
            switch(fmsColor.charAt(0)) {
                case 'R':
                    return ControlPanelColor.BLUE;
                case 'G':
                  return ControlPanelColor.YELLOW;
                case 'B':
                  return ControlPanelColor.RED;
                case 'Y':
                  return ControlPanelColor.GREEN;
                default:
                  return ControlPanelColor.UNKNOWN;
            }
        return ControlPanelColor.UNKNOWN;
  }
}
