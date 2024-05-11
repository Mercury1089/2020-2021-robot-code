/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.RobotMap.CAN;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;

public class Hopper extends SubsystemBase {
  
  private VictorSPX hopperBelt, agitator;
  private final double RUN_SPEED, AGITATOR_SPEED;
  public final boolean IS_CLOCKWISE;
  /**
   * Creates a new Hopper.
   */
  public Hopper() {
    RUN_SPEED = -0.5;

    hopperBelt = new VictorSPX(CAN.HOPPER_BELT);
    hopperBelt.setNeutralMode(NeutralMode.Brake);
    AGITATOR_SPEED = 0.5;
    IS_CLOCKWISE = true;
    agitator = new VictorSPX(CAN.AGITATOR);
    agitator.setInverted(IS_CLOCKWISE);
    agitator.setNeutralMode(NeutralMode.Brake);
    setName("Hopper");

  }

  public void runAgitator() {
    agitator.set(ControlMode.PercentOutput, AGITATOR_SPEED);
  }

  public void stopAgitator() {
    agitator.set(ControlMode.PercentOutput, 0.0);
  }

  public boolean getIsClockwise() {
    return IS_CLOCKWISE;
  }

  public void setSpeed(double speed) {
    hopperBelt.set(ControlMode.PercentOutput, speed);
  }

  public void stopHopper() {
    setSpeed(0.0);
  }

  public void runHopper(){
    setSpeed(RUN_SPEED);
  }

  public double getRunSpeed() {
    return RUN_SPEED;
  }

  public void runHopperAgitator(){ 
    runAgitator();
    runHopper();
  }

  public void stopHopperAgitator(){ 
    stopAgitator();
    stopHopper();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
