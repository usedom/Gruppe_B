package org.matsim.analysis.gruppeB;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.vehicles.Vehicle;

import java.util.*;

public class AnalyzeTripsPersonID implements LinkEnterEventHandler, LinkLeaveEventHandler {
    public Map<Id<Vehicle>, Double> VehiclesEnterZeit = new HashMap<>();
    public Map<Id<Vehicle>, Double> VehiclesLeaveZeit = new HashMap<>();
    private List<Id<Vehicle>> Fahrzeugkenn = new ArrayList<>();
    private List<Id<Vehicle>> MostInterestingCarID = new ArrayList<>();
    public Map<Id<Vehicle>, Double> WichtigeVehicles = new HashMap<>();
    int[] MostInterestingLinksOne = {49528,54738};
    /*,132668,2942,50779,48093,68519,141526,86406,70094,112640,5198,152474,
              152091, 113232, 113237,126333,97508,96172,96171,57167, 58881, 52938,113580,57059,69132,57062,69223,94781,
              113244,30224,50381,89327,59654,99708};*/

    @Override
    public void handleEvent(LinkLeaveEvent linkLeave){
        //Generate die VehicleIds for the vehicles that leaving the links that build the KMA
        for (int i : MostInterestingLinksOne) {
            if (linkLeave.getLinkId().equals(Id.createLinkId(Integer.toString(i)))) {
                MostInterestingCarID.add(linkLeave.getVehicleId());
            }
        }
        /*now run each carID and for this car the links it leaves and get the leavetime. sort this leave
        time and get the highest value. store this value with the car id in a hashmap 'vehicle leaves time'*/
        for (Id<Vehicle> carId : MostInterestingCarID) {
            for (  int i : MostInterestingLinksOne) {
                List<Double> RuckLeaveZeiten = new ArrayList<>();
                if (linkLeave.getVehicleId().compareTo(Id.createVehicleId(carId))==0 && linkLeave.getLinkId().equals(Id.createLinkId(Integer.toString(i)))) {
                    if (RuckLeaveZeiten.size()==0){
                        RuckLeaveZeiten.add(linkLeave.getTime());
                    }
                    if (RuckLeaveZeiten.size()>0 && linkLeave.getTime() > RuckLeaveZeiten.get(0) ) {
                        RuckLeaveZeiten.set(0, linkLeave.getTime());
                    }
                    VehiclesLeaveZeit.put(linkLeave.getVehicleId(), RuckLeaveZeiten.get(0));
                    //System.out.println("Test enter Zeit" + VehiclesEnterZeit);
                }
            }
        }
    }
    List<Double> PubRuckEnterZeiten = new ArrayList<>();
    @Override
    public void handleEvent(LinkEnterEvent linkEnter){
        /*almost same procedure here: run through all carIds and links this car enters and get smallest value.
        Store this value and the car id in hashmap 'vehicle enters zeit'*/
        for (Id<Vehicle> carId : MostInterestingCarID) {
            double RuckEnterZeiten = 0.0;
            double Test = 60000.0;
            for (  int i : MostInterestingLinksOne) {
                //List<Double> RuckEnterZeiten = new ArrayList<>();
                if (linkEnter.getVehicleId().compareTo(Id.createVehicleId(carId))==0 && linkEnter.getLinkId().compareTo(Id.createLinkId(Integer.toString(i)))==0) {
                   // if (RuckEnterZeiten.size()==0){
                   //     RuckEnterZeiten.add(linkEnter.getTime());
                   // }
                    //if (RuckEnterZeiten.size()>0 && linkEnter.getTime() <= RuckEnterZeiten.get(0) ) {
                    //    RuckEnterZeiten.set(0, linkEnter.getTime());
                    //}
                    if (linkEnter.getTime()<Test) {
                        RuckEnterZeiten = linkEnter.getTime();
                        Test = linkEnter.getTime();
                    }
                }
                PubRuckEnterZeiten.equals(RuckEnterZeiten);
            }
            VehiclesEnterZeit.put(linkEnter.getVehicleId(), RuckEnterZeiten);
            //System.out.println("Test enter Zeit" + VehiclesEnterZeit);
        }
    }

    public void print(){
        //System.out.println(Arrays.asList(VehiclesEnterZeit.get()));
        System.out.println(Arrays.asList(VehiclesEnterZeit));
        System.out.println(Arrays.asList(PubRuckEnterZeiten));
        //System.out.println(Arrays.asList(VehiclesLeaveZeit));
    }


}

