/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.drivetrain;

import java.util.List;

import com.ctre.phoenix.motion.SetValueMotionProfile;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;

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
  
  private DriveTrain driveTrain;
  private MercTalonSRX left, right;
  private List<TrajectoryPoint> trajectoryPoints;
  private String pathName;

  private boolean isRunning;

  public MoveOnTrajectory(String path, DriveTrain driveTrain) {
    addRequirements(driveTrain);
    this.driveTrain = driveTrain;
    setName("Move On Trajectory" + path);
    pathName = path;
    trajectoryPoints = MercPathLoader.loadPath(pathName);
    left = (MercTalonSRX)this.driveTrain.getLeftLeader();
    right = (MercTalonSRX)this.driveTrain.getRightLeader();
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
    if (this.driveTrain.isInMotionMagicMode())
      this.driveTrain.initializeNormalMotionFeedback();
    //Fix below
      reset();
    this.driveTrain.configPIDSlots(DriveTrainSide.LEFT, DriveTrain.DRIVE_MOTION_PROFILE_SLOT, DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);
    this.driveTrain.configPIDSlots(DriveTrainSide.RIGHT, DriveTrain.DRIVE_MOTION_PROFILE_SLOT, DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);

    this.driveTrain.setNeutralMode(NeutralMode.Brake);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }

  //All methods below may not be needed

  public void reset() {
    // Reset flags and motion profile modes
    isRunning = false;
    setMotionProfileMode(SetValueMotionProfile.Disable);
    left.get().getSensorCollection().setQuadraturePosition(0, RobotMap.CTRE_TIMEOUT);
    right.get().getSensorCollection().setQuadraturePosition(0, RobotMap.CTRE_TIMEOUT);

    // Clear the trajectory buffer
    left.get().clearMotionProfileTrajectories();
    right.get().clearMotionProfileTrajectories();
  }

  private void setMotionProfileMode(SetValueMotionProfile value) {
    left.set(ControlMode.MotionProfile, value.value);
    right.set(ControlMode.MotionProfile, value.value);
  }

  public void fillTopBuffer() {
    for(TrajectoryPoint point : trajectoryPoints) {
      left.get().pushMotionProfileTrajectory(point);
      right.get().pushMotionProfileTrajectory(point);
    }
  }

}
