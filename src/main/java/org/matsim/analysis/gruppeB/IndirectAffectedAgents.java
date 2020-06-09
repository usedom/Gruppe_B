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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndirectAffectedAgents {

    private static BufferedWriter bufferedWriter;

    public static void main(String[] args) {
        String originalConfigFile = "outputs/output_ori050/berlin-v5.5-1pct.output_config.xml";
        String modifiedConfigFile = "outputs/output_exp050/berlin-v5.5-1pct.output_config.xml";

        //by deleting the ! in line 85 you get unaffected agents who don't change plans
        //
        List<Id<Person>> affectedPersonsIDs = computeAffectedPersons(originalConfigFile,modifiedConfigFile);

        //Uncomment if you want to know time and distance differences of affected (agents that changed their plans
        // ) agents
      /*  List<Plan> originalaffectedPlans = computeSelectedPlans(originalConfigFile, affectedPersonsIDs);
        List<Plan> modifiedaffectedPlans = computeSelectedPlans(modifiedConfigFile, affectedPersonsIDs);

        Double originalTime = calculateTIme(originalaffectedPlans);
        Double modifiedTime = calculateTIme(modifiedaffectedPlans);

        Double originalDistance = calculateDistance(originalaffectedPlans);
        Double modifiedDistance = calculateDistance(modifiedaffectedPlans);

        Double timeDiffernce = modifiedTime - originalTime;
        Double distanceDifference = modifiedDistance - originalDistance;

        System.out.println("OriginalTime: " + originalTime/3600 + "\n ModifiedTime: " + modifiedTime/3600
                + "\n OriginalDistance: " + originalDistance/1000 + "\n ModifiedDistance: " + modifiedDistance/1000);*/

        try {
            FileWriter fileWriter = new FileWriter("src/main/java/org/matsim/analysis/gruppeB/affectedPersons.txt");
            bufferedWriter = new BufferedWriter(fileWriter);
            for (Id<Person> personId : affectedPersonsIDs){
                bufferedWriter.write("Person Ids");
                bufferedWriter.write("\n" + personId.toString());
            }

            bufferedWriter.close();
        } catch(IOException ee){
            throw new RuntimeException(ee);
        }

    }
// method computes list of person Ids that have different plans based on two config files
    private static List<Id<Person>> computeAffectedPersons(String originalConfigFileName, String modifiedConfigFileName ){
       List<Id<Person>> affectedID = new ArrayList<>();

        Map<Id<Person>,Plan> originalSelectedPlans = new HashMap<>();
        Map<Id<Person>,Plan> modifiedSelectedPlans = new HashMap<>();

        Config originalConfig = ConfigUtils.loadConfig(originalConfigFileName);
        originalConfig.plans().setInputFile("berlin-v5.5-1pct.output_plans.xml.gz");

        Config modifiedConfig = ConfigUtils.loadConfig(modifiedConfigFileName);
        modifiedConfig.plans().setInputFile("berlin-v5.5-1pct.output_plans.xml.gz");

        Scenario originalScenario = ScenarioUtils.loadScenario(originalConfig);
        Scenario modifiedScenario = ScenarioUtils.loadScenario(modifiedConfig);

        Population originalPopulation = originalScenario.getPopulation();

        Map<Id<Person>, ? extends Person> originalPopulationPersons = originalPopulation.getPersons();

        for (Id<Person> id : originalPopulationPersons.keySet()){
            Person pOriginal = PopulationUtils.findPerson(id,originalScenario);
            Person pModified = PopulationUtils.findPerson(id,modifiedScenario);

// with ! you get persons that changed their plans and without ! persons that did not change their plans
           if(!pOriginal.getSelectedPlan().getPlanElements().toString().equals(pModified.getSelectedPlan().getPlanElements().toString())){
               affectedID.add(id);
             //  System.out.println(id.toString() +  "   " +  pOriginal.getSelectedPlan().getPlanElements().toString());
           }
        }

        return affectedID;
    }
// If you want to know the difference in Time and distance uncomment the block
  /*  private static List<Plan> computeSelectedPlans(String configFileName, List<Id<Person>> unaffectedPersons){
        List<Plan> selectedPlans = new ArrayList<>();

        Config config = ConfigUtils.loadConfig(configFileName);
        config.network().setInputFile("berlin-v5.5-1pct.output_network.xml.gz");
        config.plans().setInputFile("berlin-v5.5-1pct.output_plans.xml.gz");
        Scenario scenario = ScenarioUtils.loadScenario(config);
        Population population = scenario.getPopulation();
        Map<Id<Person>, ? extends Person> persons = population.getPersons();

        for (Id<Person> id : persons.keySet()){
            if(unaffectedPersons.contains(id)) {
                Person p = PopulationUtils.findPerson(id, scenario);


                selectedPlans.add(p.getSelectedPlan());
            }
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
    } */


}
