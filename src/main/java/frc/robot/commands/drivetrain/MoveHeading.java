/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drivetrain;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FollowerType;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.DriveTrain.DriveTrainSide;
import frc.robot.util.*;
import frc.robot.util.interfaces.IMercMotorController;
import edu.wpi.first.wpilibj2.command.Subsystem;
import java.util.Set;

public class MoveHeading implements Command {
    protected final int CLOSED_LOOP_TIME_MS = 1;
    protected int moveThresholdTicks;   // ticks
    protected double angleThresholdDeg; // degrees
    protected int onTargetMinCount; // 100 millis
    protected int checkThreshold = 50;
    protected IMercMotorController left, right;

    protected double distance, targetHeading;

    protected double dirFactor;

    protected int onTargetCount, initialCheckCount;

    private Set<Subsystem> requirements;

    /**
     * Move with heading assist from pigeon
     *
     * @param distance distance to move in inches
     * @param heading  heading to turn to for the pigeon
     */
    public MoveHeading(double distance, double heading) {
        requirements = new Requirements();
        requirements.add(Robot.driveTrain);

        left = Robot.driveTrain.getLeftLeader();
        right = Robot.driveTrain.getRightLeader();

        moveThresholdTicks = 500;
        angleThresholdDeg = 5;
        onTargetMinCount = 4;

        dirFactor = Robot.driveTrain.getDirection().dir;

        this.distance = MercMath.inchesToEncoderTicks(distance * dirFactor);
        this.targetHeading = MercMath.degreesToPigeonUnits(heading);
    }

    public Set<Subsystem> getRequirements() {
        return requirements;
    }

    // Called just before this Command runs the first time
    @Override
    public void initialize() {
        Robot.driveTrain.resetEncoders();

        if (!Robot.driveTrain.isInMotionMagicMode())
            Robot.driveTrain.initializeMotionMagicFeedback();

        onTargetCount = 0;
        initialCheckCount = 0;

        /* Motion Magic Configurations */
        right.configMotionAcceleration(1000);
        right.configMotionCruiseVelocity((int) MercMath.revsPerMinuteToTicksPerTenth(DriveTrain.MAX_RPM));

        int closedLoopTimeMs = 1;
        right.configClosedLoopPeriod(0, closedLoopTimeMs);
        right.configClosedLoopPeriod(1, closedLoopTimeMs);

        right.configAuxPIDPolarity(true);

        Robot.driveTrain.configPIDSlots(DriveTrainSide.RIGHT, DriveTrain.DRIVE_PID_SLOT, DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);

        Robot.driveTrain.resetPigeonYaw();
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    public void execute() {
        /* Configured for MotionMagic on Quad Encoders and Auxiliary PID on Pigeon */
        right.set(ControlMode.MotionMagic, distance, DemandType.AuxPID, targetHeading);
        left.follow(right, FollowerType.AuxOutput1);
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    public boolean isFinished() {
        if (initialCheckCount < checkThreshold) {
            initialCheckCount++;
            return false;
        }

        double distError = right.getClosedLoopError(), angleError = right.getClosedLoopError(DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);

        angleError = MercMath.pigeonUnitsToDegrees(angleError);

        boolean isFinished = false;

        boolean isOnTarget = (Math.abs(distError) < moveThresholdTicks &&
                Math.abs(angleError) < angleThresholdDeg);

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
        Robot.driveTrain.stop();
        Robot.driveTrain.configVoltage(DriveTrain.NOMINAL_OUT, DriveTrain.PEAK_OUT);
    }

    /**
     * @param requirements the requirements to set
     */
    public void setRequirements(Set<Subsystem> requirements) {
        this.requirements = requirements;
    }
}
