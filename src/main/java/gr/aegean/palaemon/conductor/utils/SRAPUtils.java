package gr.aegean.palaemon.conductor.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SRAPUtils {

    private static final List<String> zone4 = Arrays.asList("7DG1", "7DG2", "7DG3", "7DG4", "S7-6.3");
    private static final List<String> zone3 = Arrays.asList("7CG1", "7CG2");
    private static final List<String> zone2 = Arrays.asList("7BG1", "7BG2", "7BG3", "7BG4", "7BG5", "7BG6", "S8-7.1", "S8-7.2", "C8.1", "C8.2", "C8.3", "C8.4", "C8.5", "S8-7.2", "C8.1", "C8.5");
    private static final List<String> zone1 = Arrays.asList("S7-6.1", "S7-6.3");
    private static final List<String> zone5 = Arrays.asList("8BG1", "8BG2", "8BG3", "8BG4",
            "8BG5", "8BG6", "8BG7", "8BG8", "8BG9", "8BG10");
    private static final List<String> zone6 = List.of("S8-7.1");
    private static final List<String> zone9 = Arrays.asList("9BG1", "9BG2", "S9-8.1", "C9.1");

    public static List<String> zoneId2Geofence(String zoneId) {
        switch (zoneId) {
            case "MSD":
                return zone2;
            case "MSC":
                return zone1;
            case "MSB":
                return zone3;
            case "MSA":
                return zone4;
            case "Z2D8":
                return zone5;
            case "Z1D8":
                return zone6;
            case "Z2D9":
                return zone9;
            default:
                return null;
        }
    }

    public static String getSrapZoneFromGeofence(String geofence){
        if(zone1.contains(geofence)) return "MSD";
        if(zone2.contains(geofence)) return "MSC";
        if(zone3.contains(geofence)) return "MSB";
        if(zone4.contains(geofence)) return "MSA";
        if(zone5.contains(geofence)) return "Z2D8";
        if(zone6.contains(geofence)) return "Z1D8";
        if(zone9.contains(geofence)) return "Z2D9";
        return null;
    }


}
