package org.matsim.analysis.gruppeB;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.LinkLeaveEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.LinkLeaveEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.vehicles.Vehicle;

import java.util.*;
import java.util.stream.Collectors;

public class AnalyzeTripsPersonID implements LinkEnterEventHandler, LinkLeaveEventHandler {

    public Map<Id<Vehicle>, Double> VehiclesLeaveZeit = new HashMap<>();
    private List<Id<Vehicle>> MostInterestingCarID = new ArrayList<>();
    public Map<Id<Link>, Double> linkELZeiten = new HashMap<>();
    public Map<Id<Vehicle>, Double> linkCLZeiten = new HashMap<>();
    public Map<Id<Link>, Double> linkEZeiten = new HashMap<>();
    public Map<Id<Vehicle>, Double> linkCZeiten = new HashMap<>();
    public Map<Id<Vehicle>, Double> AutoEnterZeit = new HashMap<>();
    int[] MostInterestingLinksOne = {49528};
            //132668,2942,50779,48093,68519,141526,86406,70094,112640,5198,152474,
            //152091, 113232, 113237,126333};
    /*49528,54738,132668,2942,50779,48093,68519,141526,86406,70094,112640,5198,152474,
              152091, 113232, 113237,126333,|97508,96172,96171,57167, 58881, 52938,113580,57059,69132,57062,69223,94781,
              113244,30224,50381,89327,59654,99708};*/

    @Override
    public void handleEvent(LinkLeaveEvent linkLeave){
        MostInterestingCarID.add(Id.createVehicleId(389716501));
        //Generate die VehicleIds for the vehicles that leaving the links that build the KMA
        /*for (int i : MostInterestingLinksOne) {
            if (linkLeave.getLinkId().equals(Id.createLinkId(Integer.toString(i)))) {
                MostInterestingCarID.add(linkLeave.getVehicleId());
            }
        }*/
        //MostInterestingCarID.clear();
        //MostInterestingCarID.add(Id.createVehicleId(389716501));
        //MostInterestingCarID.add(Id.createVehicleId(435039901));
        /*now run each carID and for this car the links it leaves and get the leavetime. sort this leave
        time and get the highest value. store this value with the car id in a hashmap 'vehicle leaves time'*/
        for (Id<Vehicle> carId : MostInterestingCarID) {
            for (int i : MostInterestingLinksOne) {
                if (linkLeave.getVehicleId().compareTo(Id.createVehicleId(carId))==0 && linkLeave.getLinkId().equals(Id.createLinkId(Integer.toString(i)))) {
                    linkELZeiten.put(linkLeave.getLinkId(), linkLeave.getTime());
                    LinkedHashMap<Id<Link>, Double> resultLeave = linkELZeiten.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x,y) -> x, LinkedHashMap::new));
                    double ZeLe = resultLeave.entrySet().stream().findFirst().get().getValue();
                    linkCLZeiten.put(linkLeave.getVehicleId(), ZeLe);
                    System.out.println("Lauft er bis hier test" +linkCLZeiten);
                }
            }
        }
    }

    @Override
    public void handleEvent(LinkEnterEvent linkEnter){
        /*almost same procedure here: run through all carIds and links this car enters and get smallest value.
        Store this value and the car id in hashmap 'vehicle enters zeit'*/
        for (Id<Vehicle> carId : MostInterestingCarID) {
            List<Double> eZeiten = new ArrayList<>();
            for (int i : MostInterestingLinksOne) {
                if (linkEnter.getVehicleId().compareTo(Id.createVehicleId(carId))==0 && linkEnter.getLinkId().equals(Id.createLinkId(Integer.toString(i)))) {
                    eZeiten.add(linkEnter.getTime());
                  //  LinkedHashMap<Id<Link>, Double> resultEnter = linkEZeiten.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x,y) -> x, LinkedHashMap::new));
                  //  double ZeEn = resultEnter.entrySet().stream().findFirst().get().getValue();
                  //  linkCZeiten.put(linkEnter.getVehicleId(), ZeEn);
                }
            }
        }

        /*for (Id<Vehicle> carId : MostInterestingCarID) {
            List<Double> eZeiten = new ArrayList<>();
            for (int i : MostInterestingLinksOne) {
                if (linkEnter.getVehicleId().compareTo(Id.createVehicleId(carId))==0 && linkEnter.getLinkId().equals(Id.createLinkId(Integer.toString(i)))) {
                    linkEZeiten.put(linkEnter.getLinkId(), linkEnter.getTime());
                    LinkedHashMap<Id<Link>, Double> resultEnter = linkEZeiten.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x,y) -> x, LinkedHashMap::new));
                    double ZeEn = resultEnter.entrySet().stream().findFirst().get().getValue();
                    linkCZeiten.put(linkEnter.getVehicleId(), ZeEn);
                }
            }
        }*/
    }

    public void print(){
        //System.out.println("Sortiert Enterzeiten" + Arrays.asList(linkCZeiten));
        System.out.println("Sortiert Leavezeiten" + Arrays.asList(linkCLZeiten));
    }

}

