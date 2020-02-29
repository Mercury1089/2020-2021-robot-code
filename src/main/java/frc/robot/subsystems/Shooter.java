/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/* this is definently not a class to create a school shooter                  */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.MercSparkMax;
import frc.robot.util.PIDGain;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.util.interfaces.IMercPIDTunable;
import frc.robot.util.interfaces.IMercShuffleBoardPublisher;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.RobotMap.*;
import frc.robot.sensors.Limelight;

public class Shooter extends SubsystemBase implements IMercShuffleBoardPublisher, IMercPIDTunable {
  // private IMercMotorController flywheel;

  public static final double NOMINAL_OUT = 0.0, PEAK_OUT = 1.0;

  private IMercMotorController shooterLeft, shooterRight;

  private double currentSpeed;
  private double targetRPM;

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
      shooterLeft = new MercSparkMax(CAN.SHOOTER_LEFT);
      shooterRight = new MercSparkMax(CAN.SHOOTER_RIGHT);

      shooterLeft.configVoltage(NOMINAL_OUT, PEAK_OUT);
      shooterRight.configVoltage(NOMINAL_OUT, PEAK_OUT);

      shooterLeft.setNeutralMode(NeutralMode.Coast);
      shooterRight.setNeutralMode(NeutralMode.Coast);

      shooterLeft.setInverted(true);
      shooterRight.setInverted(false);
      shooterRight.follow(shooterLeft);
    } else if (mode == ShooterMode.NONE) {
      shooterLeft = shooterRight = null;
    }


    SmartDashboard.putNumber(getName() + "/SetRPM", 0.0);
    setRunSpeed(0.0);
    targetRPM = 0.0;
    velocityGains = new PIDGain(1e-5, 2e-7, 1e-5, 0);
    
    this.limelight = limelight;

    setPIDGain(SHOOTER_PID_SLOTS.VELOCITY_GAINS.getValue(), velocityGains);
  }

  @Override
  public void periodic() {

  }

  public void setSpeed(double speed) {
    this.currentSpeed = speed;

    if (shooterLeft != null && shooterRight != null) {
      shooterLeft.setNeutralMode(NeutralMode.Coast);
      shooterRight.setNeutralMode(NeutralMode.Coast);
  
      shooterLeft.setSpeed(speed);        
    }
  }

  public void increaseSpeed() {
    currentSpeed += 0.05;
    this.setSpeed(currentSpeed);
  }

  public void decreaseSpeed() {
    currentSpeed -= 0.05;
    this.setSpeed(currentSpeed);
  }

  public double getRPM() {
    return shooterLeft != null ? shooterLeft.getEncVelocity() : 0.0;
  }

  public double getTargetRPM() {
    double distance = limelight.calcDistFromVert();
    if(distance > 100.0 && distance < 250.0)
      updateTargetRPM(distance);
    else
      setTargetRPM(4000);
    return targetRPM;
  }

  public double getTargetRPMFromHypothetical() {
    double distance = getHyotheticalDistance();
    return 7.663505E-5*Math.pow(distance, 4) - 0.056264*Math.pow(distance, 3) + 15.436557*Math.pow(distance, 2) - 1867.408104*distance + 87769.565386;
  }

  public void setTargetRPM(double rpm) {
    targetRPM = rpm;
  }

  public void updateTargetRPM(double distance) {
    targetRPM = 7.663505E-5*Math.pow(distance, 4) - 0.056264*Math.pow(distance, 3) + 15.436557*Math.pow(distance, 2) - 1867.408104*distance + 87769.565386;
  }

  public boolean atTargetRpm() {
    return Math.abs(getRPM() - getTargetRPM()) <= 0.01 * getTargetRPM();
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

  public void setVelocity(double rpm) {
    if (shooterLeft != null && shooterRight != null)
    {
      // Ensures shooter is in coast mode
      shooterLeft.setNeutralMode(NeutralMode.Coast);
      shooterRight.setNeutralMode(NeutralMode.Coast);
      // Sets RPM
      shooterLeft.setVelocity(rpm);
      // shooterRight.setVelocity(rpm);
    }
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
    SmartDashboard.putString(getName() + "/ShooterMode",
        getMode() == ShooterMode.ONE_WHEEL ? "ONE WHEEL" : "NONE");
    SmartDashboard.putNumber(getName() + "/RPM", getRPM());
    
    SmartDashboard.putNumber(getName() + "/PIDGains/P", velocityGains.kP);
    SmartDashboard.putNumber(getName() + "/PIDGains/I", velocityGains.kI);
    SmartDashboard.putNumber(getName() + "/PIDGains/D", velocityGains.kD);
    SmartDashboard.putNumber(getName() + "/PIDGains/F", velocityGains.kF);

    SmartDashboard.putBoolean(getName() + "/AtTargetRPM", atTargetRpm());
    SmartDashboard.putNumber("Hypothetical Distance", getHyotheticalDistance());
    SmartDashboard.putNumber("Hypothetical RPM", getTargetRPMFromHypothetical());
  }

  @Override
  public PIDGain getPIDGain(int slot) {
    return this.velocityGains;
  }

  @Override
  public void setPIDGain(int slot, PIDGain gains) {
    this.velocityGains = gains;

    if (shooterLeft != null && shooterRight != null) {
      shooterLeft.configPID(SHOOTER_PID_SLOTS.VELOCITY_GAINS.getValue(), this.velocityGains);
      shooterRight.configPID(SHOOTER_PID_SLOTS.VELOCITY_GAINS.getValue(), this.velocityGains);
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
