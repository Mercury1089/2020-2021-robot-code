/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.RobotMap;
import frc.robot.RobotMap.CAN;

import frc.robot.util.*;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.util.interfaces.IMercShuffleBoardPublisher;
import frc.robot.util.interfaces.IMercMotorController.LimitSwitchDirection;
import frc.robot.util.MercMotorController.*;

public class Elevator extends SubsystemBase implements IMercShuffleBoardPublisher {

  private Relay elevatorLock;

  private static final int MAX_ELEV_RPM = 18000;
  public static final double NORMAL_P_VAL = 0.21;
  public static final int PRIMARY_PID_LOOP = 0;

  private IMercMotorController elevator;
  private double runSpeed;

  public static class Positions {
    public final int MAX_HEIGHT = 60000;
    public static final int MIN_HEIGHT = 0;
  }
  
  /**
   * Creates a new Elevator.
   */
  public Elevator() {
    super();
    setName("Elevator");

    elevatorLock = new Relay(RobotMap.RELAY.ELEVATOR_LOCK, Relay.Direction.kForward);
    elevatorLock.set(Relay.Value.kOff);

    elevator = new MercTalonSRX(CAN.ELEVATOR);

    runSpeed = 0.5;
    elevator.setNeutralMode(NeutralMode.Brake);

    elevator.configMotionAcceleration((int)(MercMath.revsPerMinuteToTicksPerTenth(18000 * 2)));
    elevator.configMotionCruiseVelocity((int) MercMath.revsPerMinuteToTicksPerTenth(MAX_ELEV_RPM));

    elevator.setSensorPhase(true);
    elevator.setInverted(true);
    elevator.configVoltage(0.125, 1.0);
    elevator.configClosedLoopPeriod(0, 1);
    //elevator.configAllowableClosedLoopError(0, 5);
    elevator.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, RobotMap.PID.PRIMARY_PID_LOOP);
    elevator.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 0, 0);
    elevator.setForwardSoftLimit((int) ElevatorPosition.MAX_HEIGHT.encPos);
    elevator.enableForwardSoftLimit();
    elevator.resetEncoder();

    elevator.configPID(Elevator.PRIMARY_PID_LOOP, new PIDGain(NORMAL_P_VAL, 0.0, 0.0, MercMath.calculateFeedForward(MAX_ELEV_RPM)));
  }

  public void setLockEngaged(boolean state){
    if (state) {
      elevatorLock.set(Relay.Value.kOn);
    } else {
      elevatorLock.set(Relay.Value.kOff);
    }
  }

  public Relay.Value getLockState(){
    return elevatorLock.get();
  }

  public void setRaiseOrLowerSpeed(double speed) {
    elevator.setSpeed(speed);
  }

  public double getRunSpeed() {
    return runSpeed;
  }

  public double getEncTicks() {
    return elevator.getEncTicks();
  }

  public void setSpeed(double speed){
    elevator.setSpeed(speed);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  /**
   * @return the motor controller for the elevator
   */
  public IMercMotorController getElevatorLeader() {
    return elevator;
  }

  /**
   * Get current height of claw on elevator.
   *
   * @return height of claw as read by the encoder, in ticks
   */
  public double getCurrentHeight() {
    return elevator.getEncTicks();
  }




  @Override
  public void publishValues() {
    SmartDashboard.putNumber(getName() + "/Height(ticks)", getEncTicks());
    SmartDashboard.putBoolean(getName() + "/FwdLimit", elevator.isLimitSwitchClosed(LimitSwitchDirection.FORWARD));
    SmartDashboard.putBoolean(getName() + "/RevLimit", elevator.isLimitSwitchClosed(LimitSwitchDirection.REVERSE));
  }

  public enum ElevatorPosition{
    MAX_HEIGHT(100000),     //Test enc values
    BOTTOM(1000),         //1nd and lowest position
    CONTROL_PANEL(20000),    //2rd lowest position
    CLIMB(50000),           //Highest position
    HANGING(7000);          //3st position

    public final double encPos;

        /**
         * Creates an elevator position, storing the encoder ticks
         * representing the height that the elevator should be at,
         * as well as the P value to use to reach that level.
         *
         * @param ep encoder position, in ticks
         */
        ElevatorPosition(double ep) {
            encPos = ep;
        }
  }
}
