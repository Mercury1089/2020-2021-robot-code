/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.RobotMap.CAN;

public class Intake extends SubsystemBase {
  private final IMercMotorController intake;
  private IntakePosition intakePosition;

  public enum IntakePosition {
    OUT,
    IN
  }

  /**
   * Creates a new Intake.
   */
  public Intake() {
    intake = new MercTalonSRX(CAN.INTAKE);
    intakePosition = IntakePosition.IN;
  }

  public void setSpeed(double speed) {
    this.intake.setSpeed(speed);
  }

  public void setIntakeIn() {
    this.intakePosition = IntakePosition.IN;
  }

  public void setIntakeOut() {
    this.intakePosition = IntakePosition.OUT;
  }

  // public void toggleIntakePosition() {
    
  // }

  public IntakePosition getIntakePosition() {
    return this.intakePosition;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
