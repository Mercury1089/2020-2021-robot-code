package frc.robot.sensors.pixy;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.util.BoundingBox;
import java.util.ArrayList;
import java.util.Hashtable;


/**
 * PixyCam implementation using SPI interface
 *
 * deprecated since we cannot use the SPI interface on the RIO.
 */
public class Pixy {

    public enum LinkType {
        SPI, I2C;
    }

    private IPixyLink pixyLink;
    private int maxBlocks;
    private int numSignatures;
    private final Notifier pixyUpdateNotifier;

    // Variables used for SPI comms, derived from https://github.com/omwah/pixy_rpi
    private static final byte PIXY_SYNC_BYTE = 0x5a;
    private static final byte PIXY_SYNC_BYTE_DATA = 0x5b;
    private static final int PIXY_OUTBUF_SIZE = 6;
    private static final int PIXY_START_WORD = 0xaa55;
    private static final int PIXY_START_WORDX = 0x55aa;
    private static final int BLOCK_LEN = 5;
    private static final double PIXY_UPDATE_PERIOD_SECONDS = 0.050; // Update every 50ms.

    public final Hashtable<Integer, ArrayList<BoundingBox>> signatures;
  
    private boolean skipStart = false;
    private int debug = 0; // 0 - none, 1 - SmartDashboard, 2 - log to console/file

    private long getStart = 0;
  
    public Pixy(LinkType linkType, int numSignatures, int maxBlocks) {
        this(linkType == LinkType.I2C ? new I2CPixyLink() :
             linkType == LinkType.SPI ? new SPIPixyLink() :
             new SPIPixyLink(), // Default to SPI (even though there are only two options currently)
             numSignatures, maxBlocks);
    }
    /**
     * Instantiate new Pixy
     * @param pixyLink The IPixyLink (SPI or I2C) of the pixy
     * @param numSignatures The number of signature slots in use on the Pixy
     * @param maxBlocks Maximum number of blocks (configured in Pixy Blocks tab)
     */
    public Pixy(IPixyLink pixyLink, int numSignatures, int maxBlocks) {

        this.pixyLink = pixyLink;
        this.numSignatures = numSignatures;
        this.maxBlocks = maxBlocks;

        signatures = new Hashtable<Integer, ArrayList<BoundingBox>>();
        for(int i = 1; i <= maxBlocks; i++) {
            signatures.put(i, new ArrayList<BoundingBox>());
        }

        pixyUpdateNotifier = new Notifier(this::getBlocks);
        pixyUpdateNotifier.startPeriodic(PIXY_UPDATE_PERIOD_SECONDS);
    }

    public ArrayList<BoundingBox> getBoxes(int signum) {
        if (signum > 0 && signum <= numSignatures) {
            return signatures.get(signum);
        } else {
            return null;
        }
    }
    /**
     * Reads from SPI for data "words," and parses
     * all words into bounding boxes.
     */
    private void getBlocks() {
        // Clear out BOXES array list for reuse.
        signatures.clear();
        long count = 0;
        boolean loading = true;

        // If we haven't found the start of a block, find it.
        if (!skipStart) {
            // If we can't find the start of a block, drop out.
            if (!getStart())
                loading = false;
        } else {
            // Clear flag that tells us to find the next block as the logic below will loop
            // the appropriate number of times to retrieve a complete block.
            skipStart = false;
        }

        // Loop until we hit the maximum number of blocks.
        while (loading && count < maxBlocks) {
            if (count == 0) {
                // Beginning of loading - clear the previous signatures.
                for(int i = 1; i <= maxBlocks; i++) {
                    signatures.get(i).clear();
                }
            }

            // Since this is our first time in, bytes 2 and 3 are the checksum, grab them and store for future use.
            // NOTE: getWord grabs the entire 16 bits in one shot.
            int checksum = pixyLink.getWord();
            int trialsum = 0;

            // See if the checksum is really the beginning of the next block,
            // in which case return the current set of BOXES found and set the flag
            // to skip looking for the beginning of the next block since we already found it.
            if (checksum == PIXY_START_WORD) {
                skipStart = true;
                loading = false;
            }

            // See if we received a empty buffer, if so, assume end of comms for now and return what we have.
            else if (checksum == 0) {
                loading = false;
            }

            if (loading) {
                // Start constructing BOXES
                // Only need 5 slots since the first 3 slots, the double start BOXES and checksum, have been retrieved already.
                int[] box = new int[5];
                for (int i = 0; i < BLOCK_LEN; i++) {
                    box[i] = pixyLink.getWord();
                    trialsum += box[i];
                }

                // See if we received the data correctly.
                // Also make sure the target is for the first signature
                int signum = box[0];
                if (checksum == trialsum && signum <= numSignatures) {

                    BoundingBox bound = new BoundingBox(
                        box[1], // X
                        box[2], // Y
                        box[3], // W
                        box[4]  // H
                    );
                    // Data has been validated, add the current block of data to the overall BOXES buffer.
                    signatures.get(signum).add(bound);
                }

                // Check the next word from the Pixy to confirm it's the start of the next block.
                // Pixy sends two aa55 words at start of block, this should pull the first one.
                // The top of the loop should pull the other one.
                int w = pixyLink.getWord();

                if (w != PIXY_START_WORD) {
                    // Sort array before returning
                    loading = false;
                }
            }
            for(int i = 1; i <= maxBlocks; i++) {
                signatures.get(i).sort(BoundingBox::compareTo);
            }
        }

        // Should never get here, but if we happen to get a massive number of BOXES
        // and exceed the limit it will happen. In that case something is wrong
        // or you have a super natural Pixy and SPI link.
        //DriverStation.reportWarning("PIXY: Massive number of boxes!", false);
    }

    private boolean getStart() {
        int lastw = 0xff;
        int count = 0;

        if (debug >= 1) {
            SmartDashboard.putNumber("getStart: count: ", getStart++);
        }

        // Loop until we get a start word from the Pixy.
        while (true) {
            int w = pixyLink.getWord();

            if ((w == 0x00) && (lastw == 0x00))
                // Could delay a bit to give time for next data block, but to get accurate time would tie up cpu.
                // So might as well return and let caller call this getStart again.
                return false;
            else if ((int) w == PIXY_START_WORD && (int) lastw == PIXY_START_WORD)
                return true;

            lastw = w;
        }
    }
}

