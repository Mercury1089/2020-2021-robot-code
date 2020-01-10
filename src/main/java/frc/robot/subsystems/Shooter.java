/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* this is definently not a class to create a school shooter                   */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.MercTalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.RobotMap.*;


public class Shooter extends SubsystemBase {
  //private IMercMotorController flywheel;
  private CANSparkMax flywheel;
  /**
   * Creates a new Shooter.
   */
  public Shooter() {
    //flywheel = new MercTalonSRX(CAN.SHOOTER_FLYWHEEL);
    flywheel = new CANSparkMax(CAN.SHOOTER_FLYWHEEL, MotorType.kBrushless);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
/*
  public IMercMotorController getFlywheel() {
    return flywheel;
  } 
*/
  public CANSparkMax getFlywheel() {
    return flywheel;
  }
/*  
  public void setSpeed(double speed) {
    flywheel.setSpeed(speed);
  }
*/
public void setSpeed(double speed) {
  flywheel.set(speed);
}
}
