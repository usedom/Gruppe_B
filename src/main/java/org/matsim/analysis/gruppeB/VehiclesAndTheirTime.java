package org.matsim.analysis.gruppeB;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.*;
import org.matsim.api.core.v01.events.handler.*;
import org.matsim.api.core.v01.population.Person;
import org.matsim.vehicles.Vehicle;

import java.util.*;

public class VehiclesAndTheirTime implements PersonEntersVehicleEventHandler, PersonLeavesVehicleEventHandler, ActivityEndEventHandler, ActivityStartEventHandler, LinkEnterEventHandler, LinkLeaveEventHandler{
    //original
    int[] personid = {373490801, 434484701, 415434501, 74750501, 408402201, 118898701, 79687101, 150415201, 223731001, 173659901, 362381301, 412723001, 406719301, 338625901, 430148801, 230006301, 152910601, 189063701, 407996401, 263142401, 193538901, 252520901, 267428801, 310607701, 320387001, 262869001, 441753101, 389716501, 250473601, 381469401, 95030601, 229898101, 4567801, 292759601, 151623801, 424899401, 153111301, 108419001, 235043101, 394599301, 438833701, 117731501, 387639301, 416331101, 409137901, 267313701, 488383301, 387869801, 415934601, 281200501, 362972801, 132990601, 267428801, 166492301, 377738101,361857201, 386014401, 95715501, 320387001, 447334201, 413889801, 322449601, 216832701, 231099901, 248234601, 418974201, 323365301, 242625401, 193197701, 108419001, 334433601, 52632601, 297975501, 306071301, 438833701, 387639301, 64509501, 137587101, 357517401, 409137901, 202964301, 288029201, 369503101, 412068201, 449167501, 199257001, 347845301, 488383301, 429869101, 369227901, 438965201};
    /*//mod
    int[] personid = {152910601, 199731401, 95177101, 115036501, 345196001, 373490801, 263142401, 434484701, 193538901, 252520901, 74750501, 310607701, 408402201, 186608601, 318064001, 320387001, 262869001, 208618601, 178306901, 118898701, 381469401, 95030601, 150415201, 173659901, 362381301, 326837901, 79211601, 151623801, 235043101, 406719301, 338625901, 387639301, 416331101, 420932101, 257667701, 430148801, 230006301, 488383301, 387869801, 261724301, 166492301, 361857201, 231099901, 388292801, 380305101, 283237601, 435039901, 193197701, 446497901, 137587101, 357517401, 55287901, 369227901, 415934601, 334314901, 392816801, 362972801, 317510001, 377738101, 386014401, 95715501, 265430301, 322449601, 248234601, 418974201, 242625401, 272741901, 334433601, 52632601, 297975501, 306071301, 438833701, 64509501, 202964301, 288029201, 449167501, 199257001, 347845301, 429869101};
    *///Entering Links

    //Leaving Links

    public Map<Id<Vehicle>, Double> VehicleKMAenter = new HashMap<>();
    public Map<Id<Vehicle>, Double> VehicleKMAleave = new HashMap<>();
    public Map<Id<Vehicle>, Id<Person>> VehiclePerson = new HashMap<>();
    public List<Double> TimePersonInCar = new ArrayList<>();
    public List<Double> Triptime = new ArrayList<>();
    public List<Double> TT = new ArrayList<>();
    public List<String> AktivitetenAmEnde = new ArrayList<>();
    public Map<Id<Person>, String> AktivitetenAmAnfang = new HashMap<>();
    public Map<Id<Vehicle>, Double> VehicleEntersLinkTime = new HashMap<>();
    public Map<Id<Vehicle>, Double> VehicleLeavesLinkTime = new HashMap<>();

    @Override
    public void handleEvent(PersonEntersVehicleEvent enterVehicleEvent){
        for (int i : personid){
            if (enterVehicleEvent.getPersonId().equals(Id.createPersonId(Integer.toString(i)))){
                VehicleKMAenter.put(enterVehicleEvent.getVehicleId(), enterVehicleEvent.getTime());
            }
        }
    }

    @Override
    public void handleEvent(PersonLeavesVehicleEvent leaveVehicleEvent){
        for (int i : personid){
            if (leaveVehicleEvent.getPersonId().equals(Id.createPersonId(Integer.toString(i)))){
                VehicleKMAleave.put(leaveVehicleEvent.getVehicleId(), leaveVehicleEvent.getTime());
                VehiclePerson.put(leaveVehicleEvent.getVehicleId(), leaveVehicleEvent.getPersonId());
                TimePersonInCar.add(leaveVehicleEvent.getTime()-VehicleKMAenter.get(Id.createPersonId(Integer.toString(i))));
            }

        }
    }

    @Override
    public void handleEvent(ActivityEndEvent actEnds){
        for (Id<Vehicle> i : VehiclePerson.keySet()){
            if (actEnds.getLinkId().equals(Id.createLinkId("126333"))||actEnds.getLinkId().equals(Id.createLinkId("99708"))){
                AktivitetenAmEnde.add(actEnds.getActType());
            }
        }
    }

    @Override
    public void handleEvent(ActivityStartEvent actStarts){
        for (Id<Vehicle> i : VehiclePerson.keySet()){
            if (actStarts.getLinkId().equals(Id.createLinkId("54738"))||actStarts.getLinkId().equals(Id.createLinkId("97508"))){
                AktivitetenAmAnfang.put(actStarts.getPersonId(), actStarts.getActType());
            }
        }
    }

    @Override
    public void handleEvent(LinkEnterEvent linkEnters){
        for (Id<Vehicle> i : VehiclePerson.keySet()){
            if (linkEnters.getVehicleId().equals(i) &&(linkEnters.getLinkId().equals(Id.createLinkId("54738"))||linkEnters.getLinkId().equals(Id.createLinkId("97508")))){
                VehicleEntersLinkTime.put(linkEnters.getVehicleId(), linkEnters.getTime());
            }
        }
    }

    @Override
    public void handleEvent(LinkLeaveEvent linkLeaves){
        for (Id<Vehicle> i : VehiclePerson.keySet()){
            if (linkLeaves.getVehicleId().equals(i) &&(linkLeaves.getLinkId().equals(Id.createLinkId("126333"))||linkLeaves.getLinkId().equals(Id.createLinkId("99708")))){
                VehicleLeavesLinkTime.put(linkLeaves.getVehicleId(), linkLeaves.getTime());
                Triptime.add(VehicleEntersLinkTime.get(i));
                TT.add(linkLeaves.getTime());
            }
        }
    }

    public void print(){
      //  System.out.println("Thats the Tripduration" + Arrays.asList(TimePersonInCar));
      //  System.out.println(Arrays.asList(VehicleKMAenter));
      //  System.out.println(Arrays.asList(VehicleKMAleave));
      //  System.out.println(VehicleKMAenter.size());
      //  System.out.println("Aktivität vor Betreten der KMA" + AktivitetenAmAnfang);
      //  System.out.println("Aktivität nach Verlassen der KMA" + AktivitetenAmEnde);
      //  System.out.println("Zeit von einem Fahrzeug auf KMA" + Triptime);
        System.out.println("Wann verlässt das Fahrzeug den Link"+ VehicleLeavesLinkTime);
        System.out.println("Wann betritt das Fahrzeug den Link"+ VehicleEntersLinkTime);
        System.out.println("Differenz davon ware jetzt die richtige zeit "+ Triptime);
        System.out.println("Differenz davon"+ TT);
    }
}
