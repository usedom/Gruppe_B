package org.matsim.analysis.gruppeB;

import org.graphstream.ui.j2dviewer.renderer.shape.swing.CircleOnEdge;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.misc.OptionalTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RunKilometerAndTimeComparison {

    public static void main(String[] args) {
        String originalConfigFile = "marcelORIGINAL/berlin-v5.5-1pct.output_config.xml";
        String modifiedConfigFile = "marcelMODIFIED/berlin-v5.5-1pct.output_config.xml";

        Config originalConfig = ConfigUtils.loadConfig(originalConfigFile);
        Config modifiedConfig = ConfigUtils.loadConfig(modifiedConfigFile);

        Scenario originalScenario = ScenarioUtils.loadScenario(originalConfig);
        Scenario modfiedScenario = ScenarioUtils.loadScenario(modifiedConfig);



        Population originalPopulation = originalScenario.getPopulation();
        Population modifiedPopulation = modfiedScenario.getPopulation();

        Map<Id<Person>, ? extends Person> originalPerson = originalPopulation.getPersons();
        Map<Id<Person>, ? extends Person> modifiedPerson = modifiedPopulation.getPersons();



        List<Plan> originalPlans =  getAllSelectedPlans(originalScenario, originalPerson);
        List<Plan> modifiedPlans = getAllSelectedPlans(modfiedScenario, modifiedPerson);

        Double originalTime = calculateTIme(originalPlans);
        Double modifiedTime = calculateTIme(modifiedPlans);

        Double originalDistance = calculateDistance(originalPlans);
        Double modifiedDistance = calculateDistance(modifiedPlans);

        System.out.println("OriginlTime: " + originalTime/(originalPerson.size()*3600) + "\n ModifiedTime: " + modifiedTime/(modifiedPerson.size()*3600)
        + "\n OriginalDistance: " + originalDistance/(originalPerson.size()*1000) + "\n ModifiedDistance: " + modifiedDistance/(modifiedPerson.size()*1000));







    }

    private static Double calculateTIme(List<Plan> Plans) {
        Double time = 0.0;


        for (Plan plan : Plans){
            List<PlanElement> planElements = plan.getPlanElements();

            for (PlanElement planElement: planElements) {
                if(planElement instanceof Leg){
                    //System.out.println(((Route)(((Leg)planElement).getRoute())).getTravelTime());
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

    private static List<Plan> getAllSelectedPlans(Scenario Scenario, Map<Id<Person>, ? extends Person> Persons) {
        List<Plan> selectedPlans = new ArrayList<>();

        for (Id<Person> id : Persons.keySet()){
            Person p = PopulationUtils.findPerson(id,Scenario);
            selectedPlans.add(p.getSelectedPlan());
        }

        return selectedPlans;
    }

}
