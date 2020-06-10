package org.matsim.analysis.gruppeB;


import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.events.EventsReaderXMLv1;
import org.matsim.core.network.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class PersonEnterVehicleTimeCheck implements PersonEntersVehicleEventHandler {

    private List<LinkEnterEvent> kmaEvents;
    private List<PersonEntersVehicleEvent> personEntersVehicleEventsWhichUsedKMA = new ArrayList<>();

    public PersonEnterVehicleTimeCheck(List<LinkEnterEvent> listOfLinkEnterEvents){
        kmaEvents = listOfLinkEnterEvents;

    }

//Checks that the linkEnterEvent is after the personEntersVehicleEvent
    @Override
    public void handleEvent(PersonEntersVehicleEvent personEntersVehicleEvent) {
        for (LinkEnterEvent kmaEvent: kmaEvents) {
            if (kmaEvent.getVehicleId().compareTo(personEntersVehicleEvent.getVehicleId()) == 0 && kmaEvent.getTime() > personEntersVehicleEvent.getTime()) {
                personEntersVehicleEventsWhichUsedKMA.add(personEntersVehicleEvent);
            }
        }

    }

    public List<PersonEntersVehicleEvent> writeListOfEvents() {
        return this.personEntersVehicleEventsWhichUsedKMA;

    }
}
