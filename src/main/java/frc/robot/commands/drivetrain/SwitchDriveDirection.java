/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drivetrain;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.util.DriveAssist.DriveDirection;
import frc.robot.Robot;

public class SwitchDriveDirection extends CommandGroup {

    /**
     * Add your docs here.
     */
    public SwitchDriveDirection(DriveDirection driveDir) {

        //addParallel(new SwitchDrive(driveDir));
        Robot.driveTrain.setDirection(driveDir);

        setName("SwitchDriveDirection CommandGroup");
    }
}
