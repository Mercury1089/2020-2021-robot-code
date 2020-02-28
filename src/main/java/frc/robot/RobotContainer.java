package frc.robot;

import java.io.FileNotFoundException;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.RobotMap.DS_USB;
import frc.robot.RobotMap.GAMEPAD_AXIS;
import frc.robot.RobotMap.GAMEPAD_BUTTONS;
import frc.robot.RobotMap.JOYSTICK_BUTTONS;
import frc.robot.commands.drivetrain.DegreeRotate;
import frc.robot.commands.drivetrain.DriveDistance;
import frc.robot.commands.drivetrain.DriveWithJoysticks;
import frc.robot.commands.drivetrain.DriveWithJoysticks.DriveType;
import frc.robot.commands.drivetrain.MoveOnTrajectory;
import frc.robot.commands.drivetrain.RotateToTarget;
import frc.robot.commands.drivetrain.TestSequentialCommandGroup;
import frc.robot.commands.elevator.AutomaticElevator;
import frc.robot.commands.feeder.AutoFeedBalls;
import frc.robot.commands.intake.RunIntake;
import frc.robot.commands.limelightCamera.SwitchLEDState;
import frc.robot.commands.shooter.FullyAutoAimbot;
import frc.robot.commands.shooter.RunShooter;
import frc.robot.commands.shooter.RunShooterRPMPID;
import frc.robot.commands.spinner.ColorControl;
import frc.robot.commands.spinner.RotationControl;
import frc.robot.commands.spinner.ShiftOnScale;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.DriveTrain.DriveTrainLayout;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.ElevatorPosition;
import frc.robot.subsystems.Feeder;
import frc.robot.subsystems.Hopper;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.IntakeArticulator;
import frc.robot.subsystems.LimelightCamera;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Shooter.ShooterMode;
import frc.robot.subsystems.Spinner;
import frc.robot.util.MercMotionProfile;
import frc.robot.util.MercMotionProfile.ProfileDirection;
import frc.robot.util.ShuffleDash;
import frc.robot.util.TriggerButton;

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
    private TriggerButton gamepadLT, gamepadRT;

    private DriveTrain driveTrain;
    private Shooter shooter;
    private Intake intake;
    private IntakeArticulator intakeArticulator;
    private Feeder feeder;
    private Hopper hopper;
    private Spinner spinner;
    private Elevator elevator;
    private LimelightCamera limelightCamera;
    
    private CommandGroupBase autonCommand = null;

    public RobotContainer() {
        leftJoystick = new Joystick(DS_USB.LEFT_STICK);
        rightJoystick = new Joystick(DS_USB.RIGHT_STICK);
        gamepad = new Joystick(DS_USB.GAMEPAD);

        driveTrain = new DriveTrain(DriveTrainLayout.FALCONS); //make sure to switch it back to Falcons
        driveTrain.setDefaultCommand(new DriveWithJoysticks(DriveType.ARCADE, driveTrain));

        shooter = new Shooter(ShooterMode.ONE_WHEEL);
        shooter.setDefaultCommand(new RunCommand(() -> shooter.setSpeed(0.0), shooter));
        
        hopper = new Hopper();       
        hopper.setDefaultCommand(new RunCommand(() -> hopper.setSpeed(0.0), hopper));

        intake = new Intake();
        intakeArticulator = new IntakeArticulator();
        feeder = new Feeder();
        intake = new Intake();
        limelightCamera = new LimelightCamera();
        spinner = new Spinner();
        elevator = new Elevator();
        
        shuffleDash = new ShuffleDash();
        shuffleDash.addPublisher(shooter);
        shuffleDash.addPublisher(driveTrain);
        //shuffleDash.addPublisher(spinner);
        shuffleDash.addPublisher(intake);
        shuffleDash.addPublisher(limelightCamera);
        shuffleDash.addPublisher(intakeArticulator);
        shuffleDash.addPublisher(elevator);
        shuffleDash.addPublisher(feeder);
        shuffleDash.addPublisher(hopper);
        shuffleDash.addPIDTunable(shooter, "Shooter");
        shuffleDash.addPIDTunable(driveTrain, "DriveTrain");
    
        initializeJoystickButtons();

        //driver controls
        //toggle intake in and out
        left1.whenPressed(new ParallelCommandGroup(new RunCommand(() -> intakeArticulator.setIntakeOut(), intakeArticulator), new RunIntake(intake)));
        left2.whenPressed(new ParallelCommandGroup(new RunCommand(() -> intake.setRollerSpeed(0.0), intake), new RunCommand(() -> intakeArticulator.setIntakeIn(), intakeArticulator)));
        left3.toggleWhenPressed(new RunShooterRPMPID(shooter));
        left4.whenPressed(new RunCommand(() -> driveTrain.resetEncoders(), driveTrain));
        
        System.out.println(intakeArticulator.getIntakePosition().toString());
        left6.whenPressed(new SwitchLEDState(limelightCamera));
        try {
            left9.whenPressed(new MoveOnTrajectory("Straight", driveTrain));            
        } catch(FileNotFoundException e) {
            System.out.println(e);
        } 

        right2.whenPressed(new FullyAutoAimbot(driveTrain, limelightCamera, shooter, feeder, hopper));
        right3.whenPressed(new RotateToTarget(driveTrain, limelightCamera));
        right4.whenPressed(new DriveWithJoysticks(DriveType.ARCADE, driveTrain));
        right5.whenPressed(new DegreeRotate(90, driveTrain));
        right6.whenPressed(new TestSequentialCommandGroup(driveTrain, limelightCamera));
        right7.whenPressed(new DriveDistance(120.0, driveTrain));
        try {
            SequentialCommandGroup cmd = new SequentialCommandGroup(
                //shooting
                //new RunCommand(() -> intakeArticulator.setIntakeOut(), intakeArticulator),
                //new ParallelCommandGroup(
                    new MoveOnTrajectory(new MercMotionProfile("LeftTargetZoneToTrench", ProfileDirection.BACKWARD), driveTrain),
                //    new RunIntake(intake)
                //), 
                new MoveOnTrajectory(new MercMotionProfile("TrenchBall", ProfileDirection.FORWARD), driveTrain),
                //new ParallelCommandGroup(
                    new MoveOnTrajectory(new MercMotionProfile("TrenchOtherBall", ProfileDirection.BACKWARD), driveTrain),
                //    new RunIntake(intake)
                //),
                //new ParallelCommandGroup(
                    new MoveOnTrajectory(new MercMotionProfile("ShootInTrench", ProfileDirection.FORWARD), driveTrain)//,
                //    new RunCommand(() -> intakeArticulator.setIntakeIn(), intakeArticulator),
                //)
                //Fully auto-aim bot
                //shoot
            );
            right9.whenPressed(cmd);        
        } catch(Exception e) {
            System.out.println(e);
        }
        try {
            right10.whenPressed(new MoveOnTrajectory(new MercMotionProfile("Center5BallRendezvous", ProfileDirection.BACKWARD), driveTrain));     
        } catch(FileNotFoundException e) {
            System.out.println(e);
        }
        try {
            right11.whenPressed(new MoveOnTrajectory(new MercMotionProfile("ShootInTrench", ProfileDirection.FORWARD), driveTrain));            
        } catch(FileNotFoundException e) {
            System.out.println(e);
        }

        //operator controls
        gamepadRB.whenPressed(new RotationControl(spinner));
        gamepadLB.whenPressed(new ColorControl(spinner));
        gamepadA.whenPressed(new AutomaticElevator(elevator, ElevatorPosition.MAX_HEIGHT));
        gamepadB.whenPressed(new AutomaticElevator(elevator, ElevatorPosition.CONTROL_PANEL));
        gamepadLeftStickButton.toggleWhenPressed(new ShiftOnScale(spinner));
        gamepadLT.toggleWhenPressed(new RunShooter(shooter));
        gamepadRT.toggleWhenPressed(new AutoFeedBalls(feeder, hopper, shooter, driveTrain));
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

    private void initializeJoystickButtons() {
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
        gamepadLT = new TriggerButton(gamepad, GAMEPAD_AXIS.leftTrigger);
        gamepadRT = new TriggerButton(gamepad, GAMEPAD_AXIS.rightTrigger);
    }
    
    public void initializeAutonCommand(){
        String selectedAuton = shuffleDash.getAuton();
        if(selectedAuton == null) {
            System.out.println("No Auton My Dude");
            return;
        } else if (selectedAuton.equals("Center5BallTrench")) {
            DriverStation.reportError("Center5BallTrench Auton", false);
            try {
                autonCommand = new SequentialCommandGroup(
                    //shooting
                    //new RunCommand(() -> intakeArticulator.setIntakeOut(), intakeArticulator),
                    //new ParallelCommandGroup(
                        new MoveOnTrajectory(new MercMotionProfile("CenterTargetZoneToTrench", ProfileDirection.BACKWARD), driveTrain),
                    //    new RunIntake(intake)
                    //), 
                    new MoveOnTrajectory(new MercMotionProfile("TrenchBall", ProfileDirection.FORWARD), driveTrain),
                    //new ParallelCommandGroup(
                        new MoveOnTrajectory(new MercMotionProfile("TrenchOtherBall", ProfileDirection.BACKWARD), driveTrain),
                    //    new RunIntake(intake)
                    //),
                    //new ParallelCommandGroup(
                        new MoveOnTrajectory(new MercMotionProfile("ShootInTrench", ProfileDirection.FORWARD), driveTrain)//,
                    //    new RunCommand(() -> intakeArticulator.setIntakeIn(), intakeArticulator),
                    //)
                    //Fully auto-aim bot
                    //shoot
                );
            } catch (FileNotFoundException e) {
                System.out.println(e);
            }  
        } else if (selectedAuton.equals("Left5BallTrench")) {
            if(driveTrain.isAligned()) {
                DriverStation.reportError("Left5BallTrench Auton", false);
                try {
                    autonCommand = new SequentialCommandGroup(
                        new ParallelCommandGroup(
                            new RunShooterRPMPID(shooter),
                            new AutoFeedBalls(feeder, hopper, shooter, driveTrain),
                            new WaitCommand(5)
                        ),
                        new RunCommand(() -> intakeArticulator.setIntakeOut(), intakeArticulator),
                        new ParallelCommandGroup(
                            new RunIntake(intake),
                            new MoveOnTrajectory(new MercMotionProfile("LeftTargetZoneToTrench", ProfileDirection.BACKWARD), driveTrain)
                        ),
                        new  MoveOnTrajectory(new MercMotionProfile("TrenchBall", ProfileDirection.FORWARD), driveTrain),
                        new ParallelCommandGroup(
                            new RunIntake(intake),
                            new MoveOnTrajectory(new MercMotionProfile("TrenchOtherBall", ProfileDirection.BACKWARD), driveTrain)
                        ),
                        new ParallelCommandGroup(
                            new RunCommand(() -> intakeArticulator.setIntakeIn(), intakeArticulator),
                            new MoveOnTrajectory(new MercMotionProfile("ShootInTrench", ProfileDirection.FORWARD), driveTrain)
                        ),
                        new ParallelCommandGroup(
                            new RotateToTarget(driveTrain, limelightCamera),
                            new RunShooterRPMPID(shooter),
                            new AutoFeedBalls(feeder, hopper, shooter, driveTrain)
                        )
                    );
                } catch (FileNotFoundException e) {
                    System.out.println(e);
                }      
            } else {          
                DriverStation.reportError("Left5BallTrench Auton", false);
                try {
                    autonCommand = new SequentialCommandGroup(
                        new ParallelCommandGroup(
                            new RotateToTarget(driveTrain, limelightCamera),
                            new RunShooterRPMPID(shooter),
                            new AutoFeedBalls(feeder, hopper, shooter, driveTrain),
                            new WaitCommand(5)
                        ),
                        new RunCommand(() -> intakeArticulator.setIntakeOut(), intakeArticulator),
                        new ParallelCommandGroup(
                            new RunIntake(intake),
                            new MoveOnTrajectory(new MercMotionProfile("LeftTargetZoneToTrench", ProfileDirection.BACKWARD), driveTrain)
                        ),
                        new  MoveOnTrajectory(new MercMotionProfile("TrenchBall", ProfileDirection.FORWARD), driveTrain),
                        new ParallelCommandGroup(
                            new RunIntake(intake),
                            new MoveOnTrajectory(new MercMotionProfile("TrenchOtherBall", ProfileDirection.BACKWARD), driveTrain)
                        ),
                        new ParallelCommandGroup(
                            new RunCommand(() -> intakeArticulator.setIntakeIn(), intakeArticulator),
                            new MoveOnTrajectory(new MercMotionProfile("ShootInTrench", ProfileDirection.BACKWARD), driveTrain)
                        ),
                        new ParallelCommandGroup(
                            new RotateToTarget(driveTrain, limelightCamera),
                            new RunShooterRPMPID(shooter),
                            new AutoFeedBalls(feeder, hopper, shooter, driveTrain)
                        )
                    );
                } catch (FileNotFoundException e) {
                    System.out.println(e);
                } 
            }
        } else if (selectedAuton.equals("Right5BallRendezvous")) {
            DriverStation.reportError("Right5BallRendezvous Auton", false);
            try {
                autonCommand = new SequentialCommandGroup(
                    //shooting
                    //new RunCommand(() -> intakeArticulator.setIntakeOut(), intakeArticulator),
                    //new ParallelCommandGroup(
                        new MoveOnTrajectory(new MercMotionProfile("Right5BallRendezvous", ProfileDirection.BACKWARD), driveTrain),
                    //    new RunIntake(intake)
                    //), 
                    //new ParallelCommandGroup(
                        new MoveOnTrajectory(new MercMotionProfile("RendezvousToShoot", ProfileDirection.FORWARD), driveTrain)//,
                    //    new RunCommand(() -> intakeArticulator.setIntakeIn(), intakeArticulator),
                    //)
                    //Fully auto-aim bot
                    //shoot
                );
            } catch (FileNotFoundException e) {
                System.out.println(e);
            }  
        } else if (selectedAuton.equals("StealFromOpponent2Ball")) { 
            DriverStation.reportError("StealFromOpponent Auton", false);
            try {
                autonCommand = new SequentialCommandGroup(
                    //new RunCommand(() -> intakeArticulator.setIntakeOut(), intakeArticulator),
                    //new ParallelCommandGroup(
                        new MoveOnTrajectory(new MercMotionProfile("StealFromOpponentTrench", ProfileDirection.BACKWARD), driveTrain)
                    //    new RunIntake(intake)
                    //), 
                    //new RunCommand(() -> intakeArticulator.setIntakeIn(), intakeArticulator),
                    //Fully auto-aim bot
                    //shoot
                );
            } catch (FileNotFoundException e) {
                System.out.println(e);
            }  
        } 
    }

    public Command getAutonCommand(){
        return this.autonCommand;
    }

    public DriveTrain getDriveTrain() {
        return driveTrain;
    }
    public Shooter getShooter() {
        return shooter;
    }
    public Intake getIntake() {
        return intake;
    }
    public IntakeArticulator getIntakeArticulator() {
        return intakeArticulator;
    }
    public Feeder getFeeder() {
        return feeder;
    }
    public Hopper getHopper() {
        return hopper;
    }
    public Spinner getSpinner() {
        return spinner;
    }
    public Elevator getElevator() {
        return elevator;
    }
    public LimelightCamera getLimelightCamera() {
        return limelightCamera;
    }
}
