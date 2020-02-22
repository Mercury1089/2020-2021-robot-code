/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auton;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.drivetrain.MoveOnTrajectory;
import frc.robot.commands.intake.RunIntake;
import frc.robot.subsystems.*;
import frc.robot.util.MercMotionProfile;
import frc.robot.util.MercPathGroup;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/latest/docs/software/commandbased/convenience-features.html
public class TargetZoneTrenchRun5Ball extends SequentialCommandGroup {
  public TargetZoneTrenchRun5Ball(DriveTrain driveTrain, Intake intake, IntakeArticulator intakeArticulator, LimelightCamera limelightCamera, Shooter shooter) throws FileNotFoundException {
      super();
      
      MercPathGroup fCenter5Ball = new MercPathGroup("FCenter5BallCenter");

      List<MoveOnTrajectory> paths = new ArrayList<MoveOnTrajectory>();

      for (MercMotionProfile p : fCenter5Ball.getProfiles()){
        paths.add(new MoveOnTrajectory(p, driveTrain));
      }

      //addCommands(new RunCommand(() -> intakeArticulator.setIntakeOut(), intakeArticulator));

      for(int i = 0; i < paths.size(); i++){
        /*
        switch (i) {
          case 1:
          case 3:
            addCommands(new ParallelCommandGroup(paths.get(i), new RunIntake(intake)));
            break;
          case 5:
            addCommands(new RunCommand(() -> intakeArticulator.setIntakeIn(), intakeArticulator));
          default:
            addCommands(paths.get(i));
        }
        */
        addCommands(paths.get(i));
      }
      //TODO: Add commands to aim and shoot
  }
}
