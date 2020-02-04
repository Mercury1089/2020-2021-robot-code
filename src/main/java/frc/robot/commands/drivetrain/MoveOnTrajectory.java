/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drivetrain;

import java.util.List;

import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.SetValueMotionProfile;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotMap;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.DriveTrain.DriveTrainSide;
import frc.robot.util.MercPathLoader;
import frc.robot.util.MercTalonSRX;
import frc.robot.util.interfaces.IMercMotorController;

public class MoveOnTrajectory extends CommandBase {
  private static Notifier trajectoryProcessor;
  
  private boolean isRunning;
  private DriveTrain driveTrain;
  private MercTalonSRX right;
  private MotionProfileStatus statusRight;
  private List<TrajectoryPoint> trajectoryPoints;
  private PigeonIMU podgeboi;
  private String pathName;

  public MoveOnTrajectory(String path, DriveTrain driveTrain) {
    addRequirements(driveTrain);
    this.driveTrain = driveTrain;
    setName("Move On Trajectory" + path);
    pathName = path;
    trajectoryPoints = MercPathLoader.loadPath(pathName);
    right = (MercTalonSRX)this.driveTrain.getRightLeader();
    this.statusRight = new MotionProfileStatus();
    podgeboi = this.driveTrain.getPigeon();

    /*
    trajectoryProcessor = new Notifier(() -> {
      left.processMotionProfileBuffer();
    });
    */
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    if (trajectoryPoints == null)
      DriverStation.reportError("No trajectory to load", false);
    if (!this.driveTrain.isInMotionMagicMode())
      this.driveTrain.initializeMotionMagicFeedback();

    this.driveTrain.configPIDSlots(DriveTrainSide.RIGHT, DriveTrain.DRIVE_MOTION_PROFILE_SLOT, DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);
    this.driveTrain.setNeutralMode(NeutralMode.Brake);
    this.driveTrain.resetPigeonYaw();

    this.podgeboi.configFactoryDefault();

    this.right.get().configAuxPIDPolarity(false);

    this.fillTopBuffer();

    right.set(ControlMode.MotionProfileArc, SetValueMotionProfile.Enable.value);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    right.get().getMotionProfileStatus(statusRight);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    if (interrupted) {
      DriverStation.reportError(getName() + " is interrupted", false);
    }
    this.driveTrain.stop();
    this.driveTrain.configVoltage(DriveTrain.NOMINAL_OUT, DriveTrain.PEAK_OUT);
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
