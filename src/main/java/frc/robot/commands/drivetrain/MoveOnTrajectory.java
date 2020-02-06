/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drivetrain;

import java.io.FileNotFoundException;
import java.util.List;

import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.SetValueMotionProfile;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotMap;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.DriveTrain.DriveTrainSide;
import frc.robot.util.MercPathLoader;
import frc.robot.util.MercTalonSRX;

public class MoveOnTrajectory extends CommandBase {
  private static Notifier trajectoryProcessor;
  
  private boolean isRunning;
  private DriveTrain driveTrain;
  private MercTalonSRX right;
  private MotionProfileStatus statusRight;
  private List<TrajectoryPoint> trajectoryPoints;
  private PigeonIMU podgeboi;
  private String pathName;

  public MoveOnTrajectory(String path, DriveTrain driveTrain) throws FileNotFoundException{
    addRequirements(driveTrain);
    setName("Move On Trajectory" + path);

    pathName = path;
    this.driveTrain = driveTrain;
    podgeboi = this.driveTrain.getPigeon();
    statusRight = new MotionProfileStatus();
    trajectoryPoints = MercPathLoader.loadPath(pathName);
    right = (MercTalonSRX)this.driveTrain.getRightLeader();

    trajectoryProcessor = new Notifier(() -> {
      right.get().processMotionProfileBuffer();
    });
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    if (trajectoryPoints == null)
      DriverStation.reportError("No trajectory to load", false);
    if (!driveTrain.isInMotionMagicMode())
      driveTrain.initializeMotionMagicFeedback();

    driveTrain.configPIDSlots(DriveTrainSide.RIGHT, DriveTrain.DRIVE_MOTION_PROFILE_SLOT, DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);
    driveTrain.setNeutralMode(NeutralMode.Brake);
    driveTrain.resetPigeonYaw();

    right.set(ControlMode.MotionProfileArc, SetValueMotionProfile.Enable.value);
    right.get().changeMotionControlFramePeriod(10);
    right.get().configAuxPIDPolarity(false);

    podgeboi.configFactoryDefault();

    fillTopBuffer();
    trajectoryProcessor.startPeriodic(0.005);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    isRunning = true;
    right.get().getMotionProfileStatus(statusRight);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      DriverStation.reportError(getName() + " is interrupted", false);
    }
    trajectoryProcessor.stop();

    isRunning = false;
    driveTrain.stop();
    driveTrain.configVoltage(DriveTrain.NOMINAL_OUT, DriveTrain.PEAK_OUT);
    right.get().setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 10, RobotMap.CTRE_TIMEOUT);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return statusRight.activePointValid && 
           statusRight.isLast &&
           isRunning;
  }

  public void fillTopBuffer() {
    for(TrajectoryPoint point : trajectoryPoints) {
      right.get().pushMotionProfileTrajectory(point);
    }
  }

}
