/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auton;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Robot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.commands.drivetrain.DegreeRotate;
import frc.robot.commands.drivetrain.MoveOnPath;
import frc.robot.commands.drivetrain.MoveOnPath.MPDirection;
import frc.robot.util.DriveAssist.DriveDirection;

import java.io.FileNotFoundException;

public class AutonMove extends SequentialCommandGroup {
    /**
     * Add your docs here.
     */

    private DriveTrain driveTrain;
    private MoveOnPath mop;

     

    public AutonMove(String pathname, DriveTrain driveTrain) {
        this(pathname, driveTrain.getDirection(), driveTrain);
    }

    public AutonMove(String pathname, DriveDirection driveDirection, DriveTrain driveTrain) {
        this.driveTrain = driveTrain;
        try {
            mop = new MoveOnPath(pathname, driveTrain);
        } catch (FileNotFoundException fnfe) {
            System.out.println("Not a path!");
            fnfe.printStackTrace();
        }
        if (this.driveTrain.getDirection() != driveDirection) {
            this.driveTrain.switchDirection();
        }
        /*
        if (mop.getFilename().indexOf("Station") > 0) {
            try {
                //addSequential(new MoveOnPath(mop.getFilename(), MPDirection.BACKWARD));
            } catch (FileNotFoundException fnfe) {
                System.out.println("Not a file!");
                fnfe.printStackTrace();
            }
        }
        */
    }
}
