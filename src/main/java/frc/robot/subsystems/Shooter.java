/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/* this is definently not a class to create a school shooter                  */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import java.lang.ProcessBuilder.Redirect.Type;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.servohub.ServoHub.ResetMode;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.util.PIDGain;

import frc.robot.RobotMap.*;

public class Shooter extends SubsystemBase {
  // private IMercMotorController flywheel;

  public static final double NOMINAL_OUT = 0.0, PEAK_OUT = 1.0;
  public static final double MAX_RPM = 5000.0, STEADY_RPM = 4000.0, LOW_RPM = 1000.0, NULL_RPM = -1.0;
  public static final double MIN_DISTANCE = 6.7, MAX_DISTANCE = 17.0;
  //public static final double MIN_DISTANCE = 2.0, MAX_DISTANCE = 20.0;
  public final int BREAKBEAM_DIO = 2;
  private final double TARGET_VELOCITY_THRESHOLD = 50.0; // within a +- 50 rpm range to shoot
  private final double MAX_VOLTAGE = 10.5;
  private SparkMax shooterLeft, shooterRight;

  private double targetVelocity;

  private ShooterMode mode;

  private PIDGain velocityGains;

  private SparkMaxConfig shooterLeftConfig = new SparkMaxConfig();
  private SparkMaxConfig shooterRightConfig = new SparkMaxConfig();

  private SparkClosedLoopController shooterController;

  public enum ShooterMode {
    ONE_WHEEL, NONE
  }

  public Shooter(ShooterMode mode) {
    setName("Shooter");
    this.mode = mode;
    
    velocityGains = new PIDGain(0.00024, 0.00000001, 0.01, 0.0002025);    

    if (mode == ShooterMode.ONE_WHEEL) {
      shooterLeft = new SparkMax(CAN.SHOOTER_LEFT, MotorType.kBrushless);
      shooterRight = new SparkMax(CAN.SHOOTER_RIGHT, MotorType.kBrushless);

      shooterLeftConfig.voltageCompensation(MAX_VOLTAGE)
                    .inverted(true)
                    .idleMode(IdleMode.kCoast)
                    .closedLoop.outputRange(NOMINAL_OUT, PEAK_OUT)
                    .pidf(velocityGains.kP,velocityGains.kI,velocityGains.kD, velocityGains.kF, ClosedLoopSlot.kSlot0);

      shooterRightConfig.voltageCompensation(MAX_VOLTAGE)
                    .follow(shooterLeft, true)
                    .idleMode(IdleMode.kCoast)
                    .closedLoop.outputRange(NOMINAL_OUT, PEAK_OUT)
                    .pidf(velocityGains.kP,velocityGains.kI,velocityGains.kD, velocityGains.kF, ClosedLoopSlot.kSlot0);

      shooterLeft.configure(shooterLeftConfig, com.revrobotics.spark.SparkBase.ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
      shooterRight.configure(shooterRightConfig, com.revrobotics.spark.SparkBase.ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

      shooterController = shooterLeft.getClosedLoopController();
       // Follow inverted
    } else if (mode == ShooterMode.NONE) {
      shooterLeft = shooterRight = null;
    }


    SmartDashboard.putNumber(getName() + "/SetRPM", 0.0);

    stopShooter();
    targetVelocity = 0.0;
    // velocityGains = new PIDGain(1e-5, 2e-7, 1e-5, 0);
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
      shooterController.setReference(setVelocity, ControlType.kVelocity);
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
    return STEADY_RPM;
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

  // private void configPID(SparkMax sparkmax, PIDGain gains) {
  //   shooterLeftConfig.closedLoop
  //     .pid(gains.kP,gains.kI,gains.kF,ClosedLoopSlot.kSlot0);

  //   shooterLeftConfig.closedLoop
  //     .pid(gains.kP,gains.kI,gains.kF,ClosedLoopSlot.kSlot0);

  //   shooterLeft.configure(shooterLeftConfig, com.revrobotics.spark.SparkBase.ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  //   shooterRight.configure(shooterRightConfig, com.revrobotics.spark.SparkBase.ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
  // }

  // public void setPIDGain(int slot, PIDGain gains) {
  //   this.velocityGains = gains;

  //   if (shooterLeft != null && shooterRight != null) {
  //     configPID(shooterLeft, this.velocityGains);
  //     configPID(shooterLeft, this.velocityGains);
  //   }
  // }

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
