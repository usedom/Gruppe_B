package org.matsim.analysis.gruppeB;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.vehicles.Vehicle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LinkEventHandler implements LinkEnterEventHandler {
    /* meine Idee war jetzt hier, dass man eine Art Abfrage macht welche Personen/Autos betroffen sind im sinne von
    * wenn ein Auto link 1 entert ODER link 2 ODER link 3... (Da natürlich die Links von der Karl Marx Allee),
    * Damm schreib diese Person oder vehicle iD in eine liste*/
    public Map<Id<Vehicle>, Double> WichtigeVehicles = new HashMap<>();

    int[] MostInterestingLinks = {54738};
//54738,49528,132668,2942,50779,48093,68519,141526,86406,70094,112640,5198,152474,
//            152091, 113232, 113237,126333,97508,96172,96171,57167, 58881, 52938,113580,57059,69132,57062,69223,94781,
//            113244,30224,50381,89327,59654,99708
    @Override
   public void handleEvent(LinkEnterEvent event){
       for (int i : MostInterestingLinks) {
            if (event.getLinkId().equals(Id.createLinkId(Integer.toString(i)))) {
                WichtigeVehicles.put(event.getVehicleId(), event.getTime()); //Vielleicht ist es sinnvoller anstatt 0 noch die Zeit zu adden wenn Fahrzeug link befahren hat
            }
        }

    }

    public void print(){
        System.out.println(Arrays.asList(WichtigeVehicles));
        System.out.println(WichtigeVehicles.size());
    }

    Map<Id<Vehicle>, Double> getVev(){return WichtigeVehicles;}

}
