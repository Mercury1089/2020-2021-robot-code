package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU_StatusFrame;
import com.revrobotics.CANEncoder;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotMap;
import frc.robot.RobotMap.CAN;
import frc.robot.util.*;
import frc.robot.util.DriveAssist.DriveDirection;
import frc.robot.util.interfaces.IMercMotorController;
import frc.robot.util.interfaces.IMercPIDTunable;
import frc.robot.util.interfaces.IMercShuffleBoardPublisher;

/**
 * Subsystem that encapsulates the driveAssist train.
 * This contains the {@link DriveAssist} needed to driveAssist manually
 * using the motor controllers.
 */
public class DriveTrain extends SubsystemBase implements IMercShuffleBoardPublisher, IMercPIDTunable {

    public static final int DRIVE_PID_SLOT = 0,
        DRIVE_SMOOTH_MOTION_SLOT = 1,
        DRIVE_MOTION_PROFILE_SLOT = 2,
        DRIVE_SMOOTH_TURN_SLOT = 3;
    public static final int REMOTE_DEVICE_0 = 0,
        REMOTE_DEVICE_1 = 1;
    public static final int PRIMARY_LOOP = 0,
        AUXILIARY_LOOP = 1;
    public static final int MAG_ENCODER_TICKS_PER_REVOLUTION = 4096,
        NEO_ENCODER_TICKS_PER_REVOLUTION = 42,
        PIGEON_NATIVE_UNITS_PER_ROTATION = 8192;
    public static final double MAX_SPEED = 1,
        MIN_SPEED = -1;
    public static final double GEAR_RATIO = 1,
        MAX_RPM = 545,
        WHEEL_DIAMETER_INCHES = 5.8;
    public static final double NOMINAL_OUT = 0.0,
                               PEAK_OUT = 1.0;

    private PIDGain driveGains, smoothGains, motionProfileGains, turnGains;

    private IMercMotorController leaderLeft, leaderRight, followerLeft, followerRight;
    private DriveAssist driveAssist;
    private PigeonIMU podgeboi;
    //private LIDAR lidar;
    private DriveTrainLayout layout;
    private boolean isInMotionMagicMode;

    /**
     * Creates the drivetrain, assuming that there are four controllers.
     *
     * @param layout The layout of motor controllers used on the drivetrain
     */
    public DriveTrain(DriveTrain.DriveTrainLayout layout) {
        //This should eventually be fully configurable
        // At this point it's based on what the layout is

        super();
        setName("DriveTrain");
        this.layout = layout;
        switch (layout) {
            case FALCONS:
                leaderLeft = new MercTalonSRX(CAN.DRIVETRAIN_ML);
                leaderRight = new MercTalonSRX(CAN.DRIVETRAIN_MR);
                followerLeft = new MercTalonSRX(CAN.DRIVETRAIN_FL);
                followerRight = new MercTalonSRX(CAN.DRIVETRAIN_FR);

                //Initialize CAN Coder

                //TODO leftCANCoder = new CANCoder(RobotMap.CAN.CANCODER_ML);
                //TODO rightCANCoder = new CANCoder(RobotMap.CAN.CANCODER_MR);
                break;
            case TALONS_VICTORS:
                leaderLeft = new MercTalonSRX(CAN.DRIVETRAIN_ML);
                leaderRight = new MercTalonSRX(CAN.DRIVETRAIN_MR);
                followerLeft = new MercVictorSPX(CAN.DRIVETRAIN_FL);
                followerRight = new MercVictorSPX(CAN.DRIVETRAIN_FR);
                break;
        }

        //Initialize podgeboi
        podgeboi = new PigeonIMU(CAN.PIGEON);
        podgeboi.configFactoryDefault();

        leaderLeft.configRemoteFeedbackFilter(podgeboi.getDeviceID(), RemoteSensorSource.valueOf(2), 1);
        leaderRight.configRemoteFeedbackFilter(podgeboi.getDeviceID(), RemoteSensorSource.valueOf(2), 1);

        //CANifier and distance sensors

        //Account for motor orientation.
        leaderLeft.setInverted(false);
        followerLeft.setInverted(false);
        leaderRight.setInverted(true);
        followerRight.setInverted(true);

        //Set neutral mode to Brake to make sure our motor controllers are all in brake mode by default
        setNeutralMode(NeutralMode.Brake);

        //Account for encoder orientation.
        leaderLeft.setSensorPhase(true);
        leaderRight.setSensorPhase(true);

        //Config feedback sensors for each PID slot, ready for MOTION PROFILING
        initializeMotionMagicFeedback();

        // Config PID
        setPIDGain(DRIVE_PID_SLOT, new PIDGain(0.125, 0.0, 0.05, 0.0, .75));
        setPIDGain(DRIVE_SMOOTH_MOTION_SLOT, new PIDGain(0.6, 0.00032, 0.45, getFeedForward(), 1.0));
        setPIDGain(DRIVE_MOTION_PROFILE_SLOT, new PIDGain(0.0225, 0.0, 0.0, getFeedForward(), 1.0));
        setPIDGain(DRIVE_SMOOTH_TURN_SLOT, new PIDGain(1.075, 0.0, 0.0, 0.0, 1.0));

        resetEncoders();

        driveAssist = new DriveAssist(leaderLeft, leaderRight, DriveDirection.LIMELIGHT);

        // Set follower control on back talons. Use follow() instead of ControlMode.Follower so that Talons can follow Victors and vice versa.
        followerLeft.follow(leaderLeft);
        followerRight.follow(leaderRight);

        configVoltage(NOMINAL_OUT, PEAK_OUT);
        setMaxOutput(PEAK_OUT);

        stop();
    }

    public Command getDefaultCommand(){
        return CommandScheduler.getInstance().getDefaultCommand(this);
    }

    public void setDefaultCommand(Command command){
        CommandScheduler.getInstance().setDefaultCommand(this, command);
    }

    public void initializeNormalMotionFeedback() {

        // TODO - set up Falcon encoders here...
        leaderLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, PRIMARY_LOOP);
        leaderRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, PRIMARY_LOOP); 
        leaderRight.configSelectedFeedbackCoefficient(1.0, DriveTrain.PRIMARY_LOOP);

        isInMotionMagicMode = false;
    }

    public void initializeMotionMagicFeedback() {
        /* Configure left's encoder as left's selected sensor */
        if (layout == DriveTrainLayout.TALONS_VICTORS){
            leaderLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DriveTrain.PRIMARY_LOOP);

            /* Configure the Remote Talon's selected sensor as a remote sensor for the right Talon */
            leaderRight.configRemoteFeedbackFilter(leaderLeft.getPort(), RemoteSensorSource.TalonSRX_SelectedSensor, DriveTrain.REMOTE_DEVICE_0);

            /* Configure the Pigeon IMU to the other remote slot available on the right Talon */
            leaderRight.configRemoteFeedbackFilter(getPigeon().getDeviceID(), RemoteSensorSource.Pigeon_Yaw, DriveTrain.REMOTE_DEVICE_1);

            /* Setup Sum signal to be used for Distance */
            leaderRight.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor0);
            leaderRight.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.CTRE_MagEncoder_Relative);

            /* Configure Sum [Sum of both QuadEncoders] to be used for Primary PID Index */
            leaderRight.configSelectedFeedbackSensor(FeedbackDevice.SensorSum, DriveTrain.PRIMARY_LOOP);

            /* Scale Feedback by 0.5 to half the sum of Distance */
            leaderRight.configSelectedFeedbackCoefficient(0.5, DriveTrain.PRIMARY_LOOP);

            /* Configure Remote 1 [Pigeon IMU's Yaw] to be used for Auxiliary PID Index */
            leaderRight.configSelectedFeedbackSensor(FeedbackDevice.RemoteSensor1, DriveTrain.AUXILIARY_LOOP);

            /* Scale the Feedback Sensor using a coefficient */
            leaderRight.configSelectedFeedbackCoefficient(1, DriveTrain.AUXILIARY_LOOP);

            /* Set status frame periods to ensure we don't have stale data */
            leaderRight.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, 20);
            leaderRight.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, 20);
            leaderRight.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, 20);
            leaderRight.setStatusFramePeriod(StatusFrame.Status_10_Targets, 20);
            leaderLeft.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 20);
            getPigeon().setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR, 5);

        } else {
            leaderLeft.configSelectedFeedbackSensor(FeedbackDevice.RemoteSensor0, DriveTrain.PRIMARY_LOOP);

        }

        isInMotionMagicMode = true;
    }

    public void initializeMotionMagicFeedback(int framePeriodMs) {
        /* Configure left's encoder as left's selected sensor */
        if (layout == DriveTrainLayout.TALONS_VICTORS){
            leaderLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, DriveTrain.PRIMARY_LOOP);

            /* Configure the Remote Talon's selected sensor as a remote sensor for the right Talon */
            leaderRight.configRemoteFeedbackFilter(leaderLeft.getPort(), RemoteSensorSource.TalonSRX_SelectedSensor, DriveTrain.REMOTE_DEVICE_0);

            /* Configure the Pigeon IMU to the other remote slot available on the right Talon */
            leaderRight.configRemoteFeedbackFilter(getPigeon().getDeviceID(), RemoteSensorSource.Pigeon_Yaw, DriveTrain.REMOTE_DEVICE_1);

            /* Setup Sum signal to be used for Distance */
            leaderRight.configSensorTerm(SensorTerm.Sum0, FeedbackDevice.RemoteSensor0);
            leaderRight.configSensorTerm(SensorTerm.Sum1, FeedbackDevice.CTRE_MagEncoder_Relative);

            /* Configure Sum [Sum of both QuadEncoders] to be used for Primary PID Index */
            leaderRight.configSelectedFeedbackSensor(FeedbackDevice.SensorSum, DriveTrain.PRIMARY_LOOP);

            /* Scale Feedback by 0.5 to half the sum of Distance */
            leaderRight.configSelectedFeedbackCoefficient(0.5, DriveTrain.PRIMARY_LOOP);

            /* Configure Remote 1 [Pigeon IMU's Yaw] to be used for Auxiliary PID Index */
            leaderRight.configSelectedFeedbackSensor(FeedbackDevice.RemoteSensor1, DriveTrain.AUXILIARY_LOOP);

            /* Scale the Feedback Sensor using a coefficient */
            leaderRight.configSelectedFeedbackCoefficient(1, DriveTrain.AUXILIARY_LOOP);

            /* Set status frame periods to ensure we don't have stale data */
            leaderRight.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, framePeriodMs);
            leaderRight.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, framePeriodMs);
            leaderRight.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, framePeriodMs);
            leaderRight.setStatusFramePeriod(StatusFrame.Status_10_Targets, framePeriodMs);
            leaderLeft.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, framePeriodMs);
            getPigeon().setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR, 5);

        } else {
            leaderLeft.configSelectedFeedbackSensor(FeedbackDevice.RemoteSensor0, DriveTrain.PRIMARY_LOOP);

        }

        isInMotionMagicMode = true;
    }

    public void setStatusFramePeriod(int framePeriodMs) {
        leaderRight.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, framePeriodMs);
        leaderRight.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, framePeriodMs);
        leaderRight.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, framePeriodMs);
        leaderRight.setStatusFramePeriod(StatusFrame.Status_10_Targets, framePeriodMs);
        leaderLeft.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, framePeriodMs);
    }

    public void setStatusFramePeriod(int framePeriodMs, int pigeonFramePeriodMs) {
        leaderRight.setStatusFramePeriod(StatusFrame.Status_12_Feedback1, framePeriodMs);
        leaderRight.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0, framePeriodMs);
        leaderRight.setStatusFramePeriod(StatusFrame.Status_14_Turn_PIDF1, framePeriodMs);
        leaderRight.setStatusFramePeriod(StatusFrame.Status_10_Targets, framePeriodMs);
        leaderLeft.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, framePeriodMs);
        getPigeon().setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR, pigeonFramePeriodMs);
    }

    public void configPIDSlots(DriveTrainSide dts, int primaryPIDSlot, int auxiliaryPIDSlot) {
        if (primaryPIDSlot >= 0) {
            if (dts == DriveTrainSide.RIGHT)
                leaderRight.selectProfileSlot(primaryPIDSlot, DriveTrain.PRIMARY_LOOP);
            else
                leaderLeft.selectProfileSlot(primaryPIDSlot, DriveTrain.PRIMARY_LOOP);
        }
        if (auxiliaryPIDSlot >= 0) {
            if (dts == DriveTrainSide.RIGHT)
                leaderRight.selectProfileSlot(auxiliaryPIDSlot, DriveTrain.AUXILIARY_LOOP);
            else
                leaderLeft.selectProfileSlot(auxiliaryPIDSlot, DriveTrain.AUXILIARY_LOOP);
        }

    }

    public void configClosedLoopPeakOutput(int driveTrainPIDSlot, double maxOut) {
        leaderLeft.configClosedLoopPeakOutput(driveTrainPIDSlot, maxOut);
        leaderRight.configClosedLoopPeakOutput(driveTrainPIDSlot, maxOut);
    }

    public void resetEncoders() {
        leaderLeft.resetEncoder();
        leaderRight.resetEncoder();
    }

    @Override
    public void periodic() {
    }

    /**
     * Sets the canifier LED output to the correct {@code LEDColor}. The
     * CANifier use BRG (not RGB) for its LED Channels
     */

    /**
     * Stops the driveAssist train.
     */
    public void stop() {
        leaderLeft.stop();
        leaderRight.stop();

    }

    /**
     * Sets both of the front talons to have a forward output of nominalOutput and peakOutput with the reverse output setClawState to the negated outputs.
     *
     * @param nominalOutput The desired nominal voltage output of the left and right talons, both forward and reverse.
     * @param peakOutput    The desired peak voltage output of the left and right talons, both forward and reverse
     */
    public void configVoltage(double nominalOutput, double peakOutput) {
        leaderLeft.configVoltage(nominalOutput, peakOutput);
        leaderRight.configVoltage(nominalOutput, peakOutput);
    }

    public DriveDirection getDirection() {
        return driveAssist.getDirection();
    }

    public void setDirection(DriveDirection dd) {
        driveAssist.setDirection(dd);
    }

    public void switchDirection(){
        if (getDirection() == DriveDirection.LIMELIGHT){
            setDirection(DriveDirection.ELECTRONICS_BOARD);
        }
        else{
            setDirection(DriveDirection.LIMELIGHT);
        }
    }

    public PigeonIMU getPigeon() {
        return podgeboi;
    }

    //public LIDAR getLidar() {
        //return lidar;
    //}

    public double getPigeonYaw() {
        double[] currYawPitchRoll = new double[3];
        podgeboi.getYawPitchRoll(currYawPitchRoll);
        return currYawPitchRoll[0];
    }

    public DriveTrainLayout getLayout() {
        return layout;
    }

    public boolean isInMotionMagicMode() {
        return isInMotionMagicMode;
    }

    public void resetPigeonYaw() {
        podgeboi.setYaw(0);
    }

    public double getLeftEncPositionInTicks() {
        return leaderLeft.getEncTicks();
    }

    public double getRightEncPositionInTicks() {
        return leaderRight.getEncTicks();
    }

    public double getLeftEncPositionInFeet() {
        return MercMath.getEncPosition(getLeftEncPositionInTicks());
    }

    public double getRightEncPositionInFeet() {
        return MercMath.getEncPosition(getRightEncPositionInTicks());
    }

    public IMercMotorController getLeftLeader() {
        return leaderLeft;
    }

    public IMercMotorController getRightLeader() {
        return leaderRight;
    }

    public IMercMotorController getLeftFollower() {
        return followerLeft;
    }

    public IMercMotorController getRightFollower() {
        return followerRight;
    }

    public DriveAssist getDriveAssist() {
        return driveAssist;
    }

    public double getFeedForward() {
        return MercMath.calculateFeedForward(MAX_RPM);
    }

    public void pidWrite(double output) {
        driveAssist.tankDrive(output, -output);
    }

    public void setMaxOutput(double maxOutput) {
        driveAssist.setMaxOutput(maxOutput);
    }

    public void setNeutralMode(NeutralMode neutralMode) {
        leaderLeft.setNeutralMode(neutralMode);
        leaderRight.setNeutralMode(neutralMode);
        followerLeft.setNeutralMode(neutralMode);
        followerRight.setNeutralMode(neutralMode);
    }

    public enum DriveTrainLayout {
        TALONS_VICTORS,
        FALCONS
    }

    public enum DriveTrainSide {
        RIGHT,
        LEFT
    }

    public enum LEDColor {
        RED(1.0, 0.0, 0.0),
        GREEN(0.0, 0.0, 1.0),
        BLUE(0.0, 0.0, 1.0),
        YELLOW(1.0, 1.0, 0.0),
        CYAN(0.0, 1.0, 1.0),
        MAGENTA(1.0, 0.0, 1.0),
        WHITE(1.0, 1.0, 1.0),
        BLACK(0.0, 0.0, 0.0);

        private double r, g, b;

        LEDColor(double r, double g, double b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public double getRed() {
            return r;
        }

        public double getGreen() {
            return g;
        }

        public double getBlue() {
            return b;
        }
    }

    //Publish values to ShuffleBoard
    public void publishValues() {
        //Drive direction
        SmartDashboard.putString(getName() + "/Direction", getDirection().name());
        //Encoder positions
        SmartDashboard.putNumber(getName() + "/Left Encoder (feet)", getLeftEncPositionInFeet());
        SmartDashboard.putNumber(getName() + "/Right Encoder (feet)", getRightEncPositionInFeet());
        SmartDashboard.putNumber(getName() + "/Left Encoder (ticks)", getLeftEncPositionInTicks());
        SmartDashboard.putNumber(getName() + "/Right Encoder (ticks)", getRightEncPositionInTicks());
        //Wheel RPM
        SmartDashboard.putNumber(getName() + "/Left RPM", MercMath.ticksPerTenthToRevsPerMinute(getLeftLeader().getEncVelocity()));
        SmartDashboard.putNumber(getName() + "/Right RPM", MercMath.ticksPerTenthToRevsPerMinute(getRightLeader().getEncVelocity()));
        //Angle From Pigeon
        SmartDashboard.putNumber(getName() + "/Yaw", getPigeonYaw());
    }

    @Override
    public int[] getSlots() {
        return new int[] {
            DRIVE_PID_SLOT,
            DRIVE_SMOOTH_MOTION_SLOT,
            DRIVE_MOTION_PROFILE_SLOT,
            DRIVE_SMOOTH_TURN_SLOT
        };
    }

    @Override
    public PIDGain getPIDGain(int slot) {
        PIDGain gains = null;
        switch (slot) {
            case DRIVE_PID_SLOT:
                gains = driveGains;
                break;
            case DRIVE_SMOOTH_MOTION_SLOT:
                gains = smoothGains;
                break;
            case DRIVE_MOTION_PROFILE_SLOT:
                gains = motionProfileGains;
                break;
            case DRIVE_SMOOTH_TURN_SLOT:
                gains = turnGains;
                break;
        }
        return gains;
    }

    @Override
    public void setPIDGain(int slot, PIDGain gains) {
        switch (slot) {
            case DRIVE_PID_SLOT:
                driveGains = gains;
                leaderRight.configPID(DRIVE_PID_SLOT, driveGains);
                leaderLeft.configPID(DRIVE_PID_SLOT, driveGains);
                break;
            case DRIVE_SMOOTH_MOTION_SLOT:
                smoothGains = gains;
                leaderRight.configPID(DRIVE_SMOOTH_MOTION_SLOT, smoothGains);
                leaderLeft.configPID(DRIVE_SMOOTH_MOTION_SLOT, smoothGains);
                break;
            case DRIVE_MOTION_PROFILE_SLOT:
                motionProfileGains = gains;
                leaderRight.configPID(DRIVE_MOTION_PROFILE_SLOT, motionProfileGains);
                leaderLeft.configPID(DRIVE_MOTION_PROFILE_SLOT, motionProfileGains);
                break;
            case DRIVE_SMOOTH_TURN_SLOT:
                turnGains = gains;
                leaderRight.configPID(DRIVE_SMOOTH_TURN_SLOT, turnGains);
                leaderLeft.configPID(DRIVE_SMOOTH_TURN_SLOT, turnGains);
                break;
        }
    }
}
