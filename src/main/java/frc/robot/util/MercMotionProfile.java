/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.util;

import java.util.ArrayList;
import java.util.List;

import com.ctre.phoenix.motion.TrajectoryPoint;

/**
 * Add your docs here.
 */
public class MercMotionProfile {
    private final String name;
    private final String pathDirectory;
    List<TrajectoryPoint> trajectoryPoints;

    public MercMotionProfile(String name) {
        this.name = name;
        pathDirectory = MercPathLoader.getBasePathLocation() + name + ".wpilib.json"; 
        trajectoryPoints = MercPathLoader.loadPath(name);
    }

    public String getName() {
        return name;
    }

    public String getPathDirectory() {
        return pathDirectory;
    }

    public List<TrajectoryPoint> getTrajectoryPoints() {
        return trajectoryPoints;
    }

    public List<TrajectoryPoint> getPathForward() {
        return trajectoryPoints;
    }

    public List<TrajectoryPoint> getPathBackwards() {
        List<TrajectoryPoint> points = new ArrayList<TrajectoryPoint>();
        for (int i = trajectoryPoints.size() - 1; i >= 0; i--) {
            TrajectoryPoint p = trajectoryPoints.get(i);
            p.velocity *= -1;
           // p.headingDeg = 90 - p.headingDeg;
           points.add(p);
        }
        return points;
    }
}
