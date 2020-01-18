/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotMap.CAN;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.interfaces.IMercMotorController;

public class Elevator extends SubsystemBase {
  
  public enum ElevatorPosition{
    IN_ROBOT,
    CONTROL_PANEL,
    CLIMB,
    HANGING
  }

  private IMercMotorController elevator, spinner;
  private double runSpeed;

  /**
   * Creates a new Elevator.
   */
  public Elevator() {
    elevator = new MercTalonSRX(CAN.ELEVATOR);
    spinner = new MercTalonSRX(CAN.SPINNER);
    runSpeed = 0.5;
  }

  public void setRaiseOrLowerSpeed(double speed) {
    elevator.setSpeed(speed);
  }

  public void setSpinningSpeed(double speed) {
    spinner.setSpeed(speed);
  }

  public double getRunSpeed() {
    return runSpeed;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
