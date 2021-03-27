/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryUtil;

import frc.robot.subsystems.DriveTrain;

import com.ctre.phoenix.motion.TrajectoryPoint;
/**
 * Add your docs here.
 */
public class MercPathLoader {
    private static final String BASE_PATH_LOCATION = "/home/lvuser/deploy/trajectories/PathWeaver/output/";
    private static int minTime = 0;
    /**
     * @param pathName name + wpilib.json
     */
    public static List<TrajectoryPoint> loadPath(String pathName) {
        List<TrajectoryPoint> trajectoryPoints = new ArrayList<TrajectoryPoint>();
        List<Trajectory.State> trajectoryStates;
        Trajectory trajectory = null;

        try {
            Path trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve(BASE_PATH_LOCATION + pathName + ".wpilib.json");
            trajectory = TrajectoryUtil.fromPathweaverJson(trajectoryPath);
        } catch (IOException ex) {
            DriverStation.reportError("Unable to open trajectory: " + pathName, ex.getStackTrace());
            return null;
        }
        if (trajectory != null) {
            trajectoryStates = trajectory.getStates();
            Trajectory.State prevState = null;
            int prevTime = 0;
            double prevHeading = 0;
            double pos = 0.0;
            boolean add360 = false;
            boolean minus360 = false;
            for(Trajectory.State state : trajectoryStates) {
                TrajectoryPoint point = new TrajectoryPoint();
                double heading, velocity;
                int time;

                //Time
                time = MercMath.secondsToMilliseconds(state.timeSeconds);
                point.timeDur = time - prevTime;
                prevTime = time;    
                if(minTime == 0)
                    minTime = point.timeDur;
                //time = 20;
                //point.timeDur = time;
                //Velocity
                velocity = state.velocityMetersPerSecond;
                point.velocity = MercMath.metersPerSecondToTicksPerTenth(velocity);
                //Distance
                if (prevState == null) {
                    point.position = 0.0;
                    point.zeroPos = true;
                } else {
                    double prevX, prevY, x, y;
                    prevX = prevState.poseMeters.getTranslation().getX();
                    prevY = prevState.poseMeters.getTranslation().getY();
                    x = state.poseMeters.getTranslation().getX();
                    y = state.poseMeters.getTranslation().getY();
                    pos += MercMath.distanceFormula(prevX, x, prevY, y);
                    state.poseMeters.getTranslation().getDistance(prevState.poseMeters.getTranslation());
                    point.position = MercMath.metersToEncoderTicks(pos);
                    point.zeroPos = false;
                }
                prevState = state;

                //Heading
                heading = state.poseMeters.getRotation().getDegrees();
                if(heading > -180 && heading < 0 && prevHeading <= 180 && prevHeading >= 170) {
                    add360 = true;
                    minus360 = false;
                }
                else if(heading < 180 && heading > 0 && (prevHeading > -180 && prevHeading < -170) || prevHeading == 180) {
                    minus360 = true;
                    add360 = false;
                }
                
                if(add360 && !minus360)
                    heading += 360;   
                else if(!add360 && minus360)
                    heading -= 360;
                prevHeading = heading;
                point.auxiliaryPos = MercMath.degreesToPigeonUnits(heading); // heading stored as auxilliaryPos
                //PID Profile
                point.profileSlotSelect0 = DriveTrain.DRIVE_MOTION_PROFILE_SLOT;
                point.profileSlotSelect1 = DriveTrain.DRIVE_SMOOTH_TURN_SLOT;
                point.useAuxPID = true;
                //Says that point is not a last point
                point.isLastPoint = false;
                //Append point to point
                trajectoryPoints.add(point);
                /*
                System.out.println("time: " + time + 
                                   " velocity: " + MercMath.inchesPerSecondToRevsPerMinute(state.velocityMetersPerSecond) + 
                                   " heading: " + heading +
                                   " pos: " + pos +
                                   " TicksPerTenth Values " + MercMath.revsPerMinuteToTicksPerTenth(MercMath.inchesPerSecondToRevsPerMinute(state.velocityMetersPerSecond)) +
                                   " point.time: " + point.timeDur +
                                   " point.velocity: " + point.velocity + 
                                   " point.auxiliaryPos: " + point.auxiliaryPos +
                                   " point.position: " + point.position
                );*/
                
                minTime = Math.min(point.timeDur, minTime);
            }
            TrajectoryPoint point = new TrajectoryPoint(), lastPoint;
            lastPoint = trajectoryPoints.get(trajectoryPoints.size() - 1);

            point.timeDur = lastPoint.timeDur + 100;
            point.velocity = lastPoint.velocity;
            point.position = lastPoint.position;
            point.zeroPos = false;
            point.auxiliaryPos = lastPoint.auxiliaryPos;
            point.profileSlotSelect0 = DriveTrain.DRIVE_MOTION_PROFILE_SLOT;
            point.profileSlotSelect1 = DriveTrain.DRIVE_SMOOTH_TURN_SLOT;
            point.useAuxPID = true;
            point.isLastPoint = true;

            trajectoryPoints.add(point);

            DriverStation.reportError(pathName + "\nMin Time: " + minTime , false);
        }
        return trajectoryPoints;
    }

    public static int getMinTime() {
        return minTime;
    }

    public static String getBasePathLocation() {
        return BASE_PATH_LOCATION;
    }
}
