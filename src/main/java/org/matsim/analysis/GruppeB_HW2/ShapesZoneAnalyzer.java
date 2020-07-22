package org.matsim.analysis.GruppeB_HW2;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.matsim.core.utils.gis.ShapeFileReader;
import org.opengis.feature.simple.SimpleFeature;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShapesZoneAnalyzer {

    Config config;
    Scenario scenario;
    String shapeFile;

    public ShapesZoneAnalyzer(Config config, Scenario scenario, String shapeFile) {
        this.config = config;
        this.scenario = scenario;
        this.shapeFile = shapeFile;
    }

  /*

        File input =  new File("scenarios/berlin-v5.5-1pct/input/berlin-matsim-v5.5-network.xml.gz");
        String output = "scenarios/berlin-v5.5-1pct/input/shapes-matsim-berlin-network.xml.gz";
        String shapes = "scenarios/berlin-v5.5-1pct/input/BerlinPlanungsraeumeLOR.shp";

        try{
            URL url = new URL("https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/berlin/berlin-v5.5-10pct/input/berlin-v5.5-network.xml.gz");

            FileUtils.copyURLToFile(url, input);
        } catch (IOException e){
            e.printStackTrace();
        }

        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(String.valueOf(input));


        Collection<SimpleFeature> simpleFeatures = (new ShapeFileReader()).readFileAndInitialize(shapes);
        Map<String, Geometry> zones = new HashMap<>();

        for (SimpleFeature simpleFeature: simpleFeatures) {

            zones.put((String) simpleFeature.getAttribute("OBJECTID"), (Geometry) simpleFeature.getDefaultGeometry());
        }*/

    public Map<Id<Person>, Coord> getPersonsHomeInShape(Integer shapeId) {

        Collection<SimpleFeature> simpleFeatures = (new ShapeFileReader()).readFileAndInitialize(shapeFile);
        Map<Integer, Geometry> zones = new HashMap<>();

        for (SimpleFeature simpleFeature : simpleFeatures) {

            zones.put((Integer) simpleFeature.getAttribute("FID"), (Geometry) simpleFeature.getDefaultGeometry());
        }

        Geometry lor = zones.get(shapeId);

        Map<Id<Person>, Coord> zonepersons = new HashMap<>();


        // Population population = PopulationUtils.readPopulation("output_ori050/berlin-v5.5-1pct.output_plans.xml.gz");

        // get ALL persons living in network
        Population population = scenario.getPopulation();
        Map<Id<Person>, ? extends Person> personmap = population.getPersons();

        for (Id<Person> id : personmap.keySet()) {
            Person p = PopulationUtils.findPerson(id, scenario);
            Plan plan = p.getSelectedPlan();
            Activity firstActivity = PopulationUtils.getFirstActivity(plan);

            // get persons in our zone/cell into Map
            double x_home = firstActivity.getCoord().getX();
            double y_home = firstActivity.getCoord().getY();
            Point personPoint = MGC.xy2Point(x_home, y_home);


            if (lor.contains(personPoint)) {
                zonepersons.put(id, firstActivity.getCoord());
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

    public String modalSplitInZone(Map<Id<Person>, Coord> input) {

        Map<String, Integer> modalsplit = new HashMap<>();
        int count_pt = 0, count_car = 0, count_bike = 0, count_walk = 0, count_ride = 0, total = 0;
        modalsplit.put("pt", count_pt);
        modalsplit.put("car", count_car);
        modalsplit.put("bicycle", count_bike);
        modalsplit.put("ride", count_ride);
        modalsplit.put("walk", count_walk);

        String output = "";
        int n = 0; // Counter for LOR, used in printout (line 207)

        for (Id<Person> id : input.keySet()) {
            Person p = PopulationUtils.findPerson(id, scenario);
            Plan plan = p.getSelectedPlan();

            /** To see the plan elements of the agents in this zone, uncomment this */
            /*
            List<PlanElement> planElements = plan.getPlanElements();
            System.out.println(planElements);
            System.out.println("#################");
            //*/

            Activity firstActivity = PopulationUtils.getFirstActivity(plan);
            List<Leg> legList = PopulationUtils.getLegs(plan);
            int legListSize = legList.size();

            if (!legList.isEmpty() && firstActivity.getType() != "freight") {
                String mode = legList.get(legListSize-1).getMode();
                String nextmode = legList.get(1).getMode();
                /** Classic: Count first leg mode */
                /*
                if(modalsplit.containsKey(mode)){
                    modalsplit.put(mode,modalsplit.get(mode)+1);
                }
                // */

                /** Alternative way to count second leg mode, if first is "walk" (-> default setting) */
                //*
                if (mode == "walk" && nextmode != null) {
                    Activity nextactivity = PopulationUtils.getNextActivity(plan, legList.get(0));
                    if (nextactivity.toString().contains("interaction")) {
                        modalsplit.put(nextmode, modalsplit.get(nextmode) + 1);
                    } else {
                        modalsplit.put(mode, modalsplit.get(mode) + 1);
                    }
                } else {
                    modalsplit.put(mode, modalsplit.get(mode) + 1);
                }

                /** Count all legs of this person */
                /*
                for(Leg leg:legList){
                    if(modalsplit.containsKey(leg.getMode())){
                        modalsplit.put(leg.getMode(),modalsplit.get(leg.getMode())+1);
                    }
                }


                 */
                //*/
                total++;
            }
            Activity lastActivity = PopulationUtils.getLastActivity(plan);
            //int legListSize = legList.size();
            if (!legList.isEmpty() && lastActivity.getType() != "freight") {
                String mode = legList.get(legListSize-1).getMode();
                String previousMode = legList.get(legListSize - 2).getMode();
                /** Classic: Count first leg mode */
                /*
                if(modalsplit.containsKey(mode)){
                    modalsplit.put(mode,modalsplit.get(mode)+1);
                }
                // */

                /** Alternative way to count second leg mode, if first is "walk" (-> default setting) */
                //*
                if (mode == "walk" && previousMode != null) {
                    Activity previousActivity = PopulationUtils.getPreviousActivity(plan, legList.get(legListSize-1));
                    if (previousActivity.toString().contains("interaction")) {
                        modalsplit.put(previousMode, modalsplit.get(previousMode) + 1);
                    } else {
                        modalsplit.put(mode, modalsplit.get(mode) + 1);
                    }
                } else {
                    modalsplit.put(mode, modalsplit.get(mode) + 1);
                }
                total++;
            }

            // Print out message with modal split statistics...
            System.out.println("[ "+n+" ] "+"I count the following legs:");
            //String output = "";
            for (String s : modalsplit.keySet()) {
                double relative = Double.valueOf(modalsplit.get(s)) / total;
                System.out.println(s + ":\t" + modalsplit.get(s) + "\t(" + relative * 100 + " %)");
                output += (s + ":\t" + modalsplit.get(s) + "\t(" + relative * 100 + " %)" + "\n");
            }
            System.out.println("  ");
            n++;
            //return output;
        }
        System.out.println("### DONE! ###");
        return output;
    }
}






