/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.RobotMap;
import frc.robot.RobotMap.CAN;
import frc.robot.util.MercMath;

public class Elevator extends SubsystemBase {

  private Relay elevatorLock;

  private static final int MAX_ELEV_RPM = 18000;
  public static final double NORMAL_P_VAL = 0.21;
  public static final int PRIMARY_PID_LOOP = 0;

  private TalonSRX elevator;
  private double runSpeed;

  /**
   * Creates a new Elevator.
   */
  public Elevator() {
    super();
    setName("Elevator");

    elevatorLock = new Relay(RobotMap.RELAY.ELEVATOR_LOCK, Relay.Direction.kForward);
    elevatorLock.set(Relay.Value.kOff);

    elevator = new TalonSRX(CAN.ELEVATOR);

    runSpeed = 0.5;
    elevator.setNeutralMode(NeutralMode.Brake);

    elevator.configMotionAcceleration((int)(MercMath.revsPerMinuteToTicksPerTenth(18000 * 2)));
    elevator.configMotionCruiseVelocity((int) MercMath.revsPerMinuteToTicksPerTenth(MAX_ELEV_RPM));

    elevator.setSensorPhase(true);
    elevator.setInverted(true);
    elevator.configNominalOutputForward(0.125, RobotMap.CTRE_TIMEOUT);
    elevator.configNominalOutputReverse(-0.125, RobotMap.CTRE_TIMEOUT);
    elevator.configPeakOutputForward(1.0, RobotMap.CTRE_TIMEOUT);
    elevator.configPeakOutputReverse(-1.0, RobotMap.CTRE_TIMEOUT);
    elevator.configClosedLoopPeriod(0, 1);
    //elevator.configAllowableClosedLoopError(0, 5);
    elevator.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, RobotMap.PID.PRIMARY_PID_LOOP, RobotMap.CTRE_TIMEOUT);
    elevator.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 0, 0);
    elevator.configForwardSoftLimitThreshold((int) ElevatorPosition.TOP.encPos, RobotMap.CTRE_TIMEOUT);
    elevator.configForwardSoftLimitEnable(true, RobotMap.CTRE_TIMEOUT);
    resetElevatorPos();

    elevator.config_kP(RobotMap.PID.PRIMARY_PID_LOOP, NORMAL_P_VAL, RobotMap.CTRE_TIMEOUT);
    elevator.config_kI(RobotMap.PID.PRIMARY_PID_LOOP, 0.0, RobotMap.CTRE_TIMEOUT);
    elevator.config_kD(RobotMap.PID.PRIMARY_PID_LOOP, 0.0, RobotMap.CTRE_TIMEOUT);
    elevator.config_kF(RobotMap.PID.PRIMARY_PID_LOOP, MercMath.calculateFeedForward(MAX_ELEV_RPM), RobotMap.CTRE_TIMEOUT);
  }

  public void resetElevatorPos() {
    // reset encoder to 0 on talon
    elevator.setSelectedSensorPosition(0, RobotMap.PID.PRIMARY_PID_LOOP, RobotMap.CTRE_TIMEOUT);
  }
  public void setLockEngaged(boolean state){
    if (state) {
      elevatorLock.set(Relay.Value.kOn);
      //elevator.setForwardSoftLimit((int) ElevatorPosition.BOTTOM.encPos);
    } else {
      elevatorLock.set(Relay.Value.kOff);
      //elevator.setForwardSoftLimit((int) ElevatorPosition.TOP.encPos);
    }
  }

  public void setPosition(ElevatorPosition pos) {
    elevator.set(ControlMode.Position, pos.encPos);
  }

  public Relay.Value getLockState(){
    return elevatorLock.get();
  }

  public double getRunSpeed() {
    return runSpeed;
  }

  public void setSpeed(double speed){
    elevator.set(ControlMode.PercentOutput,speed);
  }

  /**
   * Get current height of claw on elevator.
   *
   * @return height of claw as read by the encoder, in ticks
   */
  public double getCurrentHeight() {
    return elevator.getSelectedSensorPosition(0);
  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber(getName() + "/Height(ticks)", getCurrentHeight());
  }
  
  public enum ElevatorPosition{
    TOP(58000, false),       // Maximum height
    READY(43000, false),
    BOTTOM(-500, false),     // Negative value ensures we always move down until limit switch enabled
    HOOK(50000, false),      // Ready hook position
    HANG(-20000, true);    // Hang position - relative to current position.

    public final double encPos;
    public final boolean isRelative;

        /**
         * Creates an elevator position, storing the encoder ticks
         * representing the height that the elevator should be at.
         *
         * @param ep encoder position, in ticks
         */
        ElevatorPosition(double encPos, boolean isRelative) {
            this.encPos = encPos;
            this.isRelative = isRelative;
        }
  }
}
