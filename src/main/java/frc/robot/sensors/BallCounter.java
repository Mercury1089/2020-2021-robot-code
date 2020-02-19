package frc.robot.sensors;

import edu.wpi.first.wpilibj.SPI;
import frc.robot.sensors.pixy.Pixy;
import frc.robot.sensors.pixy.SPIPixyLink;

public class BallCounter {

    private static final int NUM_SIGNATURES = 5;
    private static final int MAX_BLOCKS = 50;
    private Pixy pixyCam;

    public BallCounter() {
        pixyCam = new Pixy(new SPIPixyLink(SPI.Port.kOnboardCS0), NUM_SIGNATURES, MAX_BLOCKS);
    }

    public int balls() {
        int ballCount = 0;
        for (int i = 1; i <= NUM_SIGNATURES; i++) {
            if (pixyCam.getBoxes(i).size() == 0) {
                ballCount++;
            }
        }
        return ballCount;
    }

}