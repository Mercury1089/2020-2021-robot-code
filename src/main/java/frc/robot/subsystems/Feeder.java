/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.MercSparkMax;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.RobotMap.CAN;

public class Feeder extends SubsystemBase {
  
  private IMercMotorController feedWheel;
  private double runSpeed;

  /**
   * Creates a new Feeder.
   */
  public Feeder() {
    feedWheel = new MercSparkMax(CAN.FEEDER);
    runSpeed = 0.5;
    setName("Feeder");
    setRunSpeed(runSpeed);
  }

  public double getRunSpeed() {
    return runSpeed;
  }

  public void setRunSpeed(double runSpeed) {
    SmartDashboard.putNumber(getName() + "/RunSpeed", runSpeed);
  }

  public double getRunSpeedSD() {
    return SmartDashboard.getNumber(getName() + "/RunSpeed", 0.0);
  }

  public void setSpeed(double speed) {
    this.runSpeed = speed;

    feedWheel.setNeutralMode(NeutralMode.Coast);

    feedWheel.setSpeed(speed);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
