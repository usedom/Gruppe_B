/*
package org.matsim.analysis.gruppeB;

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
import java.util.Map;

public class RunModalShapeAnalyser_backup {
    private static BufferedWriter writer;

    public static void main(String[] args) {

        // Uncomment line below to printout your root folder
        System.out.println(System.getProperty("user.dir"));

        String configFile = "outputs/output_ori050/berlin-v5.5-1pct.output_config.xml";
        String outputTxt = "gruppeB_TXSandCSV/shapeModes.txt";
        String shapeFile = "scenarios/berlin-v5.5-1pct/input/LOR_new.shp";



        Config config = ConfigUtils.loadConfig(configFile);


        Scenario scenario = ScenarioUtils.loadScenario(config);

        ShapesZoneAnalyzer_djp shapesZoneAnalyzerDjp = new ShapesZoneAnalyzer_djp(config, scenario, shapeFile);


      //  [int] intrstingShaps = []
        String output = "";
        for (int i = 1; i < 448; i++){
            Map<Id<Person>, Coord> persons5050 = shapesZoneAnalyzerDjp.getPersonsHomeInShape(i);
            output+="\n\n" + i;
            output += shapesZoneAnalyzerDjp.modalSplitInZone(persons5050);


        }


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
*/
