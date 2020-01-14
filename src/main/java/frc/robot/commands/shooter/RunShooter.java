/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Shooter;
import frc.robot.util.interfaces.IMercMotorController;

public class RunShooter extends CommandBase {

  protected IMercMotorController shooterLeft, shooterRight;

  private Shooter shooter;
  
  private final double VOLTAGE = 0.5;

  /**
   * Creates a new RunShooter.
   */
  public RunShooter(Shooter shooter) {
    super.addRequirements(shooter);
    shooter = shooter;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    this.shooter.configVoltage(VOLTAGE, Shooter.PEAK_OUT);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
