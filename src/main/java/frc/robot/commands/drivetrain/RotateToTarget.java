/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drivetrain;

import java.util.Set;

import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.Robot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.DriveTrain.DriveTrainSide;
import frc.robot.util.MercMath;
import frc.robot.util.Requirements;

public class RotateToTarget extends DegreeRotate {

    private DriveTrain driveTrain;

    public RotateToTarget(DriveTrain driveTrain) {
        super(0, driveTrain);

        this.driveTrain = driveTrain;

        angleThresholdDeg = 1.2;
    }

    // Called just before this Command runs the first time
    @Override
    public void initialize() {
        super.initialize();

        this.driveTrain.configPIDSlots(DriveTrainSide.RIGHT, DriveTrain.DRIVE_PID_SLOT, DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);

        targetHeading = -MercMath.degreesToPigeonUnits(this.driveTrain.getLimelight().getTargetCenterXAngle());
        System.out.println("RotateToTarget initialized with angle " + this.driveTrain.getLimelight().getTargetCenterXAngle());

    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    public void execute() {
        super.execute();
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    public boolean isFinished() {
        double angleError = right.getClosedLoopError(DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);

        angleError = MercMath.pigeonUnitsToDegrees(angleError);
        System.out.println(angleError);

        boolean isFinished = false;

        boolean isOnTarget = (Math.abs(angleError) < angleThresholdDeg);

        if (isOnTarget) {
            onTargetCount++;
        } else {
            if (onTargetCount > 0)
                onTargetCount = 0;
        }

        if (onTargetCount > onTargetMinCount) {
            isFinished = true;
            onTargetCount = 0;
        }

        return isFinished;
    }

    // Called once after isFinished returns true
    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
    }
}
