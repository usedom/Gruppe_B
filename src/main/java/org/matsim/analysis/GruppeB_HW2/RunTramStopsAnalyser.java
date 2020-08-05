package org.matsim.analysis.GruppeB_HW2;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.scenario.ScenarioUtils;

public class RunTramStopsAnalyser {

    public static void main(String[] args) {

        String configin = "outputs/output_100ext/berlin-v5.5-1pct.output_config.xml";
        String input = "outputs/output_100ext/berlin-v5.5-1pct.output_events.xml.gz";
        String output = "gruppeB_TXSandCSV/personlist_entering_M10.txt";
        //String output = "gruppeB_TXSandCSV/Using_M10_ori.txt";

        Config config =  ConfigUtils.loadConfig(configin);

        Scenario scenario = ScenarioUtils.loadScenario(config);

        EventsManager eventsManager = EventsUtils.createEventsManager();

        TramStopAnalyzer tramStopAnalyzer = new TramStopAnalyzer(scenario, output);
        eventsManager.addHandler(tramStopAnalyzer);

        MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);
        eventsReader.readFile(input);

       tramStopAnalyzer.print();

    }
}
