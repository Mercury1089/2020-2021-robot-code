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
import frc.robot.RobotMap.CAN;
import frc.robot.sensors.BallCounter;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.MercVictorSPX;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.util.interfaces.IMercShuffleBoardPublisher;

public class Hopper extends SubsystemBase implements IMercShuffleBoardPublisher {
  
  private IMercMotorController hopperBelt;
  private double runSpeed;

  private BallCounter ballCounter;
  /**
   * Creates a new Hopper.
   */
  public Hopper() {
    hopperBelt = new MercVictorSPX(CAN.HOPPER_BELT);
    hopperBelt.setNeutralMode(NeutralMode.Brake);
    runSpeed = -0.5;
    setName("Hopper");

    ballCounter = new BallCounter();
  }

  public void setSpeed(double speed) {
    hopperBelt.setSpeed(speed);
  }

  public double getRunSpeed() {
    return runSpeed;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  @Override
  public void publishValues() {
    SmartDashboard.putNumber(getName() + "/BallCount", ballCounter.getCount());
    SmartDashboard.putNumber(getName() + "/Sig1", ballCounter.pixyCam.getBoxes(1).size());
    SmartDashboard.putNumber(getName() + "/Sig2", ballCounter.pixyCam.getBoxes(2).size());
    SmartDashboard.putNumber(getName() + "/Sig3", ballCounter.pixyCam.getBoxes(3).size());
    SmartDashboard.putNumber(getName() + "/Sig4", ballCounter.pixyCam.getBoxes(4).size());
    SmartDashboard.putNumber(getName() + "/Sig5", ballCounter.pixyCam.getBoxes(5).size());
  }
}
