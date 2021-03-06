package org.matsim.analysis.gruppeB;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.PersonLeavesVehicleEvent;
import org.matsim.api.core.v01.events.handler.PersonLeavesVehicleEventHandler;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.vehicles.Vehicle;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeAnalyser implements PersonLeavesVehicleEventHandler {

    private BufferedWriter bufferedWriter;
    private BufferedWriter bufferedWriter_csv;  // new BufferedWriter for csv-file

    private List<LinkEnterEvent> kmaEvents;
    private List<PersonEntersVehicleEvent> personEntersVehicleEventsWhichUsedKMA;
    private List<PersonLeavesVehicleEvent> personLeavesVehicleEventsWhichUsedKMA = new ArrayList<>();
    Map<Id<Person>, Activity> firstActivities = new HashMap<>();

    Map<Double, Id<Vehicle>> vehiclemap;
    List<Double> timelist;
    Map<Double, Id<Person>> personmap = new HashMap<>();
    List<Double> x_homes = new ArrayList<>();
    List<Double> y_homes = new ArrayList<>();
    /*List<Double> WalkingTime = new ArrayList<>();
    List<Double> CarTime = new ArrayList<>();
    List<Double> ptTime= new ArrayList<>();
    List<Double> BicycleTime = new ArrayList<>();*/

    List<PlanElement> planElements = new ArrayList<>();
    List<TripStructureUtils.Trip> tripList = new ArrayList<>();


    public HomeAnalyser(List<LinkEnterEvent> listOfLinkEnterEvents, List<PersonEntersVehicleEvent> personEntersVehicleEvents, String outputfile, String outputfile_CSV, Map<Double, Id<Vehicle>> vehiclemap, List<Double> timelist){
        kmaEvents = listOfLinkEnterEvents;
        personEntersVehicleEventsWhichUsedKMA = personEntersVehicleEvents;
        /* Get "Time, VehicleID"-Map and "Time"-List offered by BerlinKmaCounter */
        this.vehiclemap = vehiclemap;
        this.timelist = timelist;

        try {
            FileWriter fileWriter = new FileWriter(outputfile);
            FileWriter fileWriter_csv = new FileWriter(outputfile_CSV);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter_csv = new BufferedWriter(fileWriter_csv);
        } catch(IOException ee){
            throw new RuntimeException(ee);
        }
    }

    @Override
    public void handleEvent(PersonLeavesVehicleEvent personLeavesVehicleEvent) {
        for (LinkEnterEvent kmaEvent: kmaEvents) {
           if(kmaEvent.getVehicleId().compareTo(personLeavesVehicleEvent.getVehicleId()) == 0 && kmaEvent.getTime() < personLeavesVehicleEvent.getTime()){
              personLeavesVehicleEventsWhichUsedKMA.add(personLeavesVehicleEvent);

           }
        }
    }

    public void analyseHome(Scenario scenario){
        int person_it = 0;
        for (PersonLeavesVehicleEvent leaveEvent: personLeavesVehicleEventsWhichUsedKMA) {

            /** (1) Used for Classic printResult */
            Id<Person> personLeftVehicle = leaveEvent.getPersonId();
            for(PersonEntersVehicleEvent entersVehicleEvent : personEntersVehicleEventsWhichUsedKMA){

                if(entersVehicleEvent.getPersonId().compareTo(personLeftVehicle) == 0){

                    Person p = PopulationUtils.findPerson(personLeftVehicle, scenario);
                    Plan plan = p.getSelectedPlan();

                    //Get home coordinates
                    tripList = TripStructureUtils.getTrips(plan);
                    System.out.println("Person: " + person_it++);
                    //planElements = plan.getPlanElements();
                    for(int i=0; i<tripList.size(); i++){
                        //List<Leg> FirstLeg = tripList.get(i).getLegsOnly();
                        //System.out.println("Legs" + FirstLeg);
                        Activity startactivity = tripList.get(i).getOriginActivity();
                        System.out.println("Start-Plan: " + tripList.get(i) + "\t" + "Coord: " + startactivity.getCoord());
                        Activity endactivity = tripList.get(i).getDestinationActivity();
                        System.out.println("End-Plan: " + tripList.get(i) + "\t" + "Coord: " + endactivity.getCoord());
                    }
                    System.out.println("############################################\n\n");

                    Activity firstActivity = PopulationUtils.getFirstActivity(plan);
                    firstActivities.put(personLeftVehicle, firstActivity);
                }
            }

            /** (2)+(3) Used for chronological printResult*/
            int it = 0;
            for(double d:timelist){
                Id<Vehicle> lookupvehicle = vehiclemap.get(d);
                if(lookupvehicle == leaveEvent.getVehicleId()){
                    personmap.put(d, leaveEvent.getPersonId());
                    Person p = PopulationUtils.findPerson(personLeftVehicle, scenario);
                    Plan plan = p.getSelectedPlan();
                    Activity firstActivity = PopulationUtils.getFirstActivity(plan);
                    double x_home = firstActivity.getCoord().getX();
                    double y_home = firstActivity.getCoord().getY();
                    x_homes.add(it, x_home);
                    y_homes.add(it++, y_home);
                }
            }


        }
    }

    /** (1) Classic printResult */
    public void printResult() {
        try {
            bufferedWriter.write("Person ID\t HomeLink\t HomeCoord");
            for (Id<Person> personId : firstActivities.keySet()) {

                bufferedWriter.write("\n" + personId + "\t" + firstActivities.get(personId).getLinkId() + "\t" + firstActivities.get(personId).getCoord());

            }
            bufferedWriter.close();
        } catch (IOException ee){
            throw new RuntimeException(ee);
        }
    }

    /** (2) Chronological printResult in .txt */
    public void printResult_v2() {
        try {
            bufferedWriter.write("Time\t\tPerson ID\t\tHome X\t\t\tHome Y");
            System.out.println("Time\t\tPerson ID\t\tHome X\t\t\tHome Y");
            int it =0;
            for (Double d:timelist) {
                System.out.println(d + "\t\t" + personmap.get(d) + "\t\t" + x_homes.get(it) + "\t" + y_homes.get(it++));
                bufferedWriter.newLine();
                bufferedWriter.write(d + "\t\t" + personmap.get(d) + "\t\t" + x_homes.get(it) + "\t" + y_homes.get(it++));
            }
            bufferedWriter.close();
        } catch (IOException ee){
            throw new RuntimeException(ee);
        }
    }

    /** (3) Chronological printResult in .csv */
    public void printResult_v2_csv() {
        try {
            bufferedWriter_csv.write("Time;Person ID;Home X;Home Y");
            int it =0;
            for (Double d:timelist) {
                bufferedWriter_csv.newLine();
                bufferedWriter_csv.write(d + ";" + personmap.get(d) + ";" + x_homes.get(it) + ";" + y_homes.get(it++));
            }
            bufferedWriter_csv.close();
        } catch (IOException ee){
            throw new RuntimeException(ee);
        }
    }
}
