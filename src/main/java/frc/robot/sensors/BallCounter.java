package frc.robot.sensors;

import frc.robot.sensors.pixy.PixySPI;

public class BallCounter {

    private PixySPI pixyCam;

    public BallCounter() {
        pixyCam = new PixySPI(0);
    }

    public int balls() {
        pixyCam.getBoxes(5);

        return 0;
    }

}