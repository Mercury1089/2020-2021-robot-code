/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.sensors.Limelight;

public class LimelightCamera extends SubsystemBase {

  private Limelight limelight;

  public LimelightCamera() {
    this.limelight = new Limelight();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public Limelight getLimelight(){
    return limelight;
  }
}
