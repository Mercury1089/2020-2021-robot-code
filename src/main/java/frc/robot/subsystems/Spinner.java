/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotMap.CAN;
import frc.robot.sensors.REVColor;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.util.interfaces.IMercShuffleBoardPublisher;

public class Spinner extends SubsystemBase implements IMercShuffleBoardPublisher {
  
  private IMercMotorController spinController;
  private REVColor colorSensor;
  private double runSpeed;

  /**
   * Creates a new Spinner.
   */
  public Spinner() {
    spinController = new MercTalonSRX(CAN.SPINNER);
    colorSensor = new REVColor();

    SmartDashboard.putNumber("Spin speed", 0.0);
  }

  public void setRunSpeed(double runSpeed) {
    this.runSpeed = runSpeed;
  }

  public double getRunSpeed() {
    return runSpeed;
  }

  public double getEncTicks() {
    return spinController.getEncTicks();
  }

  public void setSpeed(double speed) {
    spinController.setSpeed(speed);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  @Override
  public void publishValues() {
    SmartDashboard.putNumber("Spinner ticks", getEncTicks());
    SmartDashboard.putString("Detected Color", colorSensor.get().toString());
    SmartDashboard.putNumber("Color Confidence", colorSensor.getConfidence());
  }
}
