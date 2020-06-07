package org.matsim.analysis.gruppeB;


import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

import org.matsim.core.population.PopulationUtils;
import org.matsim.core.scenario.ScenarioUtils;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RunKilometerAndTimeComparison {

    private static BufferedWriter bufferedWriter;


    public static void main(String[] args) {

        String originalConfigFile = "outputs/output_ori050/berlin-v5.5-1pct.output_config.xml";
        String modifiedConfigFile = "outputs/output_exp050/berlin-v5.5-1pct.output_config.xml";

        List<Plan> originalPlans =  computeSelectedPlans(originalConfigFile);
        List<Plan> modifiedPlans = computeSelectedPlans(modifiedConfigFile);

        Double originalTime = calculateTIme(originalPlans);
        Double modifiedTime = calculateTIme(modifiedPlans);

        Double originalDistance = calculateDistance(originalPlans);
        Double modifiedDistance = calculateDistance(modifiedPlans);

        Double timeDiffernce = modifiedTime - originalTime;
        Double distanceDifference = modifiedDistance - originalDistance;

        System.out.println("OriginalTime: " + originalTime/3600 + "\n ModifiedTime: " + modifiedTime/3600
        + "\n OriginalDistance: " + originalDistance/1000 + "\n ModifiedDistance: " + modifiedDistance/1000);


        try {
            FileWriter fileWriter = new FileWriter("src/main/java/org/matsim/analysis/gruppeB/DifferncesInTimeAndDistance.txt");
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write( "OriginalTime in hours: " + originalTime/3600 + "\n ModifiedTime in hours: " + modifiedTime/3600
                    + "\n OriginalDistance in kilometers: " + originalDistance/1000 + "\n ModifiedDistance in kilometers: " + modifiedDistance/1000
            + "\n Time difference in hours: " + timeDiffernce/3600 + "\n Distance differnce in kilometers: " + distanceDifference/1000);
            bufferedWriter.close();
        } catch(IOException ee){
            throw new RuntimeException(ee);
        }
    }

    private static List<Plan> computeSelectedPlans(String configFileName){
        List<Plan> selectedPlans = new ArrayList<>();

        Config config = ConfigUtils.loadConfig(configFileName);
        config.plans().setInputFile("berlin-v5.5-1pct.output_plans.xml.gz");
        Scenario scenario = ScenarioUtils.loadScenario(config);
        Population population = scenario.getPopulation();
        Map<Id<Person>, ? extends Person> persons = population.getPersons();

        for (Id<Person> id : persons.keySet()){
            Person p = PopulationUtils.findPerson(id,scenario);


            selectedPlans.add(p.getSelectedPlan());
        }

        return selectedPlans;
    }


    private static Double calculateTIme(List<Plan> Plans) {
        Double time = 0.0;


        for (Plan plan : Plans){
            List<PlanElement> planElements = plan.getPlanElements();

            for (PlanElement planElement: planElements) {
                if(planElement instanceof Leg){
                    time += ((Route)(((Leg)planElement).getRoute())).getTravelTime().seconds();

                }

            }

        }
        return time;
    }

    private static Double calculateDistance(List<Plan> Plans) {
        Double distance = 0.0;

        for (Plan plan : Plans){
            List<PlanElement> planElements = plan.getPlanElements();

            for (PlanElement planElement: planElements) {
                if(planElement instanceof Leg){
                    distance+= ((Route)(((Leg)planElement).getRoute())).getDistance();

                }

            }

        }
        return distance;
    }

}
