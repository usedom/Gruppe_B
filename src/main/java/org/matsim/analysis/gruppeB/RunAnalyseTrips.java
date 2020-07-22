package org.matsim.analysis.gruppeB;

import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;

public class RunAnalyseTrips {
    public static void main(String[] args) {
        String inputFile = "output_exp001/berlin-v5.5-1pct.output_events.xml.gz";
        String outputFile = "output_exp001/TripdurationByCar.txt";

        EventsManager eventsManager = EventsUtils.createEventsManager();

        AnalyzeTripsPersonID analyzeTripsPersonID = new AnalyzeTripsPersonID();
        eventsManager.addHandler(analyzeTripsPersonID);

        MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);
        eventsReader.readFile(inputFile);

        analyzeTripsPersonID.print();
    }
}
