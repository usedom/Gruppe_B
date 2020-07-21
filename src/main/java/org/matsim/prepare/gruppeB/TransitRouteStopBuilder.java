package org.matsim.prepare.gruppeB;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.pt.transitSchedule.api.TransitRouteStop;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt.transitSchedule.api.TransitScheduleFactory;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransitRouteStopBuilder {

    Map<String,List<TransitRouteStop>> TRStops = new HashMap<>();

    Map<String,List<TransitRouteStop>> getTRStops(){
        if(!TRStops.isEmpty()){
            return TRStops;
        }
        else {
            System.out.println("ERROR! Build TransitRouteStops first!");
            return null;
        }
    }

    Map<String,List<TransitRouteStop>> build(List<Node> nodeList, Map<Node, String> nodeMap, Map<String,List<Id<Link>>> links, TransitSchedule tschedule){
        System.out.println("\tCreate/Get all TransitStops and put them into the right order...");
        int nsize = nodeList.size();
        int lsize = links.get("WD").size();
        TransitScheduleFactory tsfactory = tschedule.getFactory();
        Map<Id<TransitStopFacility>, TransitStopFacility> tsfacilities = tschedule.getFacilities();

        List<TransitRouteStop> stopsM10WD = new ArrayList<>();
        List<TransitRouteStop> stopsM10DW = new ArrayList<>();
        Map<String,List<TransitRouteStop>> trstops = new HashMap<>(2);
        trstops.put("WD", stopsM10WD);
        trstops.put("DW", stopsM10DW);

        for (int i = 0; i< nsize -1; i++){
            // Dummy! Don't try to read this out
            stopsM10DW.add(i, null);
        }

        for(int i = 0; i< nsize; i++){
            TransitStopFacility stopFacilityWD;
            TransitStopFacility stopFacilityDW;
            String stopFacilityKeyWD = nodeList.get(i).getId().toString().substring(3);
            String stopFacilityKeyDW = stopFacilityKeyWD + ".9";
            String stopname = nodeMap.get(nodeList.get(i));
            Coord tscoord = nodeList.get(i).getCoord();

            /** Some helpful hints:
             * i=0: Warschauer Str. ARRIVING (only DW)
             * i=1: Warschauer Str. DEPARTING (only WD) -> link this one 38360
             * i=*: Other stops (both)
             * */

            if(i!=0) {

                if (tsfacilities.containsKey(Id.create(stopFacilityKeyWD, TransitRouteStop.class)) && i==1) {
                   // TODO: Find nicer solution for this if-condition
                    continue;
                } else {
                    stopFacilityKeyWD = nodeList.get(i).getId().toString().substring(3) + ".8";
                    stopFacilityWD = createTransitStopFacility(tscoord, tschedule, stopFacilityKeyWD);
                    stopFacilityWD.setName(stopname);
                    tschedule.addStopFacility(stopFacilityWD);
                    System.out.println(links.get("WD").get(i-1));       // new
                    stopFacilityWD.setLinkId(links.get("WD").get(i-1)); // new
                }
//                System.out.println("TSFACILITIY WD-"+(i-2)+": "+stopFacilityWD);  // for troubleshooting on TransitStopFacilities WD
                TransitRouteStop stopWD = tsfactory.createTransitRouteStop(stopFacilityWD, 60. * (i-1), (60. * (i-1)));
                stopWD.setAwaitDepartureTime(true);
                stopsM10WD.add(i-2, stopWD);
//                System.out.println("LIST WD: " + stopsM10WD); // for troubleshooting
            } else{
                stopFacilityKeyWD = nodeList.get(i).getId().toString().substring(3) + ".8";
                stopFacilityDW = createTransitStopFacility(tscoord, tschedule, stopFacilityKeyWD);
                stopFacilityDW.setName(stopname);
//                System.out.println(links.get("DW").get(lsize-1));
                stopFacilityDW.setLinkId(links.get("DW").get(lsize-1));
//                System.out.println("TSFACILITIY DW-"+(lsize-1)+": "+stopFacilityDW);  // for troubleshooting on TransitStopFacilities DW-4 (rest below)
                tschedule.addStopFacility(stopFacilityDW);
                TransitRouteStop stopDW = tsfactory.createTransitRouteStop(stopFacilityDW, 60. * (nsize - 2), (60. * (nsize - 2)));
                stopDW.setAwaitDepartureTime(true);
                stopsM10DW.set(nsize - i - 2, stopDW);
//                System.out.println("LIST DW: "+stopsM10DW); // gives errormessage due to null-dummys
                continue;
            }
            if(i!=1) {
                if (tsfacilities.containsKey(Id.create(stopFacilityKeyDW, TransitRouteStop.class))) {
                    stopFacilityDW = createTransitStopFacility(tscoord, tschedule, stopFacilityKeyWD);
                    stopFacilityDW.setName(stopname);
                    if(stopAreaIdExists(stopFacilityWD)) { stopFacilityDW.setStopAreaId(stopFacilityWD.getStopAreaId()); }
                    tschedule.addStopFacility(stopFacilityDW);
                } else {
                    stopFacilityDW = createTransitStopFacility(tscoord, tschedule, stopFacilityKeyDW);
                    stopFacilityDW.setName(stopname);
                    if(stopAreaIdExists(stopFacilityWD)) { stopFacilityDW.setStopAreaId(stopFacilityWD.getStopAreaId()); }
                    tschedule.addStopFacility(stopFacilityDW);
                }
//                System.out.println(links.get("DW").get(lsize-i));
                stopFacilityDW.setLinkId(links.get("DW").get(lsize-i));
//                System.out.println("TSFACILITIY DW-"+(lsize-i)+": "+stopFacilityDW);  // for troubleshooting on TransitStopFacilities DW-0 - DW-3 (DW-4 above)
                TransitRouteStop stopDW = tsfactory.createTransitRouteStop(stopFacilityDW, 60. * (nsize - 1 - i), (60. * (nsize - 1 - i))+5);
                stopDW.setAwaitDepartureTime(true);
                stopsM10DW.set(nsize - i - 1, stopDW);
//                System.out.println("LIST DW: "+stopsM10DW);
            }
        }
//        System.out.println("STOPLIST WD: "+stopsM10WD);  // for troubleshooting on TransitRouteStops
//        System.out.println("STOPLIST DW: "+stopsM10DW);   // for troubleshooting on TransitRouteStops
        TRStops = trstops;

        return trstops;
    }

    private static TransitStopFacility createTransitStopFacility(Coord stopcoord, TransitSchedule tschedule, String stopFacilityKey) {
        System.out.print("\t\tCreate new TransitStopFacility (" + stopFacilityKey + ")...");

        TransitScheduleFactory tsfactory = tschedule.getFactory();
        TransitStopFacility newStopFacility;
        Id<TransitStopFacility> newtsfacilityId = Id.create(stopFacilityKey, TransitStopFacility.class);

        newStopFacility = tsfactory.createTransitStopFacility(newtsfacilityId, stopcoord, false);

        System.out.println("\tDone!");
        return newStopFacility;
    }

    boolean stopAreaIdExists(TransitStopFacility tsfacility){
        if(tsfacility.getStopAreaId()!=null){
            return true;
        }
        else{
            return false;
        }
    }


}
