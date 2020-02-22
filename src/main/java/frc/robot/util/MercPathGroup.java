package frc.robot.util;

import java.util.Scanner;
import java.io.File;
import java.io.IOException;

import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.util.MercMotionProfile.ProfileDirection;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.ArrayList;


public class MercPathGroup{

    private List<MercMotionProfile> profiles;
    
    public MercPathGroup(String name){
        try{
            Scanner groupReader = new Scanner(new File("/home/lvuser/deploy/trajectories/PathWeaver/Groups/" + name));
            List<MercMotionProfile> profiles = new ArrayList<MercMotionProfile>();
            List<String> pathNames = new ArrayList<String>();
            String path;

            while (true){
                try{
                    path = groupReader.nextLine();
                    path = path.substring(0, path.indexOf(".path"));
                } catch (NoSuchElementException ex){
                    break;
                }
                pathNames.add(path);
            }

            for (String p : pathNames){
                profiles.add((p.charAt(0) == 'B') ? new MercMotionProfile(p, ProfileDirection.BACKWARDS) : new MercMotionProfile(p, ProfileDirection.FORWARD));
            }
            
        } catch (Exception ex){
            DriverStation.reportError("Problem Loading Path Group", ex.getStackTrace());
        }

    }

    public List<MercMotionProfile> getProfiles(){
        return profiles;
    }
}