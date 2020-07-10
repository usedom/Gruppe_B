package org.matsim.analysis.gruppeB;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.util.*;

public class ExtendedShapeZoneAnalyser {

    Config config;
    Scenario scenario;
    String shapeFile;

    public ExtendedShapeZoneAnalyser(Config config, Scenario scenario, String shapeFile) {
        this.config = config;
        this.scenario = scenario;
        this.shapeFile = shapeFile;
    }

    /**
     * Returns Map of Persons Ids and List of Activity, if an activity if within a given shape.
     **/
    public Map<Id<Person>, List<Activity>> getPersonsWithActivityInShape(Integer shapeId) {

        Collection<SimpleFeature> simpleFeatures = (new ShapeFileReader()).readFileAndInitialize(shapeFile);
        Map<Integer, Geometry> zones = new HashMap<>();

        for (SimpleFeature simpleFeature : simpleFeatures) {

            zones.put((Integer) simpleFeature.getAttribute("FID"), (Geometry) simpleFeature.getDefaultGeometry());
        }

        Geometry lor = zones.get(shapeId);

        Map<Id<Person>, List<Activity>> zonepersons = new HashMap<>();


        // get ALL persons living in network
        Population population = scenario.getPopulation();
        Map<Id<Person>, ? extends Person> personmap = population.getPersons();

        for (Id<Person> id : personmap.keySet()) {
            Person p = PopulationUtils.findPerson(id, scenario);
            Plan plan = p.getSelectedPlan();

            List<PlanElement> planElements = plan.getPlanElements();

            //
            for (PlanElement planElement : planElements) {
                //only Activity of non freight and interaction type
                if (planElement instanceof Activity && !planElement.toString().contains("interaction") && (!((Activity) planElement).getType().equals("freight"))) {
                    Activity potentialInShapeActivity = (Activity) planElement;

                    double x_home = potentialInShapeActivity.getCoord().getX();
                    double y_home = potentialInShapeActivity.getCoord().getY();
                    Point personPoint = MGC.xy2Point(x_home, y_home);


                    if (lor.contains(personPoint)) {
                        if (zonepersons.containsKey(id)) {
                            List<Activity> activityList = zonepersons.get(id);
                            activityList.add(potentialInShapeActivity);
                            zonepersons.put(id, activityList);
                        } else {
                            List<Activity> activityList = new ArrayList<>();
                            activityList.add(potentialInShapeActivity);
                            zonepersons.put(id, activityList);
                        }
                    }
                }
            }
            // get persons in our zone/cell into Map


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

    public String modalSplitInZone(Map<Id<Person>, List<Activity>> input) {

        Map<String, Integer> modalsplit = new HashMap<>();
        int count_pt = 0, count_car = 0, count_bike = 0, count_walk = 0, count_ride = 0, total = 0;
        modalsplit.put("pt", count_pt);
        modalsplit.put("car", count_car);
        modalsplit.put("bicycle", count_bike);
        modalsplit.put("ride", count_ride);
        modalsplit.put("walk", count_walk);

        int n = 0;
        String output = "";

        for (Id<Person> id : input.keySet()) {
            Person p = PopulationUtils.findPerson(id, scenario);
            Plan plan = p.getSelectedPlan();
            List<Activity> activityListInShape = input.get(id);
            List<Leg> personsLegsList = PopulationUtils.getLegs(plan);
            List<PlanElement> personPlanElements = plan.getPlanElements();

            int planElementsSize = personPlanElements.size();
            int legListSize = personsLegsList.size();



            for (Activity activityInShape : activityListInShape) {

                //Code for the first activity of the day
                if (personPlanElements.get(0) instanceof Activity && !personsLegsList.isEmpty()) {
                    Activity personsFirstActivity = (Activity) personPlanElements.get(0);
                    Activity p1Activity = PopulationUtils.getFirstActivity(plan);

                    if (personsFirstActivity.toString().equals(activityInShape.toString())) {
                        total++;

                        String mode = personsLegsList.get(0).getMode();
                        String nextmode = personsLegsList.get(1).getMode();

                        if (mode == "walk" && nextmode != null) {
                            Activity nextactivity = PopulationUtils.getNextActivity(plan, personsLegsList.get(0));
                            if (nextactivity.toString().contains("interaction")) {
                                modalsplit.put(nextmode, modalsplit.get(nextmode) + 1);
                            } else {
                                modalsplit.put(mode, modalsplit.get(mode) + 1);
                            }
                        } else {
                            modalsplit.put(mode, modalsplit.get(mode) + 1);
                        }

                    }
                }

               if (personPlanElements.get(planElementsSize-1) instanceof Activity && !personsLegsList.isEmpty()) {
                    Activity personsLastActivity = (Activity) personPlanElements.get(planElementsSize-1);

                    if (personsLastActivity.toString().equals(activityInShape.toString())) {
                        total++;

                        String mode = personsLegsList.get(legListSize-1).getMode();
                        String previousMode = personsLegsList.get(legListSize-2).getMode();

                        if (mode == "walk" && previousMode != null) {
                            Activity previousActivity = PopulationUtils.getPreviousActivity(plan, personsLegsList.get(legListSize-1));
                            if (previousActivity.toString().contains("interaction")) {
                                modalsplit.put(previousMode, modalsplit.get(previousMode) + 1);
                            } else {
                                modalsplit.put(mode, modalsplit.get(mode) + 1);
                            }
                        } else {
                            modalsplit.put(mode, modalsplit.get(mode) + 1);
                        }

                    }
                }

               /* int index = personPlanElements.indexOf(activityInShape);

                if (personPlanElements.get(index) instanceof Activity && !personsLegsList.isEmpty()) {
                    Activity personsActivity = (Activity) personPlanElements.get(index);

                    if (personsActivity.toString().equals(activityInShape.toString())) {
                        total++;

                        String mode = personsLegsList.get(legListSize-1).getMode();
                        String previousMode = personsLegsList.get(legListSize-2).getMode();

                        if (mode == "walk" && previousMode != null) {
                            Activity previousActivity = PopulationUtils.getPreviousActivity(plan, personsLegsList.get(legListSize-1));
                            if (previousActivity.toString().contains("interaction")) {
                                modalsplit.put(previousMode, modalsplit.get(previousMode) + 1);
                            } else {
                                modalsplit.put(mode, modalsplit.get(mode) + 1);
                            }
                        } else {
                            modalsplit.put(mode, modalsplit.get(mode) + 1);
                        }

                    }
                }*/

            }

        }
        System.out.println("[ " + n + " ] " + "I count the following legs:");
        //String output = "";
        for (String s : modalsplit.keySet()) {
            double relative = Double.valueOf(modalsplit.get(s)) / total;
            System.out.println(s + ":\t" + modalsplit.get(s) + "\t(" + relative * 100 + " %)");
            output += (s + ":\t" + modalsplit.get(s) + "\t(" + relative * 100 + " %)" + "\n");
        }
        System.out.println("  ");
        n++;
        //return output;

        System.out.println("### DONE! ###");
        return output;


    }
}
