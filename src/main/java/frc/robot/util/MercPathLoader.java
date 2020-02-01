/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;

import com.ctre.phoenix.motion.TrajectoryPoint;
/**
 * Add your docs here.
 */
public class MercPathLoader {
    private static final String BASE_PATH_LOCATION = "trajectories\\PathWeaver\\output";

    /**
     * @param pathName name + wpilib.json
     */
    public static List<TrajectoryPoint> loadPath(String pathName) {
        Trajectory trajectory = null;
        List<TrajectoryPoint> trajectoryPoints = null;
        List<Trajectory.State> trajectoryStates;

        try {
            Path trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve(BASE_PATH_LOCATION + pathName);
            trajectory = TrajectoryUtil.fromPathweaverJson(trajectoryPath);
        } catch (IOException ex) {
            DriverStation.reportError("Unable to open trajectory: " + BASE_PATH_LOCATION + pathName, ex.getStackTrace());
        }
        if (trajectory != null) {
            trajectoryStates = trajectory.getStates();
            for(Trajectory.State state : trajectoryStates) {
                TrajectoryPoint point = new TrajectoryPoint();
                /*
                point.timeDur = MercMath.secondsToMilliseconds(state.timeSeconds);
                            // NOTE: Encoder ticks are backwards, we need to work with that.
                double currentPosL = Double.parseDouble(profileLeft.get("position")) * dir;
                double currentPosR = Double.parseDouble(profileRight.get("position")) * dir;

                double velocityL = Double.parseDouble(profileLeft.get("velocity")) * dir;
                double velocityR = Double.parseDouble(profileRight.get("velocity")) * dir;

                // For each point, fill our structure and pass it to API
                trajPointL.position = MercMath.feetToEncoderTicks(currentPosL); //Convert Revolutions to Units
                trajPointR.position = MercMath.feetToEncoderTicks(currentPosR); //Convert Revolutions to Units
                trajPointL.velocity = MercMath.revsPerMinuteToTicksPerTenth(velocityL); //Convert RPM to Units/100ms
                trajPointR.velocity = MercMath.revsPerMinuteToTicksPerTenth(velocityR); //Convert RPM to Units/100ms

                trajPointL.profileSlotSelect0 = trajPointR.profileSlotSelect0 = DriveTrain.DRIVE_MOTION_PROFILE_SLOT;

                // Sets the duration of each trajectory point to 20ms
                trajPointL.timeDur = trajPointR.timeDur = 20;

                // Set these to true on the first point
                trajPointL.zeroPos = trajPointR.zeroPos = i == 0;

                // Set these to true on the last point
                trajPointL.isLastPoint = trajPointR.isLastPoint = i == TRAJECTORY_SIZE - 1;
                */
            }
        }
        return trajectoryPoints;
    }
}
