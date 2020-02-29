/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drivetrain;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.sensors.Limelight;
import frc.robot.sensors.Limelight.LimelightLEDState;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.DriveTrain.DriveTrainSide;
import frc.robot.util.MercMath;

public class RotateToTarget extends DegreeRotate {

    private DriveTrain driveTrain;
    private Limelight limelight;
    boolean isOnTarget;

    public RotateToTarget(DriveTrain driveTrain) {
        super(0, driveTrain);
        setName("RotateToTarget");

        this.isOnTarget = false;

        this.driveTrain = driveTrain;
        this.limelight = driveTrain.getLimelight();
    }

    // Called just before this Command runs the first time
    @Override
    public void initialize() {
        super.initialize();
        limelight.setLEDState(LimelightLEDState.ON);
        this.driveTrain.configPIDSlots(DriveTrainSide.RIGHT, DriveTrain.DRIVE_PID_SLOT, DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);

        targetHeading = -MercMath.degreesToPigeonUnits(limelight.getTargetCenterXAngle());
        System.out.println("RotateToTarget initialized with angle " + limelight.getTargetCenterXAngle());

    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    public void execute() {
        double angleError = right.getClosedLoopError(DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);
        angleError = MercMath.pigeonUnitsToDegrees(angleError);

        isOnTarget = (Math.abs(angleError) < DriveTrain.ANGLE_THRESHOLD_DEG);
        if(isOnTarget) {
            double checkTarget = limelight.getTargetCenterXAngle();
            if(Math.abs(checkTarget) > DriveTrain.ANGLE_THRESHOLD_DEG) {
                    targetHeading = -MercMath.degreesToPigeonUnits(checkTarget);
                    driveTrain.resetPigeonYaw();
                    onTargetCount = 0;
            } 
        }
        super.execute();
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    public boolean isFinished() {
        boolean isFinished = false;

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
