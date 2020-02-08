/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.MercVictorSPX;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.util.interfaces.IMercPIDTunable;
import frc.robot.util.interfaces.IMercShuffleBoardPublisher;
import frc.robot.util.interfaces.IMercMotorController.LimitSwitchDirection;
import frc.robot.RobotMap.CAN;
import frc.robot.commands.intake.RunManualIntake;

public class Intake extends SubsystemBase implements IMercShuffleBoardPublisher {
  private final IMercMotorController intakeRoller;
  private final IMercMotorController intakeArticulator;
  private IntakePosition intakePosition;

  public enum IntakePosition {
    OUT,
    IN
  }

  /**
   * Creates a new Intake.
   */
  public Intake() {
    super();
    setName("Intake");
    intakeRoller = new MercVictorSPX(CAN.INTAKE_ROLLER);
    intakeArticulator = new MercTalonSRX(CAN.INTAKE_ARTICULATOR);
    intakeArticulator.setInverted(true);
    intakePosition = IntakePosition.IN;
  }

  public void setSpeed(double speed) {
    this.intakeRoller.setSpeed(speed);
  }

  public void setIntakeIn() {
    this.intakePosition = IntakePosition.IN;
  }

  public void setIntakeOut() {
    this.intakePosition = IntakePosition.OUT;
  }

  public IntakePosition getIntakePosition() {
    return this.intakePosition;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  @Override
  public void publishValues() {
    SmartDashboard.putBoolean(getName() + "/FwdLimit", intakeArticulator.isLimitSwitchClosed(LimitSwitchDirection.FORWARD));
    SmartDashboard.putBoolean(getName() + "/RevLimit", intakeArticulator.isLimitSwitchClosed(LimitSwitchDirection.REVERSE));
    SmartDashboard.putNumber(getName() + "/ArticulateSpeed", intakeArticulator.getSpeed());
  }
}
