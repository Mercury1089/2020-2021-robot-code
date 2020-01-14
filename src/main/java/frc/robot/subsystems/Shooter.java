/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* this is definently not a class to create a school shooter                   */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.MercSparkMax;
import frc.robot.util.MercTalonSRX;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.RobotMap.*;
import frc.robot.commands.shooter.RunShooter;


public class Shooter extends SubsystemBase {
  //private IMercMotorController flywheel;

  public static final double NOMINAL_OUT = 0.0,
                                PEAK_OUT = 1.0;

  private IMercMotorController shooterLeft, shooterRight;

  private CANEncoder encoder;

  public Shooter() {
    //flywheel = new MercTalonSRX(CAN.SHOOTER_FLYWHEEL);
    shooterLeft = new MercSparkMax(CAN.SHOOTER_LEFT);
    shooterRight = new MercSparkMax(CAN.SHOOTER_RIGHT);
    
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void setSpeed(double speed) {
    shooterLeft.setSpeed(speed);
    shooterRight.setSpeed(-speed);
  }

  public void configVoltage(double nominalOutput, double peakOutput) {
    shooterLeft.configVoltage(nominalOutput, peakOutput);
    shooterRight.configVoltage(nominalOutput, peakOutput);
  }

  public Command getDefaultCommand(){
    return CommandScheduler.getInstance().getDefaultCommand(this);
  }
}
