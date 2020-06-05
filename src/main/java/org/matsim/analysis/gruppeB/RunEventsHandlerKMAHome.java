package org.matsim.analysis.gruppeB;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.Vehicle;
import java.util.List;
import java.util.Map;

public class RunEventsHandlerKMAHome {
    public static void main(String[] args) {
        /**
        1. Check inputfile/configfile path
        2. Set ouputfile paths: "example" for KMAenterlinks / "example_rev" for KMAleavelinks -> Check BerlinKmaCounter
         */
        String inputFile = "output_exp050/berlin-v5.5-1pct.output_events.xml.gz";
        String configFile = "output_exp050/berlin-v5.5-1pct.output_config.xml";
        //String outputFile = "output_exp050/numberOfAgents.txt";
        String outputFile = "output_exp050/numberOfAgents_rev.txt";
        //String PersonHomesFile = "output_exp050/HomeAreas.txt";
        String PersonHomesFile = "output_exp050/HomeAreas_rev.txt";

        //String PersonHomesFile_CSV = "output_exp050/HomeAreas.csv";
        String PersonHomesFile_CSV = "output_exp050/HomeAreas_rev.csv";

        EventsManager eventsManager = EventsUtils.createEventsManager();

        BerlinKmaCounter kmaCounter = new BerlinKmaCounter(outputFile);
        eventsManager.addHandler(kmaCounter);

        MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);
        eventsReader.readFile(inputFile);
        List<LinkEnterEvent> vehicleEnterKmaEvents = kmaCounter.writeListOfEvents();
        /** (1) Classic printResult: "Hour, From East, From West, Total" in terminal and .txt */
        // kmaCounter.printResult();
        /** (2) Ordered, chronological printResult: "TimeStamp, VehicleID From East, VehicleID From West, Total" in terminal and .txt*/
        // kmaCounter.printResult_timesAndIds();
        /** (3) Ordered, chronological printResult: "TimeStamp, VehicleID, From East/West" in terminal and .txt  */
        kmaCounter.printResult_timesAndIds_combi();

        /** Get "Timestamp, VehicleID"-Map and "Timestamp"-List for HomeAnalyser */
        Map<Double, Id< Vehicle >> vehiclemap = kmaCounter.gettimevehiclemap();
        List<Double> timelist = kmaCounter.getCombitimes();

        eventsManager.removeHandler(kmaCounter);

        PersonEnterVehicleTimeCheck personEnterVehicleTimeCheck = new PersonEnterVehicleTimeCheck(vehicleEnterKmaEvents);
        eventsManager.addHandler(personEnterVehicleTimeCheck);
        eventsReader.readFile(inputFile);
        List<PersonEntersVehicleEvent> personEnterVehicleThatUsedKma = personEnterVehicleTimeCheck.writeListOfEvents();
        eventsManager.removeHandler(personEnterVehicleTimeCheck);

        HomeAnalyser homeAnalyser = new HomeAnalyser(vehicleEnterKmaEvents, personEnterVehicleThatUsedKma, PersonHomesFile, PersonHomesFile_CSV, vehiclemap, timelist);
        eventsManager.addHandler(homeAnalyser);
        eventsReader.readFile(inputFile);
        Config config = ConfigUtils.loadConfig(configFile);

        // Daniel: Had to change network input directory, comment this if you get a FileNotFoundException
        config.network().setInputFile("../modified-cloned-berlin-matsim.xml.gz");

        Scenario scenario = ScenarioUtils.loadScenario(config);
        homeAnalyser.analyseHome(scenario);

        /** (1) Classic printResult: "Person ID, HomeLink, HomeCoord" in .txt */
        //homeAnalyser.printResult();

        /** (2) Ordered, chronological printResult: "Time, Person ID, Home X, Home Y" in .txt */
        homeAnalyser.printResult_v2();

        /** (3) Ordered, chronological printResult: "Time, Person ID, Home X, Home Y" in .csv (for better use in Via) */
        homeAnalyser.printResult_v2_csv();

    }
}


