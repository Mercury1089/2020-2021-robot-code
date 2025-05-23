/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.RobotMap.CAN;

public class Feeder extends SubsystemBase {
  
  private TalonSRX feedWheel;
  private static final double RUN_SPEED = 1.0;

  /**
   * Creates a new Feeder.
   */
  public Feeder() {
    feedWheel = new TalonSRX(CAN.FEEDER);
    feedWheel.setInverted(false);
    feedWheel.setNeutralMode(NeutralMode.Brake);
    setName("Feeder");
  }

  public double getRunSpeed() {
    return RUN_SPEED;
  }

  public void runFeeder() {
    setSpeed(RUN_SPEED);
  }

  public void stopFeeder() {
    setSpeed(0.0);
  }

  public void setSpeed(double speed) {
    feedWheel.set(ControlMode.PercentOutput, speed);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    //SmartDashboard.putNumber("Speed", feedWheel.getSpeed());
  }
}
