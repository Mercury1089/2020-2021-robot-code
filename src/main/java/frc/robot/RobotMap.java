package frc.robot;

public class RobotMap {

    public static final int CTRE_TIMEOUT = 10;

    public static class CAN {
        public static final int
                DRIVETRAIN_ML = 1,
                DRIVETRAIN_MR = 2,
                DRIVETRAIN_FL = 3,
                DRIVETRAIN_FR = 4,
                FEEDER = 5,
                HOPPER_BELT = 6,
                INTAKE_ROLLER = 13,
                INTAKE_ARTICULATOR = 7,
                SHOOTER_LEFT = 8,
                SHOOTER_RIGHT = 9,
                PIGEON = 10,
                ELEVATOR = 11,
                SPINNER = 12,
                AGITATOR = 14,
                CANCODER_ML = 1,
                CANCODER_MR = 2;

        private CAN() {
        }
    }

    /**
     * CAN Status frame frequencies. For details, see:
     *   https://docs.ctre-phoenix.com/en/stable/ch18_CommonAPI.html#setting-status-frame-periods
     *   https://docs.revrobotics.com/sparkmax/operating-modes/control-interfaces#periodic-status-frames
     */
    public static class CAN_STATUS_FREQ {
        public static final int
            XTRA_HIGH = 5, // 5ms Very high - use sparingly
            HIGH = 10, // 10ms - High for important sensors
            NORMAL = 20, // 20ms - Match robot loop freq
            LOW = 100, // 100ms - Less frequent
            XTRA_LOW = 255; // 255ms - Maximum status frame period
        private CAN_STATUS_FREQ() {
        }
    }
    public static class UNITS {
        public static final int
                MAG_ENCODER_TICKS_PER_REVOLUTION = 4096,
                PIGEON_NATIVE_UNITS_PER_ROTATION = 8192;

        private UNITS() {
        }
    }
    public static class NIHAR {
        public static final double
            LEFT_X = 1.0,
            LEFT_Y = 1.0,
            RIGHT_X = 0.8,
            RIGHT_Y = 1.0;
        
        private NIHAR() {}
    }

    public static class PCM {

        private PCM() {

        }
    }

    public static class AIO {
        public static final int LEFT_ULTRASONIC = 0;
        public static final int RIGHT_ULTRASONIC = 1;

        private AIO() {
        }
    }

    public static class PID {
        public static final int PRIMARY_PID_LOOP = 0;

        private PID() {
        }
    }

    public static class CANIFIER_PWM {

        private CANIFIER_PWM() {
        }
    }

    public static class PWM {
        public static final int LIMELIGHT_SERVO = 1;

        private PWM() {
        }
    }

    public static class RELAY {
        public static final int ELEVATOR_LOCK = 0;

        private RELAY() {
        }
    }

    public static class DIGITAL_INPUT {
        public static final int ELEVATOR_LIMIT_SWITCH = 0;

        private DIGITAL_INPUT() {
        }
    }

    public static class DS_USB {
        public static final int LEFT_STICK = 0, RIGHT_STICK = 1, GAMEPAD = 2;

        private DS_USB() {
        }
    }

    public static class GAMEPAD_AXIS {
        public static final int leftX = 0, leftY = 1, leftTrigger = 2, rightTrigger = 3, rightX = 4, rightY = 5;

        private GAMEPAD_AXIS() {
        }
    }

    public static class GAMEPAD_BUTTONS {
        public static final int A = 1;
        public static final int B = 2;
        public static final int X = 3;
        public static final int Y = 4;
        public static final int LB = 5;
        public static final int RB = 6;
        public static final int BACK = 7;
        public static final int START = 8;
        public static final int L3 = 9;
        public static final int R3 = 10;

        private GAMEPAD_BUTTONS() {
        }
    }
    
    /**
    *From 0 in the up direction, and counter clockwise in degrees (right is 90 and upper left is 315).
    */
    public static class GAMEPAD_POV {
        public static final int MIDDLE = -1;
        public static final int UP = 0;
        public static final int UP_RIGHT = 45;
        public static final int RIGHT = 90;
        public static final int DOWN_RIGHT = 135;
        public static final int DOWN = 180;
        public static final int DOWN_LEFT = 225;
        public static final int LEFT = 270;
        public static final int UP_LEFT = 315;
    }

    public static class JOYSTICK_BUTTONS {
        public static final int BTN1 = 1;
        public static final int BTN2 = 2;
        public static final int BTN3 = 3;
        public static final int BTN4 = 4;
        public static final int BTN5 = 5;
        public static final int BTN6 = 6;
        public static final int BTN7 = 7;
        public static final int BTN8 = 8;
        public static final int BTN9 = 9;
        public static final int BTN10 = 10;
        public static final int BTN11 = 11;

        public JOYSTICK_BUTTONS() {
        }
    }
}
