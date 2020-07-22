package org.matsim.analysis.GruppeB_HW2;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.population.PopulationUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridZoneAnalyzer {

    Config config;
    Scenario scenario;

    public GridZoneAnalyzer(Config config, Scenario scenario){
        this.config = config;
        this.scenario = scenario;
    }


    public Map<Id<Person>, Coord> getPersonsHomeInZone(Map<String, Integer> zone){
        int x1 = zone.get("x_start");
        int y1 = zone.get("y_start");
        int x2 = zone.get("x_end");
        int y2 = zone.get("y_end");

        Map<Id<Person>, Coord> zonepersons = new HashMap<>();

        //String configFile = "output_ori050/berlin-v5.5-1pct.output_config.xml";

        //Config config = ConfigUtils.loadConfig(configFile);

        //config.network().setInputFile("./berlin-v5.5-1pct.output_network.xml.gz");

        /*
        String inputpersoncsv = "output_ori050/berlin-v5.5-1pct.output_persons.csv.gz";
        ReadCSV readCSV = new ReadCSV(inputpersoncsv);
        Map<Id<Person>, Coord> allpersons = readCSV.getPersonsHomeMap(inputpersoncsv);
        int counter=0;
        for(Id<Person> personId:allpersons.keySet()){
            Coord pcoord = allpersons.get(personId);
            double home_x = pcoord.getX(), home_y = pcoord.getY();
            if(x1 <= home_x && home_x <= x2){
                if(y1 <= home_y && home_y <= y2){
                    System.out.println("Found one home! Coord: "+pcoord);
                    zonepersons.put(personId,pcoord);
                    counter++;
                }
            }
        }
        System.out.println("total: "+counter);
        System.out.println("### DONE! ###");
        return zonepersons;

         */

        /*

        PopulationReader populationReader = new PopulationReader(scenario);
        ActivityFacilities aFacilities = scenario.getActivityFacilities();
        TreeMap<Id<ActivityFacility>, ActivityFacility> activitytree = aFacilities.getFacilitiesForActivityType("home");
        int counter=0;
        for(Id<ActivityFacility> af_id:activitytree.keySet()){
            Coord af_coord = activitytree.get(af_id).getCoord();
            double af_x = af_coord.getX(), af_y = af_coord.getY();
            if(x1 <= af_x && af_x <= x2){
                if(y1 <= af_y && af_y <= y2){
                    System.out.println("Found one home! Coord: "+af_coord);
                    counter++;
                }
            }
        }
        System.out.println("total: "+counter);
        */

        //Population population = PopulationUtils.readPopulation("output_ori050/berlin-v5.5-1pct.output_plans.xml.gz");
        Population population = scenario.getPopulation();
        Map<Id<Person>, ? extends Person> personmap = population.getPersons();
        for(Id<Person> id:personmap.keySet()){
            Person p = PopulationUtils.findPerson(id, scenario);
            Plan plan = p.getSelectedPlan();
            Activity firstActivity = PopulationUtils.getFirstActivity(plan);
            double x_home = firstActivity.getCoord().getX();
            if(x1 <= x_home && x_home <= x2){
                double y_home = firstActivity.getCoord().getY();
                if(y1 <= y_home && y_home <= y2){
                    zonepersons.put(id,firstActivity.getCoord());
                }
            }
        }
        return zonepersons;

         //*/
    }

    public void modalSplitInZone(Map<Id<Person>, Coord> input){

        Map<String, Integer> modalsplit = new HashMap<>();
        int count_pt = 0, count_car = 0, count_bike = 0, count_walk = 0, count_ride = 0, total = 0;
        modalsplit.put("pt", count_pt);
        modalsplit.put("car", count_car);
        modalsplit.put("bicycle", count_bike);
        modalsplit.put("ride", count_ride);
        modalsplit.put("walk", count_walk);

        for(Id<Person> id:input.keySet()){
            Person p = PopulationUtils.findPerson(id, scenario);
            Plan plan = p.getSelectedPlan();
            /** To see the plan elements of the agents in this zone */
            /*
            List<PlanElement> planElements = plan.getPlanElements();
            System.out.println(planElements);
            System.out.println("#################");
             //*/
            //Activity activity = PopulationUtils.getFirstActivity(plan);
            List<Leg> legList = PopulationUtils.getLegs(plan);
            String mode = legList.get(0).getMode();
            String nextmode = legList.get(1).getMode();
            /** Classic: Count first leg mode */
            /*if(modalsplit.containsKey(mode)){
                modalsplit.put(mode,modalsplit.get(mode)+1);
            }
        // */
            /** Alternative way to count second leg mode, if first is "walk" */
            //*
            if(modalsplit.containsKey(mode)){
                if(mode=="walk"){
                    modalsplit.put(nextmode,modalsplit.get(nextmode)+1);
                }
                else {
                    modalsplit.put(mode, modalsplit.get(mode) + 1);
                }
            }
            total++;

            // */
            /*
            for(Leg leg:legList){
                if(modalsplit.containsKey(leg.getMode())){
                    modalsplit.put(leg.getMode(),modalsplit.get(leg.getMode())+1);
                }
            }
             */
        }
        System.out.println("I count the following legs:");
        for(String s:modalsplit.keySet()){
            double relative = modalsplit.get(s)/total;
            //System.out.println(total);
            //System.out.println(relative);
            System.out.println(s+":\t"+modalsplit.get(s)+"\t("+ relative+" %)");
            //System.out.printf("%s\t%i\t%d", s, modalsplit.get(s), relative);
        }
        System.out.println("### DONE! ###");
    }
}
