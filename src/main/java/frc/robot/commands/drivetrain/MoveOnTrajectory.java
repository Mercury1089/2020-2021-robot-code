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
import com.ctre.phoenix.motorcontrol.FollowerType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotMap;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.DriveTrain.DriveTrainSide;
import frc.robot.util.MercMotionProfile;
import frc.robot.util.MercPathLoader;
import frc.robot.util.MercTalonSRX;

public class MoveOnTrajectory extends CommandBase {
  private static Notifier trajectoryProcessor;
  
  private boolean isRunning;
  private DriveTrain driveTrain;
  private TalonSRX left, right;
  private MotionProfileStatus statusRight;
  private List<TrajectoryPoint> trajectoryPoints;
  private int timeDuration;
  private PigeonIMU podgeboi;
  private String pathName;
  private MercMotionProfile profile;

  public MoveOnTrajectory(String path, DriveTrain driveTrain) throws FileNotFoundException{
    addRequirements(driveTrain);
    setName("MoveOn " + profile.getName() + "Path");

    pathName = path;
    this.driveTrain = driveTrain;
    podgeboi = this.driveTrain.getPigeon();
    statusRight = new MotionProfileStatus();
    trajectoryPoints = MercPathLoader.loadPath(pathName);

    left = ((MercTalonSRX) this.driveTrain.getLeftLeader()).get();
    right = ((MercTalonSRX) this.driveTrain.getRightLeader()).get();
    
    trajectoryProcessor = new Notifier(() -> {
      right.processMotionProfileBuffer();
    });
  }

  public MoveOnTrajectory(MercMotionProfile profile, DriveTrain driveTrain) throws FileNotFoundException {
    this.profile = profile;
    this.driveTrain = driveTrain;

    addRequirements(driveTrain);
    setName("MoveOn " + profile.getName() + "Path");
    pathName = profile.getName();
    podgeboi = this.driveTrain.getPigeon();
    statusRight = new MotionProfileStatus();
    trajectoryPoints = profile.getPathForward();

    left = ((MercTalonSRX) this.driveTrain.getLeftLeader()).get();
    right = ((MercTalonSRX) this.driveTrain.getRightLeader()).get();
    
    trajectoryProcessor = new Notifier(() -> {
      right.processMotionProfileBuffer();
    });
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    if (trajectoryPoints == null)
      DriverStation.reportError("No trajectory to load", false);
    if (!driveTrain.isInMotionMagicMode())
      driveTrain.initializeMotionMagicFeedback();

    reset();
    fillTopBuffer();

    trajectoryProcessor.startPeriodic(0.005);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    //timeDuration = statusRight.timeDurMs / 2;
    //if(timeDuration < 1)
    //  timeDuration = 1;
    //right.changeMotionControlFramePeriod(timeDuration);
    right.getMotionProfileStatus(statusRight);
    left.follow(right, FollowerType.AuxOutput1);
    SmartDashboard.putNumber("Primary PID Error", right.getClosedLoopError(0));
    SmartDashboard.putNumber("Aux PID Error", right.getClosedLoopError(1));
    // If motion profile has not started running, and buffer is too low
    if(!isRunning && statusRight.btmBufferCnt >= 20) {
      right.set(ControlMode.MotionProfileArc, SetValueMotionProfile.Enable.value);
      isRunning = true;
      DriverStation.reportError("IsRunning", false);
    }
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
    right.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 10, RobotMap.CTRE_TIMEOUT);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return statusRight.activePointValid && 
           statusRight.isLast &&
           isRunning;
  }
  // Feeds TalonSRX with trajectory points
  public void fillTopBuffer() {
    for(TrajectoryPoint point : trajectoryPoints) {
      right.pushMotionProfileTrajectory(point);
    }
  }

  // Resets values to rerun command
  private void reset() {
    // Reset flags and motion profile modes
    isRunning = false;
    right.set(ControlMode.MotionProfileArc, SetValueMotionProfile.Disable.value);
    right.getSensorCollection().setQuadraturePosition(0, RobotMap.CTRE_TIMEOUT);
    right.configMotionProfileTrajectoryPeriod(0, RobotMap.CTRE_TIMEOUT);

    // Clear the trajectory buffer
    right.clearMotionProfileTrajectories();

    // Reconfigure driveTrain settings
    driveTrain.configPIDSlots(DriveTrainSide.RIGHT, DriveTrain.DRIVE_MOTION_PROFILE_SLOT, DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);
    driveTrain.setNeutralMode(NeutralMode.Brake);
    driveTrain.resetPigeonYaw();
    driveTrain.resetEncoders();

    // Reset pigeon
    podgeboi.configFactoryDefault();

    int halfFramePeriod = MercPathLoader.getMinTime() / 2;
    if(halfFramePeriod < 1)
      halfFramePeriod = 1;
    right.changeMotionControlFramePeriod(halfFramePeriod);
    right.configAuxPIDPolarity(false);
  }
}
