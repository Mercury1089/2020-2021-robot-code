package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.sensors.Limelight.LimelightLEDState;
import frc.robot.subsystems.*;
import frc.robot.subsystems.DriveTrain.DriveTrainLayout;
import frc.robot.util.DriveAssist.DriveDirection;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 * <p>
 * GUYS, WE FOUND THE ROBOT
 */
public class Robot extends TimedRobot {

    public static DriveTrain driveTrain;

    public static OI oi;

    /**
     * This function is run when the robot is first started up and should be used
     * for any initialization code.
     */
    @Override
    public void robotInit() {

        driveTrain = new DriveTrain(DriveTrainLayout.TALONS);

        driveTrain.setDirection(DriveDirection.HATCH);

        CommandScheduler.getInstance().enable();

        CommandScheduler.getInstance().registerSubsystem(driveTrain);

        CommandScheduler.getInstance().setDefaultCommand(driveTrain, driveTrain.getDefaultCommand());

        oi = new OI();
    }

    @Override
    public void robotPeriodic() {
        oi.updateDash();
    }

    @Override
    public void disabledInit() {
    }

    @Override
    public void disabledPeriodic() {
        CommandScheduler.getInstance().run();
    }

    @Override
    public void autonomousInit() {
        driveTrain.getLimelight().setLEDState(LimelightLEDState.ON);
    }

    @Override
    public void autonomousPeriodic() {
        CommandScheduler.getInstance().run();
    }

    @Override
    public void teleopInit() {
        driveTrain.getLimelight().setLEDState(LimelightLEDState.ON);
    }

    @Override
    public void teleopPeriodic() {
        CommandScheduler.getInstance().run();
    }

    @Override
    public void testPeriodic() {
        super.testInit();
        driveTrain.getLimelight().setLEDState(LimelightLEDState.ON);
    }
}
