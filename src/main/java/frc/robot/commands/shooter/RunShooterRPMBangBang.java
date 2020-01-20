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

public class RunShooterRPMBangBang extends CommandBase {

  protected IMercMotorController shooterLeft, shooterRight;
  private Shooter shooter;
  private double rMax, rMin;
  private final int TOLERANCE = 25;
  //  If we want tolerance to be based on a percent of rpm
  //private final double TOLERANCE = 1.01;

  public RunShooterRPMBangBang(Shooter shooter) {
    super.addRequirements(shooter);
    this.shooter = shooter;
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    rMax = Math.abs(shooter.getRunRPM()) + TOLERANCE;
    rMin = Math.abs(shooter.getRunRPM()) - TOLERANCE;

    if(shooter.getRPM() > rMax)
      shooter.setSpeed(0.0);
    else if (shooter.getRPM() < rMin)
      shooter.setSpeed(1.0);

    /*  If we want the tolerance to be based on a percent of desired rpm
    rMax = Math.abs(shooter.getRunRPM()) * TOLERANCE;
    rMin = Math.abs(shooter.getRunRPM()) / TOLERANCE;
    
    if(shooter.getRPM() > rMax)
      shooter.setSpeed(0.0);
    else if (shooter.getRPM() < rMin)
      shooter.setSpeed(1.0);
    */
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    this.shooter.setSpeed(0.0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
