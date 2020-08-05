package org.matsim.analysis.GruppeB_HW2;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.AgentWaitingForPtEvent;
import org.matsim.core.api.experimental.events.handler.AgentWaitingForPtEventHandler;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TramStopAnalyzer implements PersonEntersVehicleEventHandler, AgentWaitingForPtEventHandler {
   /*Je nachdem welchen Input ich gebe, soll der Tramstopanalyzer eine txt. Datei ausgeben, in dem drin steht wie viele
     Personen an welcher Haltestelle eingestiegen sind.*/
    private BufferedWriter bufferedWriter;

    Scenario scenario;

    List<Id<TransitRoute>> tramM10routeid = new ArrayList<>();
    Set<Id<TransitStopFacility>> M10Haltestellen = new HashSet();
    List<Id<TransitStopFacility>> listM10Haltestellen = new ArrayList<>();
    List<Integer> checkRoutelaenge = new ArrayList<>();
    List<Id<Person>> listpersonentersM10 = new ArrayList<>();
    Set<Id<Person>> setpersonentersM10 = new HashSet<>();
    Map<Id<TransitStopFacility>, Integer> auslastungM10 = new HashMap<>();
    List<String> stringlist = new ArrayList<>();
    Integer count_her = 0;
    Integer count_pfl = 0;
    Integer count_gor = 0;
    Integer count_fal = 0;



    public TramStopAnalyzer(Scenario scenario, String output){
        this.scenario = scenario;
        List<Id<TransitRoute>> trlineM10 = new ArrayList<>(scenario.getTransitSchedule().getTransitLines().get(Id.create("M10---17440_900", TransitLine.class)).getRoutes().keySet());

       //Haltestellen nodes der M10 finden
       // scenario.getTransitSchedule().getTransitLines().get(Id.create("M10---17440_900", TransitLine.class)).getRoutes().get().getStop().getStopFacility();

       for (int i = 0; i < trlineM10.size(); i++){
            if (String.valueOf(trlineM10.get(i)).contains("M10")){
                tramM10routeid.add(trlineM10.get(i));
            }
        }

       for (Id<TransitRoute> id:tramM10routeid){
           int routelaenge = scenario.getTransitSchedule().getTransitLines().get(Id.create("M10---17440_900", TransitLine.class)).getRoutes().get(id).getStops().size();
            checkRoutelaenge.add(routelaenge);
           for (int i = 0; i < routelaenge; i++){
               M10Haltestellen.add(scenario.getTransitSchedule().getTransitLines().get(Id.create("M10---17440_900", TransitLine.class)).getRoutes().get(id).getStops().get(i).getStopFacility().getId());
               stringlist.add(scenario.getTransitSchedule().getTransitLines().get(Id.create("M10---17440_900", TransitLine.class)).getRoutes().get(id).getStops().get(i).getStopFacility().getId().toString());
           }
       }

       try {
            FileWriter fileWriter = new FileWriter(output);
            bufferedWriter = new BufferedWriter(fileWriter);
        } catch(IOException ee){
            throw new RuntimeException(ee);
        }
    }

    @Override
    public void handleEvent(PersonEntersVehicleEvent person_enters_tram){
        Set<Id<Person>> allpersonidsveh = scenario.getPopulation().getPersons().keySet();
        for (Id<Person> id:allpersonidsveh){
            if (person_enters_tram.getPersonId().equals(id)){
                if (!String.valueOf(person_enters_tram.getPersonId()).contains("pt_pt")){
                    if (String.valueOf(person_enters_tram.getVehicleId()).contains("pt_M10")){
                        setpersonentersM10.add(person_enters_tram.getPersonId());
                        listpersonentersM10.add(person_enters_tram.getPersonId());
                    }
                }
            }
        }
    }

    @Override
    public void handleEvent(AgentWaitingForPtEvent person_wartet_Haltestelle){
     /*   for (Id<TransitStopFacility> id:M10Haltestellen) {
           if (person_wartet_Haltestelle.getWaitingAtStopId().equals(id)) {
               if (auslastungM10.keySet().contains(id)){
                   auslastungM10.put(id,auslastungM10.get(id)+1);
               }
               if (!auslastungM10.keySet().contains(id)){
                   auslastungM10.put(id,1);
               }
           }
       }*/
        for (Id<TransitStopFacility> id:M10Haltestellen) {
            if (person_wartet_Haltestelle.getWaitingAtStopId().equals(id)) {
                if (id.equals(Id.create("070101004244.9", TransitStopFacility.class)) || id.equals(Id.create("070101004244.8", TransitStopFacility.class))){
                    //Hermannplatz
                    count_her=count_her+1;
                }
                if (id.equals(Id.create("070101003213.9", TransitStopFacility.class)) || id.equals(Id.create("070101003213.8", TransitStopFacility.class))){
                    //Pflüger str
                    count_pfl=count_pfl+1;
                }
                if (id.equals(Id.create("07010100newpark.8", TransitStopFacility.class)) || id.equals(Id.create("07010100newpark.9", TransitStopFacility.class))){
                    //Görlitzer Park
                    count_gor=count_gor+1;
                }
                if (id.equals(Id.create("070101001365.8", TransitStopFacility.class)) || id.equals(Id.create("070101001365.9", TransitStopFacility.class))){
                    //Falckensteiner Str.
                    count_fal=count_fal+1;
                }

            }
        }


    }

    public void print(){
        List<Id<Person>> person_entering = new ArrayList<>(setpersonentersM10);
        /*In der Text datei wird der Haltestellen Name, die Anzahl der Personen die einsteigen sowie die Anzahl an
        Fahrten die gemacht werden, und die Anzahl an Personen, die diese Fahrten machen*/
        System.out.println("###WICHTIGER PART###");
        System.out.println("Anzahl an Fahrgästen M10 " +setpersonentersM10.size() + " Anzahl an Fahrten" + listpersonentersM10.size());
        System.out.println("Einstieg Hermannplatz " + count_her );
        System.out.println("Einstieg Pflügerstr. " + count_pfl );
        System.out.println("Einstieg Görtlitzer Park " + count_gor );
        System.out.println("Einstieg Falckensteinstr. " + count_fal );
        System.out.println("### DONE ###");

      try {
        /* bufferedWriter.write("Anzahl an Fahrgästen M10 " +setpersonentersM10.size() + " Anzahl an Fahrten" + listpersonentersM10.size());
         bufferedWriter.newLine();
         bufferedWriter.write("neue Haltestellen Auslastung" );
         bufferedWriter.newLine();
         bufferedWriter.write("Einstieg Hermannplatz " + count_her );
         bufferedWriter.newLine();
         bufferedWriter.write("Einstieg Pflügerstr. " + count_pfl );
         bufferedWriter.newLine();
         bufferedWriter.write("Einstieg Görtlitzer Park " + count_gor );
         bufferedWriter.newLine();
         bufferedWriter.write("Einstieg Falckensteinstr. " + count_fal );
         bufferedWriter.newLine();*/
         for(int i=0; i<person_entering.size(); i++){
             bufferedWriter.write(String.valueOf(person_entering.get(i)));
             bufferedWriter.newLine();
         }

         bufferedWriter.close();
        } catch (IOException ee){
            throw new RuntimeException(ee);
        }
    }
}
