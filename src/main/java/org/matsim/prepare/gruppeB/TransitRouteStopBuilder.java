package org.matsim.prepare.gruppeB;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Node;
import org.matsim.pt.transitSchedule.api.*;

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
        int size = nodeList.size();
        TransitScheduleFactory tsfactory = tschedule.getFactory();
        Map<Id<TransitStopFacility>, TransitStopFacility> tsfacilities = tschedule.getFacilities();

        List<TransitRouteStop> stopsM10WD = new ArrayList<>();
        List<TransitRouteStop> stopsM10DW = new ArrayList<>();
        Map<String,List<TransitRouteStop>> trstops = new HashMap<>(2);
        trstops.put("WD", stopsM10WD);
        trstops.put("DW", stopsM10DW);

        for (int i=0;i<size-1;i++){
            // Dummy! Don't try to read this out
            stopsM10DW.add(i, null);
        }

        for(int i=0; i<size; i++){
            TransitStopFacility stopFacilityWD;
            TransitStopFacility stopFacilityDW;
            String stopFacilityKeyWD = nodeList.get(i).getId().toString().substring(3);
            String stopFacilityKeyDW = stopFacilityKeyWD + ".1";
            String stopname = nodeMap.get(nodeList.get(i));
            Coord tscoord = nodeList.get(i).getCoord();

            /** Some helpful hints:
             * i=0: Warschauer Str. ARRIVING (only DW)
             * i=1: Warschauer Str. DEPARTING (only WD)
             * i=*: Other stops (both)
             * */

            if(i!=0) {

                if (tsfacilities.containsKey(Id.create(stopFacilityKeyWD, TransitRouteStop.class))) {
                    stopFacilityWD = tsfacilities.get(Id.create(stopFacilityKeyWD, TransitRouteStop.class));
                    // stopFacilityWD.setStopAreaId(Id.create("456789123", TransitStopArea.class));  // to test StopAreaId-function -> is not part of printout!
                    stopname = stopFacilityWD.getName();
                } else {
                    Id<Link> reflinkWD = links.get("WD").get(i);
                    stopFacilityWD = createTransitStopFacility(tscoord, reflinkWD, tschedule, stopFacilityKeyWD);
                    //stopname = stopname+i;
                    stopFacilityWD.setName(stopname);
                    tschedule.addStopFacility(stopFacilityWD);
                }
                System.out.println("TSFACILITIY WD-"+i+": "+stopFacilityWD);  // for troubleshooting on TransitStopFacilities
                TransitRouteStop stopWD = tsfactory.createTransitRouteStop(stopFacilityWD, 60. * (i-1), (60. * (i-1))+5);
                stopsM10WD.add(i-1, stopWD);
                //System.out.println("LIST WD: " + stopsM10WD); // for troubleshooting
            } else{
                stopFacilityDW = tsfacilities.get(Id.create(stopFacilityKeyWD, TransitRouteStop.class));
                TransitRouteStop stopDW = tsfactory.createTransitRouteStop(stopFacilityDW, 60. * (size - 2), (60. * (size - 2))+5);
                stopsM10DW.set(size - i - 2, stopDW);
                // System.out.println("LIST DW: "+stopsM10DW); // gives errormessage due to null-dummys
                continue;
            }
            if(i!=1) {

                if (tsfacilities.containsKey(Id.create(stopFacilityKeyDW, TransitRouteStop.class))) {
                    stopFacilityDW = tsfacilities.get(Id.create(stopFacilityKeyDW, TransitRouteStop.class));
                } else {
                    Id<Link> reflinkDW = links.get("DW").get(i);
                    stopFacilityDW = createTransitStopFacility(tscoord, reflinkDW, tschedule, stopFacilityKeyDW);
                    stopFacilityDW.setName(stopname);
                    if(stopAreaIdExists(stopFacilityWD)) { stopFacilityDW.setStopAreaId(stopFacilityWD.getStopAreaId()); }
                    tschedule.addStopFacility(stopFacilityDW);
                }
                System.out.println("TSFACILITIY DW-"+i+": "+stopFacilityDW);  // for troubleshooting on TransitStopFacilities
                TransitRouteStop stopDW = tsfactory.createTransitRouteStop(stopFacilityDW, 60. * (size - 1 - i), (60. * (size - 1 - i))+5);

                stopsM10DW.set(size - i - 1, stopDW);
                //System.out.println("LIST DW: "+stopsM10DW);
            }
        }
        System.out.println("STOPLIST WD: "+stopsM10WD);  // for troubleshooting on TransitRouteStops
        System.out.println("STOPLIST DW: "+stopsM10DW);   // for troubleshooting on TransitRouteStops
        TRStops = trstops;

        return trstops;
    }

    private static TransitStopFacility createTransitStopFacility(Coord stopcoord, Id<Link> refLinkWD, TransitSchedule tschedule, String stopFacilityKey) {
        System.out.print("\t\tCreate new TransitStopFacility (" + stopFacilityKey + ")...");

        TransitScheduleFactory tsfactory = tschedule.getFactory();
        TransitStopFacility newStopFacility;
        Id<TransitStopFacility> newtsfacilityId = Id.create(stopFacilityKey, TransitStopFacility.class);

        newStopFacility = tsfactory.createTransitStopFacility(newtsfacilityId, stopcoord, false);
        newStopFacility.setLinkId(refLinkWD);

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
