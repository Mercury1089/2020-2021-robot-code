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
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
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
  private TalonSRX right;
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
    
    driveTrain.configPIDSlots(DriveTrainSide.RIGHT, DriveTrain.DRIVE_MOTION_PROFILE_SLOT, DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);
    driveTrain.setNeutralMode(NeutralMode.Brake);
    driveTrain.resetPigeonYaw();

    podgeboi.configFactoryDefault();

    right.changeMotionControlFramePeriod(10);
    right.configAuxPIDPolarity(false);

    fillTopBuffer();
    
    trajectoryProcessor.startPeriodic(0.005);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    right.getMotionProfileStatus(statusRight);

    if (!isRunning && statusRight.btmBufferCnt >= 5) {
      right.set(ControlMode.MotionProfileArc, SetValueMotionProfile.Enable.value);

      isRunning = true;
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      DriverStation.reportError(getName() + " is interrupted", false);
    }
    DriverStation.reportError("MoveOnTrajectory END", false);

    trajectoryProcessor.stop();

    reset();
    driveTrain.stop();
    driveTrain.configVoltage(DriveTrain.NOMINAL_OUT, DriveTrain.PEAK_OUT);
    right.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 10, RobotMap.CTRE_TIMEOUT);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    boolean isFinished = statusRight.activePointValid && 
                         statusRight.isLast &&
                         isRunning;
    DriverStation.reportError("MoveOnTrajectory" + isFinished, false);

    return isFinished;
  }

  //Fills talon with trajectory states
  public void fillTopBuffer() {
    for(TrajectoryPoint point : trajectoryPoints) {
      right.pushMotionProfileTrajectory(point);
      if(point.isLastPoint)
        System.out.println("IS TRU BRO");
    }
  }

  public void setMotionProfileMode () {
    right.set(ControlMode.MotionProfileArc, SetValueMotionProfile.Enable.value);
  }

  public void reset() {
    //Reset motion profile mode and flags
    isRunning = false;
    setMotionProfileMode();
    right.getSensorCollection().setQuadraturePosition(0, RobotMap.CTRE_TIMEOUT);
    //Clear trajectory buffer
    right.clearMotionProfileTrajectories();
  }
}
