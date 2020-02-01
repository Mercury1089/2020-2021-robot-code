/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.spinner;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.sensors.REVColor.ControlPanelColor;
import frc.robot.subsystems.Spinner;

public class RotationControl extends CommandBase{
  
  private Spinner spinner;
  private ControlPanelColor sensorColor, currentColor, nextColor;

  private int colorsCrossed;

  private final int MINIMUM_COLORS_CROSSED = 24;

  public RotationControl(Spinner spinner) {
    addRequirements(spinner);
    this.spinner = spinner;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    spinner.setSpeed(0.05);
    colorsCrossed = 0;
    sensorColor = spinner.getColorSensor().get();
    currentColor = sensorColor;
    if(currentColor == ControlPanelColor.RED)
        nextColor = ControlPanelColor.GREEN;
    else if(currentColor == ControlPanelColor.GREEN)
      nextColor = ControlPanelColor.BLUE;
    else if(currentColor == ControlPanelColor.BLUE)
      nextColor = ControlPanelColor.YELLOW;
    else if(currentColor == ControlPanelColor.YELLOW)
      nextColor = ControlPanelColor.RED;
}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    sensorColor = spinner.getColorSensor().get(); 
    //check if the color seen is the next color on the wheel   
    if(sensorColor == nextColor){
      currentColor = nextColor;
      colorsCrossed++;
      //assign the next color based on what the current color is
      if(currentColor == ControlPanelColor.RED)
        nextColor = ControlPanelColor.GREEN;
      else if(currentColor == ControlPanelColor.GREEN)
        nextColor = ControlPanelColor.BLUE;
      else if(currentColor == ControlPanelColor.BLUE)
        nextColor = ControlPanelColor.YELLOW;
      else if(currentColor == ControlPanelColor.YELLOW)
        nextColor = ControlPanelColor.RED;
    }
    spinner.setColorsCrossed(colorsCrossed);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    spinner.setSpeed(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return colorsCrossed == MINIMUM_COLORS_CROSSED;
  }
}
