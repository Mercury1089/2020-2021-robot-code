package frc.robot;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.RobotMap.DS_USB;
import frc.robot.RobotMap.JOYSTICK_BUTTONS;
import frc.robot.RobotMap.NIHAR;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Feeder;
import frc.robot.subsystems.Hopper;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.IntakeArticulator;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Elevator.ElevatorPosition;
import frc.robot.subsystems.IntakeArticulator.IntakePosition;
import frc.robot.subsystems.Shooter.ShooterMode;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */

public class RobotContainer {
    public static final double DEADZONE = 0.08;

    private CommandJoystick rightJoystick, leftJoystick;
    private CommandXboxController gamepad;

    private Trigger left1, left2, left3, left4, left5, left6, left7, left8, left9, left10, left11;
    private Trigger right1, right2, right3, right4, right5, right6, right7, right8, right9, right10, right11;
    private Trigger gamepadA, gamepadB, gamepadX, gamepadY, gamepadRB, gamepadLB, gamepadL3, gamepadBack, gamepadStart, gamepadLeftStickButton, gamepadRightStickButton;
    private Trigger gamepadLT, gamepadRT;
    private Trigger gamepadPOVDown, gamepadPOVUpLeft, gamepadPOVUp, gamepadPOVUpRight, gamepadPOVLeft, gamepadPOVRight, gamepadPOVDownRight, gamepadPOVDownLeft;
    private Supplier<Double> gamepadLeftX, gamepadLeftY, gamepadRightX, gamepadRightY, rightJoystickX, rightJoystickY, leftJoystickX, leftJoystickY;

    private DriveTrain driveTrain;
    private Shooter shooter;
    private Intake intake;
    private IntakeArticulator intakeArticulator;
    private Feeder feeder;
    private Hopper hopper;
    private Elevator elevator;

    public RobotContainer() {
        leftJoystick = new CommandJoystick(DS_USB.LEFT_STICK);
        rightJoystick = new CommandJoystick(DS_USB.RIGHT_STICK);
        gamepad = new CommandXboxController(DS_USB.GAMEPAD);


        driveTrain = new DriveTrain();
        driveTrain.setDefaultCommand(new RunCommand(() -> driveTrain.arcadeDrive(leftJoystickY, rightJoystickX, true), driveTrain));

        shooter = new Shooter(ShooterMode.ONE_WHEEL);
        shooter.setDefaultCommand(new RunCommand(() -> shooter.stopShooter(), shooter));
        
        hopper = new Hopper();       
        hopper.setDefaultCommand(new RunCommand(() -> hopper.stopHopperAgitator(), hopper));

        intake = new Intake();
        intake.setDefaultCommand(new RunCommand(() -> intake.stopIntakeRoller(), intake));
        
        intakeArticulator = new IntakeArticulator();
        intakeArticulator.setDefaultCommand(new RunCommand(() -> intakeArticulator.setIntakePosition(IntakePosition.IN), intakeArticulator));

        feeder = new Feeder();
        feeder.setDefaultCommand(new RunCommand(() -> feeder.stopFeeder(), feeder));

        elevator = new Elevator();

        initializeJoystickButtons();
       
        left1.whileTrue(
            new ParallelCommandGroup(
                new RunCommand(() -> intakeArticulator.setIntakePosition(IntakePosition.OUT), intakeArticulator),
                new RunCommand(() -> intake.runIntakeRoller(), intake),
                new RunCommand(() -> hopper.runHopperAgitator(), hopper)
            )
        );

        right1.whileTrue(
            new SequentialCommandGroup(
                new RunCommand(() -> shooter.setVelocity(Shooter.STEADY_RPM), shooter).until(() -> shooter.atTargetRpm()),
                new ParallelCommandGroup(
                    new RunCommand(() -> shooter.setVelocity(Shooter.STEADY_RPM), shooter),
                    new RunCommand(() -> feeder.runFeeder(), feeder),
                    new RunCommand(() -> hopper.runHopperAgitator(), hopper)
                )
            )
        );

        left2.onTrue(
            new RunCommand(() -> elevator.setPosition(ElevatorPosition.TOP))
        );

        right2.onTrue(
            new RunCommand(() -> elevator.setPosition(ElevatorPosition.BOTTOM))
        );
    }

    public double getJoystickX(int port) {
        switch (port) {
            case DS_USB.LEFT_STICK:
                return leftJoystick.getX() * NIHAR.LEFT_X;
            case DS_USB.RIGHT_STICK:
                return rightJoystick.getX() * NIHAR.RIGHT_X;
            default:
                return 0;
        }
    }

    public double getJoystickY(int port) {
        switch (port) {
            case DS_USB.LEFT_STICK:
                return leftJoystick.getY() * NIHAR.LEFT_Y;
            case DS_USB.RIGHT_STICK:
                return rightJoystick.getY() * NIHAR.RIGHT_Y;
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
        // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    
            left1 = leftJoystick.button(JOYSTICK_BUTTONS.BTN1);
            left2 = leftJoystick.button(JOYSTICK_BUTTONS.BTN2);
            left3 = leftJoystick.button(JOYSTICK_BUTTONS.BTN3);
            left4 = leftJoystick.button(JOYSTICK_BUTTONS.BTN4);
            left5 = leftJoystick.button(JOYSTICK_BUTTONS.BTN5);
            left6 = leftJoystick.button(JOYSTICK_BUTTONS.BTN6);
            left7 = leftJoystick.button(JOYSTICK_BUTTONS.BTN7);
            left8 = leftJoystick.button(JOYSTICK_BUTTONS.BTN8);
            left9 = leftJoystick.button(JOYSTICK_BUTTONS.BTN9);
            left10 = leftJoystick.button(JOYSTICK_BUTTONS.BTN10);
            left11 = leftJoystick.button(JOYSTICK_BUTTONS.BTN11);
    
            right1 = rightJoystick.button(JOYSTICK_BUTTONS.BTN1);
            right2 = rightJoystick.button(JOYSTICK_BUTTONS.BTN2);
            right3 = rightJoystick.button(JOYSTICK_BUTTONS.BTN3);
            right4 = rightJoystick.button(JOYSTICK_BUTTONS.BTN4);
            right5 = rightJoystick.button(JOYSTICK_BUTTONS.BTN5);
            right6 = rightJoystick.button(JOYSTICK_BUTTONS.BTN6);
            right7 = rightJoystick.button(JOYSTICK_BUTTONS.BTN7);
            right8 = rightJoystick.button(JOYSTICK_BUTTONS.BTN8);
            right9 = rightJoystick.button(JOYSTICK_BUTTONS.BTN9);
            right10 = rightJoystick.button(JOYSTICK_BUTTONS.BTN10);
            right11 = rightJoystick.button(JOYSTICK_BUTTONS.BTN11);
    
            gamepadA = gamepad.a();
            gamepadB = gamepad.b();
            gamepadX = gamepad.x();
            gamepadY = gamepad.y();
            gamepadRB = gamepad.rightBumper();
            gamepadLB = gamepad.leftBumper();
            gamepadBack = gamepad.back();
            gamepadStart = gamepad.start();
            gamepadLeftStickButton = gamepad.leftStick();
            gamepadRightStickButton = gamepad.rightStick();
            gamepadLT = gamepad.leftTrigger();
            gamepadRT = gamepad.rightTrigger();
            
            gamepadPOVDown = gamepad.povDown();
            gamepadPOVUpLeft = gamepad.povUpLeft();
            gamepadPOVUp = gamepad.povUp();
            gamepadPOVUpRight = gamepad.povUpRight();
            gamepadPOVLeft = gamepad.povLeft();
            gamepadPOVRight = gamepad.povRight();
            gamepadPOVDownRight = gamepad.povDownRight();
            gamepadPOVDownLeft = gamepad.povDownLeft();
    
            gamepadLeftX = () -> gamepad.getLeftX();
            gamepadRightX = () -> gamepad.getRightX();
            gamepadLeftY = () -> -gamepad.getLeftY();
            gamepadRightY = () -> -gamepad.getRightY();
    
            leftJoystickX = () -> leftJoystick.getX();
            leftJoystickY = () -> leftJoystick.getY();
            rightJoystickX = () -> rightJoystick.getX();
            rightJoystickY = () -> rightJoystick.getY();
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
    public Elevator getElevator() {
        return elevator;
    }

}
