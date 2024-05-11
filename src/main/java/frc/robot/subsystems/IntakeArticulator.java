/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotMap;
import frc.robot.RobotMap.CAN;
import frc.robot.util.interfaces.IMercShuffleBoardPublisher;

public class IntakeArticulator extends SubsystemBase implements IMercShuffleBoardPublisher{

  public enum IntakePosition {
    OUT(0.75),
    IN(-1.0),
    DISABLED(0.0);

    public final double speed;
    IntakePosition(double speed) {
      this.speed = speed;
    }
  }
  private IntakePosition intakePosition;
  private final TalonSRX intakeArticulator;

  /**
   * Creates a new IntakeArticulator.
   */
  public IntakeArticulator() {
    super();
    intakeArticulator = new TalonSRX(CAN.INTAKE_ARTICULATOR);
    intakeArticulator.configFactoryDefault();
    intakeArticulator.setNeutralMode(NeutralMode.Brake);
    intakeArticulator.setInverted(true);
    // No sensor feedback is required, so Status 2 frequency can be extra low.
    intakeArticulator.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, RobotMap.CAN_STATUS_FREQ.XTRA_LOW);
    intakePosition = IntakePosition.IN;
  }

  public void setIntakePosition(IntakePosition intakePosition) {
    this.intakePosition = intakePosition;
    intakeArticulator.set(ControlMode.PercentOutput, this.intakePosition.speed);
  }

  public IntakePosition getIntakePosition() {
    return this.intakePosition;
  }

  public boolean getIntakeOut() {
    return this.intakePosition == IntakePosition.OUT;
  }

  @Override
  public void publishValues() {
    // SmartDashboard.putBoolean(getName() + "/FwdLimit", intakeArticulator.getSensorCollection().isFwdLimitSwitchClosed());
    // SmartDashboard.putBoolean(getName() + "/RevLimit", intakeArticulator.getSensorCollection().isRevLimitSwitchClosed());
  }

}
