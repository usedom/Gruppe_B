package org.matsim.analysis.gruppeB;

//import com.sun.jndi.ldap.Ber;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;

public class RunEventsHandlerKMA {
    public static void main(String[] args) {
        String inputFile = "testcounter/berlin-v5.5-1pct.output_events.xml.gz";
        String outputFile = "testcounter/linkKMAcounter.txt";

        EventsManager eventsManager = EventsUtils.createEventsManager();

        /*
        SimpleEventHandler eventHandler = new SimpleEventHandler();
        eventsManager.addHandler(eventHandler);
        */
        /*
        LinkEventHandler eventHandler1 = new LinkEventHandler(outputFile);
        eventsManager.addHandler(eventHandler1);

         */

        BerlinKmaCounter kmaCounter = new BerlinKmaCounter(outputFile);
        eventsManager.addHandler(kmaCounter);

        MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);
        eventsReader.readFile(inputFile);

        /*
        MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);
        eventsReader.readFile(inputFile);

         */

        kmaCounter.printResult();
    }
}
