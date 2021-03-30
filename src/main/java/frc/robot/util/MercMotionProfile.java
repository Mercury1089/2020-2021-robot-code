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

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Add your docs here.
 */
public class MercMotionProfile {
    private final String name;
    private final String pathDirectory;
    private final ProfileDirection direction;
    private final List<TrajectoryPoint> trajectoryPoints;

    public MercMotionProfile(final String name, final ProfileDirection direction) {
        this.name = name;
        this.direction = direction;
        pathDirectory = MercPathLoader.getBasePathLocation() + name + ".wpilib.json"; 
        trajectoryPoints = MercPathLoader.loadPath(name);
        if(this.direction == ProfileDirection.BACKWARD)
            driveBackwards();
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

    public void driveBackwards() {
        if(trajectoryPoints == null) {
            DriverStation.reportError("No Trajectory To Load", false);
            return;
        }
        for (int i = 0; i < trajectoryPoints.size(); i++) {
            trajectoryPoints.get(i).velocity *= -1;
            trajectoryPoints.get(i).position *= -1;
            trajectoryPoints.get(i).auxiliaryPos *= -1;
        }
    }

    public enum ProfileDirection{
        FORWARD,
        BACKWARD
    }
}
