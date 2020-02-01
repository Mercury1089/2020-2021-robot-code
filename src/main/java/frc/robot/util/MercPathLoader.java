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
import edu.wpi.first.wpilibj.geometry.Pose2d;
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
            Trajectory.State prevState = null;
            for(Trajectory.State state : trajectoryStates) {
                TrajectoryPoint point = new TrajectoryPoint();
                double heading, velocity, pos;
                int time;

                //Time
                time = MercMath.secondsToMilliseconds(state.timeSeconds);
                point.timeDur = time;
                //Velocity
                velocity = state.velocityMetersPerSecond;
                point.velocity = velocity;
                //Distance
                if (prevState == null) {
                    point.position = 0.0;
                } else {
                    pos =  MercMath.pose2dToDistance(state.poseMeters, prevState.poseMeters);
                    point.position = pos;
                }
                prevState = state;
                //Heading
                heading = MercMath.radiansToDegrees(state.curvatureRadPerMeter);
                point.headingDeg = heading;

                //Append point to point
                trajectoryPoints.add(point);
            }
        }
        return trajectoryPoints;
    }
}
