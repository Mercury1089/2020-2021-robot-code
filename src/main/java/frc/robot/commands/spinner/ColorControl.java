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
  
  /**
   * Creates a new ColorControl.
   */
  public ColorControl(Spinner spinner) {
    addRequirements(spinner);
    this.spinner = spinner;
    fmsColor = DriverStation.getInstance().getGameSpecificMessage();
        if(fmsColor.length() > 0)
            switch(fmsColor.charAt(0)) {
                case 'R':
                    color = ControlPanelColor.BLUE;
                    break;
                case 'G':
                  color = ControlPanelColor.YELLOW;
                  break;
                case 'B':
                  color = ControlPanelColor.RED;
                  break;
                case 'Y':
                  color = ControlPanelColor.GREEN;
                  break;
                default:
                  color = ControlPanelColor.UNKNOWN;
            }
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    spinner.setSpeed(spinner.getRunSpeed());
  }
  
  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    spinner.setSpeed(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    boolean noColor = false;
    if (color == ControlPanelColor.UNKNOWN) {
      if (timeWithoutColor >= UNKNOWN_THRESHOLD) {
        noColor = true;
      }
    }
    else noColor = false;
    return spinner.getColorSensor().get() == color && noColor;
  }
}
