package gr.aegean.palaemon.conductor.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SRAPUtils {



    //TODO confirm this with NTUA
    public static List<String> zoneId2Geofence(String zoneId){
        List<String> zone4 =Arrays.asList("7DG1","7DG2","7DG3","7DG4");
        List<String> zone3 =Arrays.asList("7CG1","7CG2");
        List<String> zone2 =Arrays.asList("7BG1","7BG2","7BG3","7BG4","7BG5","7BG6","8BG1","8BG2","8BG3","8BG4",
                "8BG5","8BG6","8BG7","8BG8","8BG9","8BG10","S8-7.1","S8-7.2","C8.1","C8.2","C8.3","C8.4","C8.5");
        List<String> zone1 =Arrays.asList("S7-6.1","S7-6.1","S7-6.3");

        switch (zoneId){
            case "zone2": return zone2;
            case "zone1": return zone1;
            case "zone3": return zone3;
            case  "zone4" : return zone4;
            default: return null;
        }
    }
}
