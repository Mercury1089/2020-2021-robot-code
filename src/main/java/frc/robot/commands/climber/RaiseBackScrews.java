/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands.climber;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FollowerType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Climber.ScrewMotor;
import frc.robot.util.MercMath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RaiseBackScrews extends Command {
  private final Logger LOG = LogManager.getLogger(RaiseBackScrews.class);
  private WPI_TalonSRX backRight, backLeft, front;

  private final double CLIMB_DIST_INCHES = 22, END_POS, FRONT_START_POS;
  private double setPos;

  public RaiseBackScrews(double endPositionInches) {
    requires(Robot.climber);

    backRight = (WPI_TalonSRX)Robot.climber.getBackRight();
    backLeft = (WPI_TalonSRX)Robot.climber.getBackLeft();
    front = (WPI_TalonSRX)Robot.climber.getFront();

    setPos = MercMath.inchesToEncoderTicks(CLIMB_DIST_INCHES);
    END_POS = MercMath.inchesToEncoderTicks(endPositionInches);

    //Find the height of the front 
    FRONT_START_POS = Robot.climber.getFrontHeightInTicks();

    setName("RaiseBackScrews Command");
    LOG.info(getName() + " Constructed");
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    Robot.climber.resetEncoders();

    if (!Robot.climber.isInLiftMode())
      Robot.driveTrain.initializeMotionMagicFeedback();
    
    //onTargetCount = 0;
    //initialCheckCount = 0;

    /* Motion Magic Configurations */
    backRight.configMotionAcceleration(1000, RobotMap.CTRE_TIMEOUT);
    backRight.configMotionCruiseVelocity(1000, RobotMap.CTRE_TIMEOUT); //TEMP VALUE
    front.configMotionAcceleration(1000, RobotMap.CTRE_TIMEOUT);
    front.configMotionCruiseVelocity(1000, RobotMap.CTRE_TIMEOUT); //TEMP VALUE

    int closedLoopTimeMs = 1;
    backRight.configClosedLoopPeriod(0, closedLoopTimeMs, RobotMap.CTRE_TIMEOUT);
    backRight.configClosedLoopPeriod(1, closedLoopTimeMs, RobotMap.CTRE_TIMEOUT);
    front.configClosedLoopPeriod(0, closedLoopTimeMs, RobotMap.CTRE_TIMEOUT);
    front.configClosedLoopPeriod(1, closedLoopTimeMs, RobotMap.CTRE_TIMEOUT);

    backRight.configAuxPIDPolarity(false, RobotMap.CTRE_TIMEOUT);
    front.configAuxPIDPolarity(false, RobotMap.CTRE_TIMEOUT);

    Robot.climber.configPIDSlots(ScrewMotor.BACK_RIGHT, Climber.LIFT_BR_RUN, Climber.LIFT_BR_ADJUST);
    Robot.climber.configPIDSlots(ScrewMotor.FRONT, Climber.LIFT_FRONT_RUN, Climber.LIFT_FRONT_ADJUST);
    
    LOG.info(getName() + " Initialized");
  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    backRight.set(ControlMode.MotionMagic, setPos, DemandType.AuxPID, 0);
    backLeft.follow(backRight, FollowerType.AuxOutput1);
    LOG.info(getName() + " Executed");
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return Robot.climber.getFrontHeightInTicks() == FRONT_START_POS
        && Robot.climber.getBackLeftHeightInTicks() == END_POS
        && Robot.climber.getBackRightHeightInTicks() == END_POS;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
    Robot.climber.stop();
    LOG.info(getName() + " Ended");
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
    LOG.info(getName() + " Interrupted");  
    end();
  }
}
