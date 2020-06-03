package org.matsim.analysis.gruppeB;

//import com.sun.jndi.ldap.Ber;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.List;

public class RunEventsHandlerKMAHome {
    public static void main(String[] args) {
        String inputFile = "outputs/output_exp050/berlin-v5.5-1pct.output_events.xml.gz";
        String outputFile = "outputs/output_exp050/numberOfAgents.txt";
        String configFile = "outputs/output_exp050/berlin-v5.5-1pct.output_config.xml";
        String PersonHomesFile = "outputs/output_exp050/HomeAreas.txt";


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
        List<LinkEnterEvent> vehicleEnterKmaEvents = kmaCounter.writeListOfEvents();
        kmaCounter.printResult();
        eventsManager.removeHandler(kmaCounter);

        PersonEnterVehicleTimeCheck personEnterVehicleTimeCheck = new PersonEnterVehicleTimeCheck(vehicleEnterKmaEvents);
        eventsManager.addHandler(personEnterVehicleTimeCheck);
        eventsReader.readFile(inputFile);
        List<PersonEntersVehicleEvent> personEnterVehicleThatUsedKma = personEnterVehicleTimeCheck.writeListOfEvents();
        eventsManager.removeHandler(personEnterVehicleTimeCheck);

        HomeAnalyser homeAnalyser = new HomeAnalyser(vehicleEnterKmaEvents,
                personEnterVehicleThatUsedKma, PersonHomesFile);
        eventsManager.addHandler(homeAnalyser);
        eventsReader.readFile(inputFile);
        Config config = ConfigUtils.loadConfig(configFile);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        homeAnalyser.analyseHome(scenario);
        homeAnalyser.printResult();


    }
}


