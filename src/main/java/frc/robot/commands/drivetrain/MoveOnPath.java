/**
 *
 */
/*
package frc.robot.commands.drivetrain;

import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.SetValueMotionProfile;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.DriveTrain.DriveTrainSide;
import frc.robot.util.DriveAssist.DriveDirection;
import frc.robot.util.MercMath;
import frc.robot.util.MercTalonSRX;
//import org.apache.commons.csv.CSVFormat;
//import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Use motion profiling to move on a specified path
 */
/*
public class MoveOnPath extends CommandBase {
    private static Notifier trajectoryProcessor;
    private final int TRAJECTORY_SIZE;
    private TalonSRX left;
    private TalonSRX right;
    private Reader rightTrajCSV, leftTrajCSV;
    private List<CSVRecord> trajectoryListRight = new ArrayList<CSVRecord>(), trajectoryListLeft = new ArrayList<CSVRecord>();
    private MotionProfileStatus statusLeft, statusRight;
    private boolean isRunning;
    private int dir;
    private String name;
    private DriveTrain driveTrain;
    public MoveOnPath(String filename, MPDirection direction) throws FileNotFoundException {
        this(filename);
        switch (direction) {
            case BACKWARD:
                dir = -1;
                break;
            case FORWARD:
            default:
                dir = 1;
                break;
        }
    }

    public MoveOnPath(String filename, DriveTrain driveTrain) throws FileNotFoundException {
        super.addRequirements(driveTrain);
        this.driveTrain = driveTrain;
        setName("MoveOnPath-" + filename);

        name = filename;

        try {
            if (this.driveTrain.getDirection() == DriveDirection.CARGO) {
                dir = -1;
                leftTrajCSV = new FileReader("/home/lvuser/deploy/trajectories/PathWeaver/output/" + filename + ".right.pf1.csv");
                rightTrajCSV = new FileReader("/home/lvuser/deploy/trajectories/PathWeaver/output/" + filename + ".left.pf1.csv");
            } else if (this.driveTrain.getDirection() == DriveDirection.HATCH) {
                dir = 1;
                leftTrajCSV = new FileReader("/home/lvuser/deploy/trajectories/PathWeaver/output/" + filename + ".right.pf1.csv");
                rightTrajCSV = new FileReader("/home/lvuser/deploy/trajectories/PathWeaver/output/" + filename + ".left.pf1.csv");
            }
            CSVFormat.RFC4180.withFirstRecordAsHeader().parse(leftTrajCSV).forEach(record -> trajectoryListLeft.add(record));
            CSVFormat.RFC4180.withFirstRecordAsHeader().parse(rightTrajCSV).forEach(record -> trajectoryListRight.add(record));
        } catch (Exception e) {
            System.out.println("this a'int a profile fella");
            e.printStackTrace();
            end(false);
        }

        left = ((MercTalonSRX) this.driveTrain.getLeftLeader()).get();
        right = ((MercTalonSRX) this.driveTrain.getRightLeader()).get();

        if (trajectoryProcessor == null) {
            trajectoryProcessor = new Notifier(() -> {
                left.processMotionProfileBuffer();
                right.processMotionProfileBuffer();
            });
        }

        statusLeft = new MotionProfileStatus();
        statusRight = new MotionProfileStatus();

        if (trajectoryListLeft != null) {
            TRAJECTORY_SIZE = trajectoryListLeft.size();
        } else {
            TRAJECTORY_SIZE = 0;
            end(false);
        }
    }

    //Called just before this Command runs for the first time.
    public void initialize() {

        System.out.println("running MP");

        if (this.driveTrain.isInMotionMagicMode())
            this.driveTrain.initializeNormalMotionFeedback();

        // Reset command state
        reset();

        dir = this.driveTrain.getDirection() == DriveDirection.HATCH ? 1 : -1;

        this.driveTrain.configPIDSlots(DriveTrainSide.LEFT, DriveTrain.DRIVE_MOTION_PROFILE_SLOT, DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);
        this.driveTrain.configPIDSlots(DriveTrainSide.RIGHT, DriveTrain.DRIVE_MOTION_PROFILE_SLOT, DriveTrain.DRIVE_SMOOTH_MOTION_SLOT);

        // Change motion control frame period
        left.changeMotionControlFramePeriod(10);
        right.changeMotionControlFramePeriod(10);

        // Fill TOP (API-level) buffer
        fillTopBuffer();

        // Start processing
        // i.e.: moving API points to RAM
        trajectoryProcessor.startPeriodic(0.005);
    }

    //Called repeatedly when this Command is scheduled to run.
    public void execute() {
        left.getMotionProfileStatus(statusLeft);
        right.getMotionProfileStatus(statusRight);

        // Give a slight buffer when we process to make sure we don't bite off more than
        // we can chew or however that metaphor goes.
        if (!isRunning && statusLeft.btmBufferCnt >= 5 && statusRight.btmBufferCnt >= 5) {
            setMotionProfileMode(SetValueMotionProfile.Enable);

            isRunning = true;
        }
    }

    @Override
    public boolean isFinished() {
        // If we're running, only finish if both talons
        // reach their last valid point
        return
                isRunning &&
                        statusLeft.activePointValid &&
                        statusLeft.isLast &&
                        statusRight.activePointValid &&
                        statusRight.isLast;
    }

    @Override
    public void end(boolean interrupted) {
        // Stop processing trajectories
        trajectoryProcessor.stop();

        left.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 10, RobotMap.CTRE_TIMEOUT);
        right.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 10, RobotMap.CTRE_TIMEOUT);

        Robot.driveTrain.stop();

    }
    private void fillTopBuffer() {
        for (int i = 0; i < TRAJECTORY_SIZE; i++) {

            CSVRecord profileLeft = trajectoryListLeft.get(i);
            CSVRecord profileRight = trajectoryListRight.get(i);

            TrajectoryPoint trajPointL = new TrajectoryPoint();
            TrajectoryPoint trajPointR = new TrajectoryPoint();

            // NOTE: Encoder ticks are backwards, we need to work with that.
            double currentPosL = Double.parseDouble(profileLeft.get("position")) * dir;
            double currentPosR = Double.parseDouble(profileRight.get("position")) * dir;

            double velocityL = Double.parseDouble(profileLeft.get("velocity")) * dir;
            double velocityR = Double.parseDouble(profileRight.get("velocity")) * dir;

            // For each point, fill our structure and pass it to API
            trajPointL.position = MercMath.feetToEncoderTicks(currentPosL); //Convert Revolutions to Units
            trajPointR.position = MercMath.feetToEncoderTicks(currentPosR); //Convert Revolutions to Units
            trajPointL.velocity = MercMath.revsPerMinuteToTicksPerTenth(velocityL); //Convert RPM to Units/100ms
            trajPointR.velocity = MercMath.revsPerMinuteToTicksPerTenth(velocityR); //Convert RPM to Units/100ms

            trajPointL.profileSlotSelect0 = trajPointR.profileSlotSelect0 = DriveTrain.DRIVE_MOTION_PROFILE_SLOT;

            // Sets the duration of each trajectory point to 20ms
            trajPointL.timeDur = trajPointR.timeDur = 20;

            // Set these to true on the first point
            trajPointL.zeroPos = trajPointR.zeroPos = i == 0;

            // Set these to true on the last point
            trajPointL.isLastPoint = trajPointR.isLastPoint = i == TRAJECTORY_SIZE - 1;

            // Push to API level buffer
            left.pushMotionProfileTrajectory(trajPointL);
            right.pushMotionProfileTrajectory(trajPointR);
        }
    }

    private void setMotionProfileMode(SetValueMotionProfile value) {
        left.set(ControlMode.MotionProfile, value.value);
        right.set(ControlMode.MotionProfile, value.value);
    }

    private void reset() {
        // Reset flags and motion profile modes
        isRunning = false;
        setMotionProfileMode(SetValueMotionProfile.Disable);
        left.getSensorCollection().setQuadraturePosition(0, RobotMap.CTRE_TIMEOUT);
        right.getSensorCollection().setQuadraturePosition(0, RobotMap.CTRE_TIMEOUT);

        // Clear the trajectory buffer
        left.clearMotionProfileTrajectories();
        right.clearMotionProfileTrajectories();
    }

    public String getFilename() {
        return name;
    }

    public enum MPDirection {
        BACKWARD, FORWARD
    }
}
*/