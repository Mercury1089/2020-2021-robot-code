/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;
import com.ctre.phoenix.motorcontrol.NeutralMode;
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
  private final IMercMotorController intakeRoller, agitator;
  public final double INTAKE_SPEED, AGITATOR_SPEED;
  public final boolean IS_CLOCKWISE;
  /**
   * Creates a new Intake.
   */
  public Intake() {
    super();

    double agitatorSpeed = 0.5;
    
    INTAKE_SPEED = 1.0;
    IS_CLOCKWISE = true;
    AGITATOR_SPEED = IS_CLOCKWISE ? -agitatorSpeed : agitatorSpeed;
    setName("Intake");
    intakeRoller = new MercVictorSPX(CAN.INTAKE_ROLLER);
    intakeRoller.setInverted(true);
    agitator = new MercVictorSPX(CAN.AGITATOR);
    agitator.setNeutralMode(NeutralMode.Brake);
  }

  public void setRollerSpeed(double speed) {
    intakeRoller.setSpeed(speed);
  }

  public void runIntakeRoller(double speedProportion) {
    intakeRoller.setSpeed(INTAKE_SPEED * speedProportion);
  }

  public void runIntakeRoller() {
    intakeRoller.setSpeed(INTAKE_SPEED);
  }

  public void stopIntakeRoller() {
    intakeRoller.setSpeed(0.0);
  }

  public void runAgitator() {
    agitator.setSpeed(AGITATOR_SPEED);
  }

  public void stopAgitator() {
    agitator.setSpeed(0.0);
  }

  public boolean getIsClockwise() {
    return IS_CLOCKWISE;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  @Override
  public void publishValues() {
    
  }
}
