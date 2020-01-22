/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.sensors;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;

import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorMatch;

/**
 * Add your docs here.
 */
public class REVColor {

  private final I2C.Port i2cPort;
  private final ColorSensorV3 colorSensor;
  private final ColorMatch colorMatch;

  private final Color kBlueTarget;
  private final Color kGreenTarget;
  private final Color kRedTarget;
  private final Color kYellowTarget;
  
  private Color detectedColor;
  private double confidence = 0.0;
  private double confidenceWePutIntoTheColor;

  public REVColor() {

    i2cPort = I2C.Port.kOnboard;
    colorSensor = new ColorSensorV3(i2cPort);
    colorMatch = new ColorMatch();

    //Without Light
    kBlueTarget = ColorMatch.makeColor(0.2, 0.5, 0.3);
    kGreenTarget = ColorMatch.makeColor(0.25, 0.55, 0.2);
    kRedTarget = ColorMatch.makeColor(0.6, 0.3, 0.1);
    kYellowTarget = ColorMatch.makeColor(0.4, 0.5, 0.1);

    //With Light
    //We haven't tesed this yet
    //Once we will there will be values here
    //Unless we don't put them
    //And then this will still be here

    colorMatch.addColorMatch(kBlueTarget);
    colorMatch.addColorMatch(kGreenTarget);
    colorMatch.addColorMatch(kRedTarget);
    colorMatch.addColorMatch(kYellowTarget); 
    
    confidenceWePutIntoTheColor = 1.0;

    colorMatch.setConfidenceThreshold(confidenceWePutIntoTheColor);
  }


  public ControlPanelColor get() {
    detectedColor = colorSensor.getColor();
    ColorMatchResult match = colorMatch.matchColor(detectedColor);
    confidence = match.confidence;

    if (match.color == kBlueTarget)
      return ControlPanelColor.BLUE;
    else if (match.color == kRedTarget)
      return ControlPanelColor.RED;
    else if (match.color == kGreenTarget)
      return ControlPanelColor.GREEN;
    else if (match.color == kYellowTarget) 
      return ControlPanelColor.YELLOW;
    return ControlPanelColor.UNKNOWN;
  }

  public double getConfidence() {
    return confidence;
  }

  public Color getRawColor() {
    return colorSensor.getColor();
  }

  public Color getDetectedColor() {
    return detectedColor;
  }

  public enum ControlPanelColor {
    BLUE,
    RED,
    GREEN,
    YELLOW,
    UNKNOWN
  }
}