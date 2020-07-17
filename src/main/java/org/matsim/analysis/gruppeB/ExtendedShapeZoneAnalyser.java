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
        int count_pt = 0, count_car = 0, count_bike = 0, count_walk = 0, count_ride = 0, total_count = 0;
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
            List<Activity> activityListOfPersonInRegion = input.get(id);
            System.out.println("Activities in Zone: " + activityListOfPersonInRegion.size() + " " + id);
            System.out.println(activityListOfPersonInRegion);
            List<Leg> personsLegsList = PopulationUtils.getLegs(plan);
            List<Leg> legsAlreadyAnalysed = new ArrayList<>();
            List<PlanElement> personPlanElements = plan.getPlanElements();

            int planElementsSize = personPlanElements.size();
            int legListSize = personsLegsList.size();

            for (Activity activityInShape : activityListOfPersonInRegion) {

                //Code for the first activity of the day, person should have legs to be analysed, because some persons stay at home
                if (personPlanElements.get(0) instanceof Activity && !personsLegsList.isEmpty()) {
                    Activity personsFirstActivity = (Activity) personPlanElements.get(0);

                    if (personsFirstActivity.toString().equals(activityInShape.toString())) {
                        total_count++;


                        String mode = personsLegsList.get(0).getMode();
                        String nextmode = personsLegsList.get(1).getMode();

                        Activity nextactivity = PopulationUtils.getNextActivity(plan, personsLegsList.get(0));

                        //checking that after the activity is an interaction activity, than the leg after the interaction is anlysed
                        if (nextactivity.toString().contains("interaction")) {
                            modalsplit.put(nextmode, modalsplit.get(nextmode) + 1);
                            legsAlreadyAnalysed.add(personsLegsList.get(1));
                            // continue;

                            // code is executed when the next activity is a regular activity, meaning that persons walks or cycles
                        } else {
                            modalsplit.put(mode, modalsplit.get(mode) + 1);
                            legsAlreadyAnalysed.add(personsLegsList.get(0));
                            //continue;

                        }

                    }
                }

                //Analysing persons last activities , persons LegList can be empty in case person does not leave their homes
                if (personPlanElements.get(planElementsSize - 1) instanceof Activity && !personsLegsList.isEmpty()) {
                    Activity personsLastActivity = (Activity) personPlanElements.get(planElementsSize - 1);

                    if (personsLastActivity.toString().equals(activityInShape.toString())) {

                        String mode = personsLegsList.get(legListSize - 1).getMode();
                        String previousMode = personsLegsList.get(legListSize - 2).getMode();

                        Activity previousActivity = PopulationUtils.getPreviousActivity(plan, personsLegsList.get(legListSize - 1));

                        //if previousActivity is an interaction, we analyse the leg before
                        if (previousActivity.toString().contains("interaction") && !legsAlreadyAnalysed.contains(personsLegsList.get(legListSize - 2))) {
                            modalsplit.put(previousMode, modalsplit.get(previousMode) + 1);
                            legsAlreadyAnalysed.add(personsLegsList.get(legListSize - 2));
                            total_count++;
                            //             continue;

                            // if previousActivity is regular, the last leg is analysed
                        } else if (!previousActivity.toString().contains("interaction") && !legsAlreadyAnalysed.contains(personsLegsList.get(legListSize - 1))) {
                            modalsplit.put(mode, modalsplit.get(mode) + 1);
                            legsAlreadyAnalysed.add(personsLegsList.get(legListSize - 1));
                            total_count++;
                            //      continue;


                        }


                    }
                }

                /**Activity in Region with leg before and after*/
                int index = personPlanElements.indexOf(activityInShape);
                System.out.println(index);

                //check that not last or first activity are analysed
                if (index != 0 && index != planElementsSize - 1) {

               //cast to Activity is possible because of line 195
                    Activity personsWithinActivity = (Activity) personPlanElements.get(index);



                    int previousLegIndex = personsLegsList.indexOf(PopulationUtils.getPreviousLeg(plan, personsWithinActivity));

                    String previousMode = PopulationUtils.getPreviousLeg(plan, personsWithinActivity).getMode();

                    //if person is walking or cycling into the region there is no previousPreviousMode
                    String previousPreviousMode = null;
                    if (previousLegIndex != 0) {
                        previousPreviousMode = personsLegsList.get(previousLegIndex - 1).getMode();

                    }

                    int nextLegIndex = personsLegsList.indexOf(PopulationUtils.getNextLeg(plan, personsWithinActivity));
                    String nextMode = PopulationUtils.getNextLeg(plan, personsWithinActivity).getMode();

                    //if person is walking or cycling out of the region there is no nextNExtMode

                    String nextNextMode = null;
                    if (nextLegIndex != personsLegsList.size() - 1) {
                        nextNextMode = personsLegsList.get(nextLegIndex + 1).getMode();

                    }

                    //previousMode will always be walk or bicycle and there is a previouspreviousMode (
                    if (/*(previousMode == "walk" || previousMode == "bicycle") &&*/ previousPreviousMode != null) {
                        Activity previousActivity = PopulationUtils.getPreviousActivity(plan, personsLegsList.get(previousLegIndex));

                        //if the previousActivity is an interaction, the leg before this interaction (previousprevious) is analysed
                        if (previousActivity.toString().contains("interaction") && !legsAlreadyAnalysed.contains(personsLegsList.get(previousLegIndex - 1))) {
                            modalsplit.put(previousPreviousMode, modalsplit.get(previousPreviousMode) + 1);
                            legsAlreadyAnalysed.add(personsLegsList.get(previousLegIndex - 1));
                            total_count++;


                            //if previous Activity is a regular activity the previousLeg is analysed (previousPrevious is without interest)
                        } else if (!previousActivity.toString().contains("interaction") && !legsAlreadyAnalysed.contains(personsLegsList.get(previousLegIndex))) {
                            modalsplit.put(previousMode, modalsplit.get(previousMode) + 1);
                            legsAlreadyAnalysed.add(personsLegsList.get(previousLegIndex));
                            total_count++;

                        }


                        // we get here, when a person starts with a walk or bicycle directly in our region
                    } else if (!legsAlreadyAnalysed.contains(personsLegsList.get(previousLegIndex))) {
                        modalsplit.put(previousMode, modalsplit.get(previousMode) + 1);
                        legsAlreadyAnalysed.add(personsLegsList.get(previousLegIndex));
                        total_count++;

                    }
                    //checking there is a nextNextMode
                    if (/*(nextMode == "walk" || nextMode == "bicycle") && */nextNextMode != null) {
                        Activity nextActivity = PopulationUtils.getNextActivity(plan, personsLegsList.get(nextLegIndex));

                        //if the nextActivity is interaction, the leg after the interactiojn (nextnext) is analysed
                        if (nextActivity.toString().contains("interaction") && !legsAlreadyAnalysed.contains(personsLegsList.get(nextLegIndex + 1))) {
                            modalsplit.put(nextNextMode, modalsplit.get(nextNextMode) + 1);
                            legsAlreadyAnalysed.add(personsLegsList.get(nextLegIndex + 1));
                            total_count++;

                            //if next Activity is a regular, the nextLeg is anlysed (nextNext can be ignored)
                        } else if (!nextActivity.toString().contains("interaction") && !legsAlreadyAnalysed.contains(personsLegsList.get(nextLegIndex))) {
                            modalsplit.put(nextMode, modalsplit.get(nextMode) + 1);
                            legsAlreadyAnalysed.add(personsLegsList.get(nextLegIndex));
                            total_count++;

                        }

                        //we get here, when person walks or cycles directly home
                    } else if (!legsAlreadyAnalysed.contains(personsLegsList.get(nextLegIndex))) {
                        modalsplit.put(nextMode, modalsplit.get(nextMode) + 1);
                        legsAlreadyAnalysed.add(personsLegsList.get(nextLegIndex));
                        total_count++;

                    }

                }
                // }
                //  }

                System.out.println("Legs counted: " + legsAlreadyAnalysed.size());
                System.out.println(legsAlreadyAnalysed);
            }

        }
        System.out.println("[ " + n + " ] " + "I count the following legs:");
        //String output = "";
        for (String s : modalsplit.keySet()) {
            double relative = Double.valueOf(modalsplit.get(s)) / total_count;
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
