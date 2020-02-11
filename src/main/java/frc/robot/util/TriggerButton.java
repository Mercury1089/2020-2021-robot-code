package frc.robot.util;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;

public class TriggerButton {

  private Joystick gamepad;
  private int trigger;
  private boolean held;
  private final double PRESS_THRESHOLD;

  public TriggerButton(Joystick gamepad, int trigger) {
    this(gamepad, trigger, 0.1);
  }

  public TriggerButton(Joystick gamepad, int trigger, double press_threshold) {
    this.gamepad = gamepad;
    this.trigger = trigger;
    PRESS_THRESHOLD = press_threshold;
  }
  
  public boolean isPressed(){
    if(gamepad.getRawAxis(trigger) > 0.1)
      return true;
    return false;
  }

  public void whenPressed(Command command) {
    if(isPressed() && !held) {
      command.schedule();
      held = true;
    }
    else if(!isPressed())
      held = false;
  }

  public void whenHeld(Command command) {
    if(isPressed() && !held) {
      command.schedule();
      held = true;
    }
    else if(!isPressed()) {
      command.cancel();
      held = false;
    }
  }

  public void whileHeld(Command command) {
    if(isPressed())
      command.schedule();
  }
    
  public void toggleWhenPressed(Command command) {
    if(isPressed())
      if(command.isScheduled())
        command.cancel();
      else
        command.schedule();
  }
}