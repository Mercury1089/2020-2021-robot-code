/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotMap.CAN;
import frc.robot.util.MercVictorSPX;
import frc.robot.util.interfaces.IMercMotorController;

public class Jiggler extends SubsystemBase {
  //IMercMotorController jiggler;
  public Jiggler() {
    //setName("Jiggler");
    //jiggler = new MercVictorSPX(CAN.JIGGLER);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
}
