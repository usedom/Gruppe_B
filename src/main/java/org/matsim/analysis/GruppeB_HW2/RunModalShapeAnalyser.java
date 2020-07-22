package org.matsim.analysis.GruppeB_HW2;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RunModalShapeAnalyser {
    private static BufferedWriter writer;

    public static void main(String[] args) {

        // Uncomment line below to printout your root folder
        System.out.println(System.getProperty("user.dir"));

        String configFile = "outputs/output_ori050/berlin-v5.5-1pct.output_config.xml";
        String outputTxt = "gruppeB_TXSandCSV/shapeModes.txt";
        String shapeFile = "scenarios/berlin-v5.5-1pct/input/LOR_new.shp";


        Config config = ConfigUtils.loadConfig(configFile);


        Scenario scenario = ScenarioUtils.loadScenario(config);

        ShapesZoneAnalyzer shapesZoneAnalyzer = new ShapesZoneAnalyzer(config, scenario, shapeFile);


        int [] extendedArea = {60, 65,61,66,53,55,54,287,289,283,280,51};
        Map<Id<Person>, Coord> personsAlongM10 = new HashMap<>();


        String output = "";
        for (int i : extendedArea) {
            Map<Id<Person>, Coord> persons5050 = shapesZoneAnalyzer.getPersonsHomeInShape(i);

            for (Id<Person> personId : persons5050.keySet()){
                personsAlongM10.put(personId, persons5050.get(personId));
            }


           // output+="\n\n" + i;
            //output += shapesZoneAnalyzer.modalSplitInZone(persons5050);
        }

        output = shapesZoneAnalyzer.modalSplitInZone(personsAlongM10);


        try {
            FileWriter fileWriter = new FileWriter(outputTxt);
            writer = new BufferedWriter(fileWriter);
            writer.write(output);
            writer.close();
        } catch(IOException ee){
            throw new RuntimeException(ee);
        }
        System.out.println("\nDONE!\n###\n");
    }
}
