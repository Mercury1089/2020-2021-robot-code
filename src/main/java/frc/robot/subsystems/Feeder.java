/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.RobotMap.CAN;

public class Feeder extends SubsystemBase {
  
  private IMercMotorController feedWheel;
  private double runSpeed;

  /**
   * Creates a new Feeder.
   */
  public Feeder() {
    feedWheel = new MercTalonSRX(CAN.FEEDER);
    runSpeed = 0.5;
  }

  public void setSpeed(double speed) {
    feedWheel.setSpeed(speed);
  }

  public double getRunSpeed() {
    return runSpeed;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
