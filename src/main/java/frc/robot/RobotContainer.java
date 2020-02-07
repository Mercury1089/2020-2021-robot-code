package frc.robot;

import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import java.io.FileNotFoundException;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

import frc.robot.RobotMap.DS_USB;
import frc.robot.RobotMap.GAMEPAD_BUTTONS;
import frc.robot.RobotMap.JOYSTICK_BUTTONS;

import frc.robot.commands.drivetrain.DegreeRotate;
import frc.robot.commands.drivetrain.DriveDistance;
import frc.robot.commands.drivetrain.DriveWithJoysticks;
import frc.robot.commands.drivetrain.MoveHeading;
import frc.robot.commands.drivetrain.MoveOnPath;
import frc.robot.commands.drivetrain.MoveOnTrajectory;
import frc.robot.commands.drivetrain.RotateToTarget;
import frc.robot.commands.drivetrain.TestSequentialCommandGroup;
import frc.robot.commands.drivetrain.DriveWithJoysticks.DriveType;

import frc.robot.commands.feeder.RunFeeder;
import frc.robot.commands.hopper.RunHopperBelt;
import frc.robot.commands.intake.RunIntake;
import frc.robot.commands.limelightCamera.SwitchLEDState;

import frc.robot.commands.shooter.RunShooter;
import frc.robot.commands.shooter.RunShooterRPMBangBang;
import frc.robot.commands.shooter.RunShooterRPMPID;
import frc.robot.commands.spinner.ColorControl;
import frc.robot.commands.spinner.RotationControl;
import frc.robot.commands.spinner.RunSpinner;
import frc.robot.commands.spinner.ShiftOnScale;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Feeder;
import frc.robot.subsystems.Hopper;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.LimelightCamera;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Spinner;

import frc.robot.subsystems.DriveTrain.DriveTrainLayout;
import frc.robot.subsystems.Elevator.ElevatorPosition;
import frc.robot.subsystems.Shooter.ShooterMode;
import frc.robot.commands.elevator.DriveElevator;
import frc.robot.commands.elevator.GoToSetPosition;
import frc.robot.commands.elevator.ManualElevator;
import frc.robot.util.ShuffleDash;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */

public class RobotContainer {
    public static final double DEADZONE = 0.08;

    private ShuffleDash shuffleDash;

    private Joystick rightJoystick, leftJoystick, gamepad;

    private JoystickButton left1, left2, left3, left4, left5, left6, left7, left8, left9, left10, left11;
    private JoystickButton right1, right2, right3, right4, right5, right6, right7, right8, right9, right10, right11;
    private JoystickButton gamepadA, gamepadB, gamepadX, gamepadY, gamepadRB, gamepadLB, gamepadBack, gamepadStart, gamepadLeftStickButton, gamepadRightStickButton;

    private DriveTrain driveTrain;
    private Shooter shooter;
    private Intake intake;
    private Feeder feeder;
    private Hopper hopper;
    private Spinner spinner;
    private Elevator elevator;
    private LimelightCamera limelightCamera;
    

    private CommandGroupBase autonCommand;

    public RobotContainer() {
        leftJoystick = new Joystick(DS_USB.LEFT_STICK);
        rightJoystick = new Joystick(DS_USB.RIGHT_STICK);
        gamepad = new Joystick(DS_USB.GAMEPAD);

        driveTrain = new DriveTrain(DriveTrainLayout.TALONS_VICTORS);
        driveTrain.setDefaultCommand(new DriveWithJoysticks(DriveType.ARCADE, driveTrain));

        shooter = new Shooter(ShooterMode.ONE_WHEEL);
        shooter.setDefaultCommand(new RunCommand(() -> shooter.setSpeed(0.0), shooter));

        intake = new Intake();
    
        feeder = new Feeder();
        intake = new Intake();
        hopper = new Hopper();       
        limelightCamera = new LimelightCamera();
        spinner = new Spinner();
        elevator = new Elevator();
        
        shuffleDash = new ShuffleDash();
        //shuffleDash.addPublisher(shooter);
        shuffleDash.addPublisher(driveTrain);
        //shuffleDash.addPublisher(spinner);
        shuffleDash.addPublisher(intake);
        shuffleDash.addPublisher(limelightCamera);
        shuffleDash.addPublisher(elevator);
        
        shuffleDash.addPIDTunable(shooter, "Shooter");
        shuffleDash.addPIDTunable(driveTrain, "DriveTrain");
        
        autonCommand = new SequentialCommandGroup();
        autonCommand.addRequirements(driveTrain);
        initializeAutonCommand();
        
        initalizeJoystickButtons();
        
        left2.whenPressed(() -> shooter.setSpeed(0.0), shooter);
        left3.whenPressed(new RunShooter(shooter));
        left4.whenPressed(new RunShooterRPMBangBang(shooter));
        left5.whenPressed(new RunShooterRPMPID(shooter));
        left6.whenPressed(new SwitchLEDState(limelightCamera));
        left7.whileHeld(new RunHopperBelt(hopper));
        left8.whenPressed(new RunFeeder(feeder));
        try{
            left9.whenPressed(new MoveOnTrajectory("Unnamed.wpilib.json", driveTrain));            
        } catch(FileNotFoundException e){
            System.out.println(e);
        }
        
        right1.whileHeld(new RunIntake(intake));
        right2.whileHeld(new RunFeeder(feeder));
        right3.whileHeld(new DriveElevator(elevator));
        right4.whenPressed(new DriveWithJoysticks(DriveType.ARCADE, driveTrain));
        right5.whenPressed(new RotationControl(spinner));
        right6.whenPressed(new TestSequentialCommandGroup(driveTrain, limelightCamera));
        right7.whenPressed(new DegreeRotate(45, driveTrain));
        right8.whenPressed(new DegreeRotate(90, driveTrain));
        right9.whenPressed(new DegreeRotate(135, driveTrain));
        right10.whenPressed(new DriveDistance(150.0, driveTrain));
        right11.whenPressed(new RotateToTarget(driveTrain, limelightCamera));

        gamepadY.whenHeld(new RunFeeder(feeder));
        gamepadX.whenHeld(new RunHopperBelt(hopper));
        gamepadB.whenPressed(new ColorControl(spinner));
        gamepadA.whenPressed(new GoToSetPosition(elevator, ElevatorPosition.CONTROL_PANEL));
        gamepadRightStickButton.toggleWhenPressed(new ShiftOnScale(spinner));
        gamepadLeftStickButton.toggleWhenPressed(new ManualElevator(elevator));
        

    }

    public String getAutonFirstStep() {
        return shuffleDash.getFirstStep();
    }

    public double getJoystickX(int port) {
        switch (port) {
            case DS_USB.LEFT_STICK:
                return leftJoystick.getX();
            case DS_USB.RIGHT_STICK:
                return rightJoystick.getX();
            default:
                return 0;
        }
    }

    public double getJoystickY(int port) {
        switch (port) {
            case DS_USB.LEFT_STICK:
                return leftJoystick.getY();
            case DS_USB.RIGHT_STICK:
                return rightJoystick.getY();
            default:
                return 0;
        }
    }

    public double getJoystickZ(int port) {
        switch (port) {
            case DS_USB.LEFT_STICK:
                return leftJoystick.getZ();
            case DS_USB.RIGHT_STICK:
                return rightJoystick.getZ();
            default:
                return 0;
        }
    }

    public double getGamepadAxis(int axis) {
        return ((axis % 2 != 0 && axis != 3) ? -1.0 : 1.0) * gamepad.getRawAxis(axis);
    }

    public void updateDash() {
        //shuffleDash.updateDash();
    }

    private void initalizeJoystickButtons() {
        left1 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN1);
        left2 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN2);
        left3 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN3);
        left4 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN4);
        left5 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN5);
        left6 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN6);
        left7 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN7);
        left8 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN8);
        left9 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN9);
        left10 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN10);
        left11 = new JoystickButton(leftJoystick, JOYSTICK_BUTTONS.BTN11);

        right1 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN1);
        right2 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN2);
        right3 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN3);
        right4 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN4);
        right5 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN5);
        right6 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN6);
        right7 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN7);
        right8 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN8);
        right9 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN9);
        right10 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN10);
        right11 = new JoystickButton(rightJoystick, JOYSTICK_BUTTONS.BTN11);

        gamepadA = new JoystickButton(gamepad, GAMEPAD_BUTTONS.A);
        gamepadB = new JoystickButton(gamepad, GAMEPAD_BUTTONS.B);
        gamepadX = new JoystickButton(gamepad, GAMEPAD_BUTTONS.X);
        gamepadY = new JoystickButton(gamepad, GAMEPAD_BUTTONS.Y);
        gamepadRB = new JoystickButton(gamepad, GAMEPAD_BUTTONS.RB);
        gamepadLB = new JoystickButton(gamepad, GAMEPAD_BUTTONS.LB);
        gamepadBack = new JoystickButton(gamepad, GAMEPAD_BUTTONS.BACK);
        gamepadStart = new JoystickButton(gamepad, GAMEPAD_BUTTONS.START);
        gamepadLeftStickButton = new JoystickButton(gamepad, GAMEPAD_BUTTONS.L3);
        gamepadRightStickButton = new JoystickButton(gamepad, GAMEPAD_BUTTONS.R3);
    }

    //Eventually this will link to our auton app on the shuffledash
    public void initializeAutonCommand(){
        autonCommand.addRequirements(this.driveTrain);
        try{
            autonCommand.addCommands(new MoveOnTrajectory("Curvy.wpilib.json", this.driveTrain));            
        } catch(FileNotFoundException e){
            System.out.println(e);
        }
    }

    public CommandGroupBase getAutonCommand(){
        return this.autonCommand;
    }
}
