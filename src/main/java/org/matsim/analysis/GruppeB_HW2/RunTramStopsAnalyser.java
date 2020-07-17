package org.matsim.analysis.GruppeB_HW2;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class RunTramStopsAnalyser {

    public static void main(String[] args) {

        String configin = "outputs/output_ori050/berlin-v5.5-1pct.output_config.xml";
        String input = "outputs/output_ori050/berlin-v5.5-1pct.output_events.xml.gz";
        //String output = "gruppeB_TXSandCSV/HaltestellenAuslastung.txt";
        String output = "gruppeB_TXSandCSV/PersonenSteigenInDieTram.txt";

        Config config =  ConfigUtils.loadConfig(configin);

        Scenario scenario = ScenarioUtils.loadScenario(config);

        //Fahrzeuge der M10
        List<Id<Vehicle>> trvehicles = new ArrayList<>(scenario.getTransitVehicles().getVehicles().keySet());
        List<Id<Vehicle>> fahrzeuge = new ArrayList<>();
        for (int i = 0; i < trvehicles.size(); i++){
            if (String.valueOf(trvehicles.get(i)).contains("pt_M10")){
                fahrzeuge.add(trvehicles.get(i));
            }
        }

        EventsManager eventsManager = EventsUtils.createEventsManager();

        TramStopAnalyzer tramStopAnalyzer = new TramStopAnalyzer(output, fahrzeuge);
        eventsManager.addHandler(tramStopAnalyzer);

        //System.out.println(scenario.getTransitSchedule().getFacilities().get(Id.createNodeId("000005100035")).getName());

        MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);
        eventsReader.readFile(input);

        tramStopAnalyzer.print();


    }
}
