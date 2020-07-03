package org.matsim.analysis.gruppeB;

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

        // Population population = PopulationUtils.readPopulation("output_ori050/berlin-v5.5-1pct.output_plans.xml.gz");

        // get ALL persons living in network
        Population population = scenario.getPopulation();
        Map<Id<Person>, ? extends Person> personmap = population.getPersons();
        for(Id<Person> id:personmap.keySet()){
            Person p = PopulationUtils.findPerson(id, scenario);
            Plan plan = p.getSelectedPlan();
            Activity firstActivity = PopulationUtils.getFirstActivity(plan);

            // get persons in our zone/cell into Map
            double x_home = firstActivity.getCoord().getX();
            if(x1 <= x_home && x_home <= x2){
                double y_home = firstActivity.getCoord().getY();
                if(y1 <= y_home && y_home <= y2){
                    zonepersons.put(id,firstActivity.getCoord());
                }
            }
        }
        return zonepersons;
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

            /** To see the plan elements of the agents in this zone, uncomment this */
            /*
            List<PlanElement> planElements = plan.getPlanElements();
            System.out.println(planElements);
            System.out.println("#################");
            //*/

            Activity activity = PopulationUtils.getFirstActivity(plan);
            List<Leg> legList = PopulationUtils.getLegs(plan);
            if(legList.isEmpty() == false && activity.getType()!="freight"){
                String mode = legList.get(0).getMode();
                String nextmode = legList.get(1).getMode();
                /** Classic: Count first leg mode */
                /*
                if(modalsplit.containsKey(mode)){
                    modalsplit.put(mode,modalsplit.get(mode)+1);
                }
                // */

                /** Alternative way to count second leg mode, if first is "walk" (-> default setting) */
                //*
                if (modalsplit.containsKey(mode)) {
                    if (mode == "walk" && nextmode!=null) {
                        Activity nextactivity = PopulationUtils.getNextActivity(plan, legList.get(0));
                        if(nextactivity.toString().contains("interaction")){
                            modalsplit.put(nextmode, modalsplit.get(nextmode) + 1);
                        }
                        else {
                            modalsplit.put(mode, modalsplit.get(mode) + 1);
                        }
                    } else {
                        modalsplit.put(mode, modalsplit.get(mode) + 1);
                    }
                }
                //*/

                /** Count all legs of this person */
                /*
                for(Leg leg:legList){
                    if(modalsplit.containsKey(leg.getMode())){
                        modalsplit.put(leg.getMode(),modalsplit.get(leg.getMode())+1);
                    }
                }
                //*/
                total++;
            }
        }

        // Print out message with modal split statistics...
        System.out.println("I count the following legs:");
        for(String s:modalsplit.keySet()){
            double relative = Double.valueOf(modalsplit.get(s))/total;
            System.out.println(s+":\t"+modalsplit.get(s)+"\t("+ relative*100 +" %)");
        }
        System.out.println("### DONE! ###");
    }
}
