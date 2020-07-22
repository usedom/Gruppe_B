package org.matsim.analysis.GruppeB_HW2;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.Map;

public class RunModalAnalyzer {
    public static void main(String[] args) {

        // Uncomment line below to printout your root folder
        System.out.println(System.getProperty("user.dir"));

        String configFile = "outputs/output_ori050/berlin-v5.5-1pct.output_config.xml";
        Config config = ConfigUtils.loadConfig(configFile);

        Grid grid = new Grid(100);

        Map<String, Integer>[][] berlingrid = grid.createGrid();  // new Grid(100).createGrid();
        grid.printGrid(berlingrid);
        grid.printGridLines_via(berlingrid);

        Scenario scenario = ScenarioUtils.loadScenario(config);

        GridZoneAnalyzer gza = new GridZoneAnalyzer(config, scenario);
        Map<Id<Person>, Coord> persons5050 = gza.getPersonsHomeInZone(berlingrid[49][59]);
        gza.modalSplitInZone(persons5050);

    }
}
