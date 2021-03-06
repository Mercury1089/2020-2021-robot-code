/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.shooter;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.sensors.Limelight;
import frc.robot.sensors.Limelight.LimelightLEDState;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.DriveTrain.ShootingStyle;
import frc.robot.util.interfaces.IMercMotorController;

public class RunShooterRPMPID extends CommandBase {

  protected IMercMotorController shooterLeft, shooterRight;

  private Shooter shooter;
  private Limelight limelight;

  private ShootingStyle shootingStyle;
  private boolean manualShooting;
  private boolean lowerPortShooting;

  /**
   * Creates a new RunShooter.
   */
  public RunShooterRPMPID(Shooter shooter, Limelight limelight, ShootingStyle shootingStyle) {
    addRequirements(shooter);
    setName("RunShooterRPMPID");
    this.shooter = shooter;
    this.limelight = limelight;
    this.shootingStyle = shootingStyle;
    manualShooting = shootingStyle == ShootingStyle.MANUAL;
    lowerPortShooting = shootingStyle == ShootingStyle.LOWER_PORT;
  }
  
  public RunShooterRPMPID(Shooter shooter, Limelight limelight) {
    this(shooter, limelight, ShootingStyle.AUTOMATIC);
  }
  
  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    limelight.setLEDState(LimelightLEDState.ON);
  }
  
  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    shooter.setShootingStyle(shootingStyle);
    if(!limelight.getTargetAcquired())
      shootingStyle = ShootingStyle.MANUAL;
    else if(!manualShooting)
      shootingStyle = ShootingStyle.AUTOMATIC;
    if(lowerPortShooting)
        shootingStyle = ShootingStyle.LOWER_PORT;
    shooter.setVelocity(Math.abs(shooter.getTargetRPM()));
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
