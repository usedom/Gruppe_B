package org.matsim.analysis.gruppeB;

import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;

public class RunPersonInVehicle {

    public static void main(String[] args) {
        String inputFile = "output_exp001/berlin-v5.5-1pct.output_events.xml";
        //String outputFile = "output_exp001/ITERS/testHandledEvents.txt";

        EventsManager eventsManager = EventsUtils.createEventsManager();

        LinkEventHandler linkEventHandler = new LinkEventHandler();
        eventsManager.addHandler(linkEventHandler);

        PersonEntersVehicleHandler personEntersVehicleHandler = new PersonEntersVehicleHandler(linkEventHandler);
        eventsManager.addHandler(personEntersVehicleHandler);

        /*Ich brauche noch ein Peron enters vehicle (Person ID) und person leaves vehicle (time) um zu schauen welche
        Personen im Auto waren als auto über KMA gefahren ist*/

        MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);
        eventsReader.readFile(inputFile);

        linkEventHandler.print();
    }
}
