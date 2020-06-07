package org.matsim.analysis.gruppeB;

import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;

import java.util.HashMap;
import java.util.Map;

public class ModeStatCounterKMA implements PersonDepartureEventHandler {
    private Map<Integer, Integer> LegModeForPerson = new HashMap<>();

    public void handleEvent(PersonDepartureEvent departureEvent){
        LegModeForPerson.put(0,0);
    }

//Constructor für die Person IDs müsste hier noch rein

}
