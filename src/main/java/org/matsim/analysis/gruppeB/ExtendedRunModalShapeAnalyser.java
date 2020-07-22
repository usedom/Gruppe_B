package org.matsim.analysis.gruppeB;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtendedRunModalShapeAnalyser {
    private static BufferedWriter writer;

    public static void main(String[] args) {

        // Uncomment line below to printout your root folder
        System.out.println(System.getProperty("user.dir"));
         String configFile = "outputs/output_100ext/berlin-v5.5-1pct.output_config.xml";
        String ModalsplitTxt = "gruppeB_TXSandCSV/ForMarcelWithOutNsxtMode.txt";
        String personGroupTxt = "gruppeB_TXSandCSV/PersonGroupAll.txt";
        String shapeFile = "scenarios/berlin-v5.5-1pct/input/LOR_new.shp";
        int[] shapeIDs = { 60, 65, 61, 66, 53, 55, 54, 287, 289, 283, 280, 51};


        Config config = ConfigUtils.loadConfig(configFile);
        config.network().setInputFile("./berlin-v5.5-1pct.output_network.xml.gz");
        config.transit().setTransitScheduleFile("./berlin-v5.5-1pct.output_transitSchedule.xml.gz");
        config.plans().setInputFile("./berlin-v5.5-1pct.output_plans.xml.gz");

        Scenario scenario = ScenarioUtils.loadScenario(config);

        ExtendedShapeZoneAnalyser shapesZoneAnalyzer = new ExtendedShapeZoneAnalyser(config, scenario, shapeFile);

        //Array of ShapeIds that are analysed
        
        Map<Id<Person>, List<Activity>> personsAlongM10 = new HashMap<>();


        String output = "";
        //For every id of a Shape that should be analysed
        for (int i : shapeIDs) {

            //We get a map with persons Ids and their activitys in that shape
            Map<Id<Person>, List<Activity>> persons5050 = shapesZoneAnalyzer.getPersonsWithActivityInShape(i);

            //Here it is controlled that if the persons id is allready in the keyset, that the new activitys are added to the activity list
            //and the key ist not overwritten
            for (Id<Person> personId : persons5050.keySet()) {
                if (personsAlongM10.containsKey(personId)) {
                    List<Activity> globalactivityList = personsAlongM10.get(personId);
                    List<Activity> localActivityList = persons5050.get(personId);
                    for (Activity activity : localActivityList) {
                        globalactivityList.add(activity);
                    }
                    personsAlongM10.put(personId, globalactivityList);
                } else {
                    personsAlongM10.put(personId, persons5050.get(personId));
                }
            }

        }


        // output+="\n\n" + i;
        //output += shapesZoneAnalyzer.modalSplitInZone(persons5050);

        System.out.println("Population: " + personsAlongM10.size());

        output = shapesZoneAnalyzer.modalSplitInZone(personsAlongM10,true,true, true, personGroupTxt);


        try {
            FileWriter fileWriter = new FileWriter(ModalsplitTxt);
            writer = new BufferedWriter(fileWriter);
            writer.write(output);
            writer.close();
        } catch (IOException ee) {
            throw new RuntimeException(ee);
        }
        System.out.println("\nDONE!\n###\n");
    }
}


