package frc.robot.util;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class TriggerButton {

  private Joystick gamepad;
  private int trigger;
  private boolean held = false;
  private final double PRESS_THRESHOLD;

  public TriggerButton(Joystick gamepad, int trigger) {
    this(gamepad, trigger, 0.1);
  }

  public TriggerButton(Joystick gamepad, int trigger, double press_threshold) {
    this.gamepad = gamepad;
    this.trigger = trigger;
    PRESS_THRESHOLD = press_threshold;
  }
  
  /**
   * @return whether or not the trigger is pressed past the set threshold
   */
  public boolean isPressed(){
    if(gamepad.getRawAxis(trigger) > PRESS_THRESHOLD)
      return true;
    return false;
  }

  /**
   * Starts the command when the trigger is pressed, won't run again until the button is released and pressed again.
   * @param command the command to run
   */
  public void whenPressed(Command command) {
    whenPressed(command, true);
  }

  /**
   * Starts the command when the trigger is pressed, won't run again until the button is released and pressed again.
   * @param command the command to run
   * @param interruptible whether or not the command is interruptible
   */
  public void whenPressed(Command command, boolean interruptible) {
    CommandScheduler.getInstance().addButton(new Runnable() {
  
      @Override
      public void run() {
        boolean pressed = isPressed();

        if(pressed && !command.isScheduled()) {
          command.schedule(interruptible);
        }
      }
    });
  }

  /**
   * Starts the command when the trigger is pressed, ends it when it is released.
   * @param command the command to run
   * @param interruptible whether or not the command is interruptible
   */
  public void whenHeld(Command command, boolean interruptible) {
    CommandScheduler.getInstance().addButton(new Runnable() {
  
      @Override
      public void run() {
        boolean pressed = isPressed();

        if (pressed && !held) {
          command.schedule(interruptible);
          held = true;
        } else if (!pressed) {
          command.cancel();
          held = false;
        }
      }
    });
  }

  /**
   * Starts the command when the trigger is pressed, ends it when it is released.
   * @param command the command to run
   */
  public void whenHeld(Command command) {
    whenHeld(command, true);
  }

  /**
   * Runs the command repeatedly while the trigger is held.
   * @param command the command to run
   * @param interruptible whether or not the command is interruptible
   */
  public void whileHeld(Command command, boolean interruptible) {
    CommandScheduler.getInstance().addButton(new Runnable() {
  
      @Override
      public void run() {
        boolean pressed = isPressed();

        if (pressed) {
          command.schedule(interruptible);
        } 
        else if (!pressed) {
          command.cancel();
        }
      }
    });
  }

  /**
   * Runs the command repeatedly while the trigger is held.
   * @param command the command to run
   */
  public void whileHeld(Command command) {
    whileHeld(command, true);
  }
    
  /**
   * Starts the command when the trigger is pressed pressed, stops the command when it is pressed again.
   * @param command the command to run
   */
  public void toggleWhenPressed(Command command) {
    toggleWhenPressed(command, true);
  }

  /**
   * Starts the command when the trigger is pressed pressed, stops the command when it is pressed again.
   * @param command the command to run
   * @param interruptible whether or not the command is interruptible
   */
  public void toggleWhenPressed(Command command, boolean interruptible) {
    CommandScheduler.getInstance().addButton(new Runnable() {
  
      @Override
      public void run() {
        boolean pressed = isPressed();

        if (pressed && !held) {
          System.out.println("Pressed to toggle");
          if(command.isScheduled())
            command.cancel();
          else
            command.schedule(interruptible);
          held = true;
        }
        else if(!pressed)
          held = false;
      }
    });
  }
}