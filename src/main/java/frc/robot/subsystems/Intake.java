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
import frc.robot.Robot;
import frc.robot.RobotMap.CAN;
import frc.robot.RobotMap.GAMEPAD_AXIS;
import frc.robot.commands.intake.RunManualIntake;

public class Intake extends SubsystemBase implements IMercShuffleBoardPublisher {
  private final IMercMotorController intakeRoller;

  /**
   * Creates a new Intake.
   */
  public Intake() {
    super();
    setName("Intake");
    intakeRoller = new MercVictorSPX(CAN.INTAKE_ROLLER);
  }

  public void setRollerSpeed(double speed) {
    this.intakeRoller.setSpeed(speed);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  @Override
  public void publishValues() {
    
  }
}
