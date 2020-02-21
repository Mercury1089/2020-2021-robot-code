/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drivetrain;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.LimelightCamera;
import frc.robot.subsystems.DriveTrain.DriveTrainSide;
import frc.robot.util.MercMath;

public class RotateToTarget extends DegreeRotate {

    private DriveTrain driveTrain;
    private LimelightCamera limelightCamera;

    public RotateToTarget(DriveTrain driveTrain, LimelightCamera limelightCamera) {
        super(0, driveTrain);
        setName("RotateToTarget");

        this.driveTrain = driveTrain;
        this.limelightCamera = limelightCamera;
    }

    // Called just before this Command runs the first time
    @Override
    public void initialize() {
        super.initialize();

        this.driveTrain.configPIDSlots(DriveTrainSide.RIGHT, DriveTrain.DRIVE_PID_SLOT, DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);

        targetHeading = -MercMath.degreesToPigeonUnits(this.limelightCamera.getLimelight().getTargetCenterXAngle());
        System.out.println("RotateToTarget initialized with angle " + this.limelightCamera.getLimelight().getTargetCenterXAngle());

    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    public void execute() {
        super.execute();
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    public boolean isFinished() {
        // double angleError = right.getClosedLoopError(DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);

        // angleError = MercMath.pigeonUnitsToDegrees(angleError);
        // System.out.println(angleError);

        // boolean isFinished = false;

        // boolean isOnTarget = (Math.abs(angleError) < DriveTrain.ANGLE_THRESHOLD_DEG);

        // if (isOnTarget) {
        //     onTargetCount++;
        // } else {
        //     if (onTargetCount > 0)
        //         onTargetCount = 0;
        // }

        // if (onTargetCount > onTargetMinCount) {
        //     isFinished = true;
        //     onTargetCount = 0;
        // }

        // SmartDashboard.putNumber("angleError", angleError);
        // return isFinished;
        return false;
    }

    // Called once after isFinished returns true
    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
    }
}
