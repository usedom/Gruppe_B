package org.matsim.analysis.gruppeB;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.PersonLeavesVehicleEvent;
import org.matsim.api.core.v01.events.handler.PersonLeavesVehicleEventHandler;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Plan;
import org.matsim.core.population.PopulationUtils;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeAnalyser implements PersonLeavesVehicleEventHandler {
    private BufferedWriter bufferedWriter;

    private List<LinkEnterEvent> kmaEvents;
    private List<PersonEntersVehicleEvent> personEntersVehicleEventsWhichUsedKMA;
    private List<PersonLeavesVehicleEvent> personLeavesVehicleEventsWhichUsedKMA = new ArrayList<>();
    Map<Id<Person>, Activity> firstActivities = new HashMap<>();

    public HomeAnalyser(List<LinkEnterEvent> listOfLinkEnterEvents, List<PersonEntersVehicleEvent> personEntersVehicleEvents, String outputfile){
        kmaEvents = listOfLinkEnterEvents;
        personEntersVehicleEventsWhichUsedKMA = personEntersVehicleEvents;


        try {
            FileWriter fileWriter = new FileWriter(outputfile);
            bufferedWriter = new BufferedWriter(fileWriter);
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


        for (PersonLeavesVehicleEvent leaveEvent: personLeavesVehicleEventsWhichUsedKMA) {
            Id<Person> personLeftVehicle = leaveEvent.getPersonId();

            for(PersonEntersVehicleEvent entersVehicleEvent : personEntersVehicleEventsWhichUsedKMA){

                if(entersVehicleEvent.getPersonId().compareTo(personLeftVehicle) == 0){

                    Person p = PopulationUtils.findPerson(personLeftVehicle, scenario);
                    Plan plan = p.getSelectedPlan();
                    Activity firstActivity = PopulationUtils.getFirstActivity(plan);
                    firstActivities.put(personLeftVehicle, firstActivity);
                }
                
            }

        }
    }
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
}
