/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/* this is definently not a class to create a school shooter                  */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel;
import com.revrobotics.CANSparkBase.ControlType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


import frc.robot.util.interfaces.IMercPIDTunable;
import frc.robot.util.interfaces.IMercShuffleBoardPublisher;
import frc.robot.util.PIDGain;

import frc.robot.RobotMap.*;
import frc.robot.sensors.Limelight;

public class Shooter extends SubsystemBase implements IMercShuffleBoardPublisher, IMercPIDTunable {
  // private IMercMotorController flywheel;

  public static final double NOMINAL_OUT = 0.0, PEAK_OUT = 1.0;
  public static final double MAX_RPM = 5000.0, STEADY_RPM = 3600.0, LOW_RPM = 1000.0, NULL_RPM = -1.0;
  public static final double MIN_DISTANCE = 6.7, MAX_DISTANCE = 17.0;
  //public static final double MIN_DISTANCE = 2.0, MAX_DISTANCE = 20.0;
  public final int BREAKBEAM_DIO = 2;
  private final double TARGET_VELOCITY_THRESHOLD = 50.0; // within a +- 50 rpm range to shoot
  private final double MAX_VOLTAGE = 10.5;
  private CANSparkMax shooterLeft, shooterRight;

  private double targetVelocity;

  private ShooterMode mode;

  private PIDGain velocityGains;

  private Limelight limelight;

  public enum ShooterMode {
    ONE_WHEEL, NONE
  }

  public Shooter(ShooterMode mode, Limelight limelight) {
    setName("Shooter");
    this.mode = mode;

    if (mode == ShooterMode.ONE_WHEEL) {
      shooterLeft = new CANSparkMax(CAN.SHOOTER_LEFT, MotorType.kBrushless);
      shooterRight = new CANSparkMax(CAN.SHOOTER_RIGHT, MotorType.kBrushless);

      shooterLeft.enableVoltageCompensation(MAX_VOLTAGE);
      shooterRight.enableVoltageCompensation(MAX_VOLTAGE);

      shooterLeft.getPIDController().setOutputRange(NOMINAL_OUT, PEAK_OUT);
      shooterRight.getPIDController().setOutputRange(NOMINAL_OUT, PEAK_OUT);

      shooterLeft.setIdleMode(IdleMode.kCoast);
      shooterRight.setIdleMode(IdleMode.kCoast);

      shooterLeft.setInverted(true);
      shooterRight.follow(shooterLeft, true); // Follow inverted
    } else if (mode == ShooterMode.NONE) {
      shooterLeft = shooterRight = null;
    }


    SmartDashboard.putNumber(getName() + "/SetRPM", 0.0);

    stopShooter();
    targetVelocity = 0.0;
    // velocityGains = new PIDGain(1e-5, 2e-7, 1e-5, 0);
    velocityGains = new PIDGain(0.00024, 0.00000001, 0.01, 0.0002025);    

    this.limelight = limelight;
    setPIDGain(SHOOTER_PID_SLOTS.VELOCITY_GAINS.getValue(), velocityGains);
  }

  @Override
  public void periodic() {

  }

  public void setVelocity(double velocity) {
    if (shooterLeft != null && shooterRight != null)
    {
      // Record the target velocity for atTargetRPM()
      targetVelocity = velocity;
      // If the target velocity is outside the valid range, run at steady rate.
      double setVelocity = velocity != NULL_RPM && velocity <= MAX_RPM ? velocity : STEADY_RPM;
      shooterLeft.getPIDController().setReference(setVelocity, ControlType.kVelocity);
    }
  }

  public void stopShooter() {
    targetVelocity = 0.0;
    shooterLeft.stopMotor();
  }

  public double getRPM() {
    return shooterLeft != null ? shooterLeft.getEncoder().getVelocity() : 0.0;
  }

  public boolean isReadyToShoot(){
    return atTargetRpm();
  }

  public double getTargetVelocity() {
    double distance = limelight.getRawVertDistance();
    return distance >= MIN_DISTANCE && distance <= MAX_DISTANCE ? getVelocityFromDistance(distance) : NULL_RPM;
  }

  public double getTargetRPMFromHypothetical() {
    double distance = getHyotheticalDistance();
    return -2.93032197e-09*Math.pow(distance, 6) + 3.21815380e-06*Math.pow(distance, 5) - 1.40572567e-03*Math.pow(distance, 4) + 3.06747428e-01*Math.pow(distance, 3) - 3.38724423e+01*Math.pow(distance, 2) + 1.60699276e+03*distance - 9.44326999e+03;
  }

  public void setTargetVelocity(double rpm) {
    targetVelocity = rpm;
  }

  public double getVelocityFromDistance(double distance) {
    return -2.93032197e-09*Math.pow(distance, 6) + 3.21815380e-06*Math.pow(distance, 5) - 1.40572567e-03*Math.pow(distance, 4) + 3.06747428e-01*Math.pow(distance, 3) - 3.38724423e+01*Math.pow(distance, 2) + 1.60699276e+03*distance - 9.44326999e+03;
  }

  public boolean atTargetRpm() {
    return Math.abs(getRPM() - getTargetVelocity()) <= 0.01 * getTargetVelocity();
  }

  public Command getDefaultCommand() {
    return CommandScheduler.getInstance().getDefaultCommand(this);
  }

  public void setDefaultCommand(Command command) {
    CommandScheduler.getInstance().setDefaultCommand(this, command);
  }

  public void setRunSpeed(double runSpeed) {
    SmartDashboard.putNumber(getName() + "/RunSpeed", 0.0);
  }

  public double getRunSpeed() {
    return SmartDashboard.getNumber(getName() + "/RunSpeed", 0.0);
  }

  public double getRunRPM() {
    return SmartDashboard.getNumber(getName() + "/SetRPM", 0.0);
  }

  public double getHyotheticalDistance() {
    return SmartDashboard.getNumber("Hypothetical Distance", 0.0);
  }

  public ShooterMode getMode() {
    return mode;
  }

  public void publishValues() {
    //SmartDashboard.putString(getName() + "/ShooterMode",
        //getMode() == ShooterMode.ONE_WHEEL ? "ONE WHEEL" : "NONE");
    SmartDashboard.putNumber(getName() + "/RPM", getRPM());
    
    //SmartDashboard.putNumber(getName() + "/PIDGains/P", velocityGains.kP);
    //SmartDashboard.putNumber(getName() + "/PIDGains/I", velocityGains.kI);
    //SmartDashboard.putNumber(getName() + "/PIDGains/D", velocityGains.kD);
    //SmartDashboard.putNumber(getName() + "/PIDGains/F", velocityGains.kF);

    SmartDashboard.putBoolean(getName() + "/AtTargetRPM", atTargetRpm());
    SmartDashboard.putNumber(getName() + "/TargetRPM", targetVelocity);
    SmartDashboard.putBoolean(getName() + "/IsReadtToShoot", isReadyToShoot());
    
    //SmartDashboard.putNumber("Hypothetical Distance", getHyotheticalDistance());
    //SmartDashboard.putNumber("Hypothetical RPM", getTargetRPMFromHypothetical());
  }

  @Override
  public PIDGain getPIDGain(int slot) {
    return this.velocityGains;
  }
  private void configPID(CANSparkMax sparkmax, int slot, PIDGain gains) {
    sparkmax.getPIDController().setP(gains.kP, slot);
    sparkmax.getPIDController().setI(gains.kI, slot);
    sparkmax.getPIDController().setD(gains.kD, slot);
    sparkmax.getPIDController().setFF(gains.kF, slot);
  }

  @Override
  public void setPIDGain(int slot, PIDGain gains) {
    this.velocityGains = gains;

    if (shooterLeft != null && shooterRight != null) {
      configPID(shooterLeft, SHOOTER_PID_SLOTS.VELOCITY_GAINS.getValue(), this.velocityGains);
      configPID(shooterLeft, SHOOTER_PID_SLOTS.VELOCITY_GAINS.getValue(), this.velocityGains);
    }
  }

  @Override
  public int[] getSlots() {
    return new int[] { 0 };
  }

  public enum SHOOTER_PID_SLOTS {
    VELOCITY_GAINS(0);

    private int value;

    SHOOTER_PID_SLOTS(int value) {
      this.value = value;
    }

    public int getValue() {
      return this.value;
    }
  }
}
