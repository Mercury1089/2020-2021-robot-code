package frc.robot.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import edu.wpi.first.wpilibj2.command.Subsystem;


// used as the return type for new requires method
public class Requirements{

    public static Set<Subsystem> requires(Subsystem[] requirements){
        return new HashSet<>(Arrays.stream(requirements).collect(Collectors.toSet()));
    }
}