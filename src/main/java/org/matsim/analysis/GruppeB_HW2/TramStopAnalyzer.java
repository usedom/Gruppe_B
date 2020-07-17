package org.matsim.analysis.GruppeB_HW2;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.PersonDepartureEvent;
import org.matsim.api.core.v01.events.PersonEntersVehicleEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.api.core.v01.events.handler.PersonDepartureEventHandler;
import org.matsim.api.core.v01.events.handler.PersonEntersVehicleEventHandler;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.experimental.events.AgentWaitingForPtEvent;
import org.matsim.core.api.experimental.events.handler.AgentWaitingForPtEventHandler;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;
import org.matsim.vehicles.Vehicle;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class TramStopAnalyzer implements PersonEntersVehicleEventHandler, LinkEnterEventHandler, PersonDepartureEventHandler, AgentWaitingForPtEventHandler {
   /*Je nachdem welchen Input ich gebe, soll der Tramstopanalyzer eine txt. Datei ausgeben, in dem drin steht wie viele
     Personen an welcher Haltestelle eingestiegen sind. Vllt auch nochmal zeitlicher Verlauf für den Tag*/
    private BufferedWriter bufferedWriter;

    List<Id<Link>> linksM10= new ArrayList<>();
    List<Id<Vehicle>> vehiclesM10= new ArrayList<>();
    List<Id<Person>> FahrgastTram = new ArrayList<>();
    List<Id<Person>> FahrerTram = new ArrayList<>();
    String expVehLW = "pt_M10---17440_900_14_29";
    String expVehWL = "pt_M10---17440_900_7_31";
    List<Id<Link>> tramstops = new ArrayList<>();
    Map<Id<Link>, Integer> auslastung_Haltestellen = new HashMap<>();
    Integer count = 0;
    Map<Id<Person>,Id<Link>> test =new HashMap<>();
    List<Id<Vehicle>> tram10veh = new ArrayList<>();
    List<Id<TransitStopFacility>> testStops = new ArrayList<>() ;
    List<Id<Person>> singlePerson = new ArrayList<>();

    public TramStopAnalyzer(String output, List<Id<Vehicle>> fahrzeuge){
        tram10veh.addAll(fahrzeuge);
        try {
            FileWriter fileWriter = new FileWriter(output);
            bufferedWriter = new BufferedWriter(fileWriter);
        } catch(IOException ee){
            throw new RuntimeException(ee);
        }
    }
    //UNNÖTIG! KANN WEG!
    @Override
    public void handleEvent(LinkEnterEvent vehicle_enters_link){
       if (vehicle_enters_link.getVehicleId().equals(Id.create(expVehLW,expVehLW.getClass()))||vehicle_enters_link.getVehicleId().equals(Id.create(expVehWL,expVehWL.getClass()))) {
            linksM10.add(vehicle_enters_link.getLinkId());
            auslastung_Haltestellen.put(vehicle_enters_link.getLinkId(),0);
        }
        //Holt die dazugehörigen Fahrzeuge raus, ABER das sind noch alle pt auf dem link
       for (Id<Link> linkid:linksM10) {
            if (vehicle_enters_link.getLinkId().equals(linkid)) {
                if (!vehiclesM10.contains(vehicle_enters_link.getVehicleId())) {
                    //Aus der Liste mit allen pt für die links, werden die pt_M10 Einträge sortiert
                   if (String.valueOf(vehicle_enters_link.getVehicleId()).contains("pt_M10")) {
                        vehiclesM10.add(vehicle_enters_link.getVehicleId());
                   }
                }
            }
       }
    }

    @Override
    public void handleEvent(PersonEntersVehicleEvent person_enters_tram){

        for(Id<Vehicle> vehid:tram10veh){
            if (person_enters_tram.getVehicleId().compareTo(vehid)==0){
                if (!String.valueOf(person_enters_tram.getPersonId()).contains("pt_pt")){
                FahrgastTram.add(person_enters_tram.getPersonId());
                } else {
                    //Die Liste ist quasi unnötig weil es für jedes Fahreug quasi einen Fahrer gibt
                    FahrerTram.add(person_enters_tram.getPersonId());
                }

            }
        }
    }
    //UNNÖTIG! KANN WEG!
    @Override
    public void handleEvent(PersonDepartureEvent person_haltestelle_einstieg){
        for (Id<Person> p:FahrgastTram){
           if (person_haltestelle_einstieg.getPersonId().equals(p) && String.valueOf(linksM10).contains(String.valueOf(person_haltestelle_einstieg.getLinkId())) && person_haltestelle_einstieg.getLegMode().equals("pt")){
               tramstops.add(person_haltestelle_einstieg.getLinkId());
               if (String.valueOf(auslastung_Haltestellen.keySet()).contains(String.valueOf(person_haltestelle_einstieg.getLinkId()))){
                   auslastung_Haltestellen.put(person_haltestelle_einstieg.getLinkId(), auslastung_Haltestellen.get(person_haltestelle_einstieg.getLinkId())+1);
                   count=count+1;
                   test.put(person_haltestelle_einstieg.getPersonId(),person_haltestelle_einstieg.getLinkId());
               }
           }
        }

    }

    @Override
    public void handleEvent(AgentWaitingForPtEvent person_wartet_Haltestelle){
        Set<Id<Person>> einzelne_Personen = new HashSet<>(FahrgastTram);

        List<Id<Person>> testlistperson = new ArrayList<>();
        testlistperson.add(Id.createPersonId("234372001"));
        testlistperson.add(Id.createPersonId("360285601"));
//einzelne_Personen
        for (Id<Person> pid:testlistperson){
            if (person_wartet_Haltestelle.getPersonId().equals(pid)){
                testStops.add(person_wartet_Haltestelle.getWaitingAtStopId());
            }
        }
       // System.out.println("Anzahl einzelne Personen" + einzelne_Personen.size()+ "Anzahl Fahrten" + personTram.size());
    }
    //Was ist gegeben: es sind die FahrzeugIDs bekannt und die Personen, die einsteigen.
    /*Was noch gesucht wird: Aus den events suche ich die stopFacilities raus an denen die Personen warten
    zum einsteigen. wenn daraufhin die nächste activity/event "person enters vehicle --> pt_M10...,
    wird der name für die stopFacility aus transitschedule geholt und in der Haltestellen Auslastung der
    counter hochgesetzt.*/

    public void print(){
        System.out.println("###WICHTIGER PART###");
        System.out.println("Anzahl Fahrgäste" + FahrgastTram.size() + "und Anzahl Mitarbeiter" + FahrerTram.size());
        System.out.println("### DONE ###");
        try {
          /*  bufferedWriter.write("Haltestelle\tAuslastung" );
            bufferedWriter.newLine();
            for (int i = 0; i < auslastung_Haltestellen.keySet().size(); i++) {
                bufferedWriter.write(linksM10.get(i) + "\t" + auslastung_Haltestellen.get(linksM10.get(i)));
                bufferedWriter.newLine();
            }
            bufferedWriter.write("Summe der Personen in total:" + personTram.size() + "gegenchecken mit warten auf pt" + testStops.size()+ "und wie viele einzelne Personen sind es" +singlePerson.size());
            bufferedWriter.close();*/
            bufferedWriter.write("Personen die in M10 steigen = " + FahrgastTram.size());
            bufferedWriter.newLine();
            for (int i = 0; i < FahrgastTram.size(); i++) {
                bufferedWriter.write(String.valueOf(FahrgastTram.get(i)));
                bufferedWriter.newLine();
            }
        } catch (IOException ee){
            throw new RuntimeException(ee);
        }

    }

}
