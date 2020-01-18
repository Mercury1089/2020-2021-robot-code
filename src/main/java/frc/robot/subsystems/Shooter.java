/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* this is definently not a class to create a school shooter                   */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.MercSparkMax;
import frc.robot.util.MercTalonSRX;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.util.interfaces.IMercShuffleBoardPublisher;
import frc.robot.Robot;
import frc.robot.RobotMap.*;
import frc.robot.commands.shooter.RunShooter;


public class Shooter extends SubsystemBase implements IMercShuffleBoardPublisher{
  //private IMercMotorController flywheel;

  public static final double NOMINAL_OUT = 0.0,
                                PEAK_OUT = 1.0;

  private IMercMotorController shooterLeft, shooterRight;

  private double currentSpeed;
  private double runSpeed;
  
  private ShooterMode mode;


  public enum ShooterMode{
    OVER_THE_TOP,
    THROUGH_MIDDLE
  }


  public Shooter(ShooterMode mode) {
    //flywheel = new MercTalonSRX(CAN.SHOOTER_FLYWHEEL);
    shooterLeft = new MercSparkMax(CAN.SHOOTER_LEFT);
    shooterRight = new MercSparkMax(CAN.SHOOTER_RIGHT);
    configVoltage(NOMINAL_OUT, PEAK_OUT);
    this.mode = mode;
    shooterLeft.setNeutralMode(NeutralMode.Coast);
    shooterRight.setNeutralMode(NeutralMode.Coast);

    setRunSpeed(0.0);
    
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void setSpeed(double speed) {
    this.currentSpeed = speed;

    shooterLeft.setNeutralMode(NeutralMode.Coast);
    shooterRight.setNeutralMode(NeutralMode.Coast);

    if (mode == ShooterMode.OVER_THE_TOP) {
        shooterLeft.setSpeed(speed);
        shooterRight.setSpeed(-speed);
    }
    else if (mode == ShooterMode.THROUGH_MIDDLE) {
      shooterLeft.setSpeed(speed);
      shooterRight.setSpeed(speed);
    }
  }

  public void configVoltage(double nominalOutput, double peakOutput) {
    shooterLeft.configVoltage(nominalOutput, peakOutput);
    shooterRight.configVoltage(nominalOutput, peakOutput);
  }

  public void increaseSpeed(){
    currentSpeed += 0.05;
    this.setSpeed(currentSpeed);
  }

  public void decreaseSpeed(){
    currentSpeed -= 0.05;
    this.setSpeed(currentSpeed);
  }

  public double getRPM(){
    return shooterLeft.getEncVelo();
  }

  public Command getDefaultCommand(){
    return CommandScheduler.getInstance().getDefaultCommand(this);
  }

  public void setDefaultCommand(Command command){
    CommandScheduler.getInstance().setDefaultCommand(this, command);
  }

  public void setRunSpeed(double runSpeed){
    SmartDashboard.putNumber("Shooting speed", 0.0);
  }

  public double getRunSpeed() {
    return SmartDashboard.getNumber("Shooting speed", 0.0);
  }

  public ShooterMode getMode() {
    return mode;
  }

  public void publishValues() {
    SmartDashboard.putString("Shooter mode", getMode() == ShooterMode.OVER_THE_TOP ? "Over the top" : "Through the middle");
    SmartDashboard.putNumber("Shooter RPM", getRPM());
  }
}
