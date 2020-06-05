package org.matsim.analysis.gruppeB;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.population.Person;
import org.matsim.vehicles.Vehicle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PersonEntersVehicleHandler implements PersonEntersVehicleEventHandler {

    //public List<Id<Vehicle>> FahrzeugKennzeichen = new ArrayList<>();
    public Map<Id<Person>, Double> PersonenImVehicle = new HashMap<>();
    private LinkEventHandler KMAevents;
    public PersonEntersVehicleHandler(LinkEventHandler linkEventHandler) {
       KMAevents=linkEventHandler;
    }

    @Override
    public void handleEvent(PersonEntersVehicleEvent personEntersVehicleEvent) {
        for (Id<Vehicle> car : KMAevents.getVev().keySet()){
        if (personEntersVehicleEvent.getVehicleId().compareTo(car)==0){
            PersonenImVehicle.put(personEntersVehicleEvent.getPersonId(), personEntersVehicleEvent.getTime());
        }
        }
    }
    public void print(){
        System.out.println(Arrays.asList(PersonenImVehicle));
    }
}
