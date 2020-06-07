package org.matsim.analysis.gruppeB;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;
import org.matsim.vehicles.Vehicle;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BerlinKmaCounter implements LinkEnterEventHandler {

    // in Datei schreiben
    private final BufferedWriter bufferedWriter;

    /** (1) Used for classic printResult */
    private final List<LinkEnterEvent> events = new ArrayList<>();
    private final double[] eastLinkKMA = new double[30];
    private final double[] westLinkKMA = new double[30];

    /** (2) Used for chronological printResult, for each direction one column */
    private final Map<Double, Id<Vehicle>> eastmap = new HashMap<>();
    private final Map<Double, Id<Vehicle>> westmap = new HashMap<>();
    private final List<Double> geteasttime = new ArrayList<>();
    private final List<Double> getwesttime = new ArrayList<>();

    /** (3) Used for chronological printResult, for both directions one column */
    private final Map<Double, Id<Vehicle>> combimap = new HashMap<>();
    private final List<Double> combitimes = new ArrayList<>();
    private final List<String> direction = new ArrayList<>();
    int it_direction = 0;

    public BerlinKmaCounter(String outputfile){

        try {
            FileWriter fileWriter = new FileWriter(outputfile);
            bufferedWriter = new BufferedWriter(fileWriter);
        } catch(IOException ee){
            throw new RuntimeException(ee);
        }
    }

    private int getSlot(double time){
        return (int) time/3600;
    }

    @Override
    public void handleEvent(LinkEnterEvent enterKMA){

        /**
        Links **entering** KMA: From East: 54738 / From West: 97508
        Links **leaving** KMA:  From East: 126333 / From West: 99708
         */

        if(enterKMA.getLinkId().equals(Id.createLinkId("126333")) || enterKMA.getLinkId().equals(Id.createLinkId("99708"))){
            int slot = getSlot(enterKMA.getTime());
            combimap.put(enterKMA.getTime(), enterKMA.getVehicleId());
            combitimes.add(enterKMA.getTime());
            if(enterKMA.getLinkId().equals(Id.createLinkId("126333"))){
                this.eastLinkKMA[slot]++;
                eastmap.put(enterKMA.getTime(), enterKMA.getVehicleId());
                geteasttime.add(enterKMA.getTime());

                direction.add(it_direction, "east");
            }
            else if (enterKMA.getLinkId().equals(Id.createLinkId("99708"))){
                this.westLinkKMA[slot]++;
                westmap.put(enterKMA.getTime(), enterKMA.getVehicleId());
                getwesttime.add(enterKMA.getTime());

                direction.add(it_direction, "west");
            }
            it_direction++;
            events.add(enterKMA);
        }

    }

    /** (1) */
    public void printResult() {
        try {
            bufferedWriter.write("Hour\tFrom East\tFrom West\tTotal:\t" + events.size());
            bufferedWriter.newLine();
            System.out.println("Hour\tFrom East\tFrom West\tTotal\n");
            for (int i = 0; i < 24; i++) {
                double east_volume = this.eastLinkKMA[i];
                double west_volume = this.westLinkKMA[i];
                bufferedWriter.write(i+1 + "\t" + east_volume + "\t" + west_volume + "\t" + (east_volume+west_volume));
                bufferedWriter.newLine();
                //System.out.println("volume on link 6 from " + i + " to " + (i+1) + "o clock = " + volume);
                System.out.println(i+1 + "\t\t" + east_volume + "\t\t\t" + west_volume + "\t\t\t" + (east_volume+west_volume) + "\n");
            }
            bufferedWriter.close();
        } catch (IOException ee){
            throw new RuntimeException(ee);
        }
    }

    /** (2) */
    public void printResult_timesAndIds() {
        try {
            bufferedWriter.write("Time\tFrom East\tFrom West\tTotal:\t" + events.size());
            bufferedWriter.newLine();

            System.out.println("Time\t\tVehicle from East\t\tVehicle from West");

            for (int i = 0; i < (eastmap.size()+westmap.size()); i++) {
                double easttime = 30*3600, westtime =30*3600;
                boolean eastcheck = false, westcheck = false;
                //System.out.println(geteasttime);
                //System.out.println(getwesttime);
                if(i < eastmap.size()) {
                    easttime = geteasttime.get(i);
                    eastcheck=true;
                }
                if (i < westmap.size()) {
                    westtime = getwesttime.get(i);
                    westcheck=true;
                }

                //bufferedWriter.newLine();
                if(eastcheck && westcheck) {
                    if (easttime < westtime) {
                        bufferedWriter.write(easttime + "\t" + eastmap.get(easttime));
                        System.out.println(easttime + "\t" + eastmap.get(easttime) + "\n");
                    } else {
                        bufferedWriter.write(westtime + "\t\t\t\t\t\t" + westmap.get(westtime));
                        System.out.println(westtime + "\t\t\t\t\t" + westmap.get(westtime) + "\n");
                    }
                    bufferedWriter.newLine();
                }
                else if(eastcheck){
                    bufferedWriter.write(String.valueOf(easttime) + eastmap.get(easttime));
                    System.out.println(easttime + "\t\t" + eastmap.get(easttime) + "\n");
                    bufferedWriter.newLine();
                }
                else if (westcheck) {
                    bufferedWriter.write("\t\t\t\t\t\t\t\t" + westtime + westmap.get(westtime));
                    System.out.println("\t\t\t\t\t\t\t" + westtime + "\t\t\t" + westmap.get(westtime) + "\n");
                    bufferedWriter.newLine();
                }
                else{
                    continue;
                }

            }
            bufferedWriter.close();
        } catch (IOException ee){
            throw new RuntimeException(ee);
        }
    }

    /** (3) */
    public void printResult_timesAndIds_combi() {
        try {
            bufferedWriter.write("Time\t\tVehicleID\t\t\t\t\tFrom:\t\tTotal:\t" + events.size());
            bufferedWriter.newLine();

            System.out.println("Time\t\tVehicleID\t\t\t\t\tFrom:\t\tTotal:\t" + events.size());

            for (int i = 0; i < (combimap.size()); i++) {
                bufferedWriter.write(combitimes.get(i) + "\t\t" + combimap.get(combitimes.get(i)) + "\t\t" + direction.get(i));
                System.out.println(combitimes.get(i) + "\t\t" + combimap.get(combitimes.get(i)) + "\t\t" + direction.get(i));
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException ee){
            throw new RuntimeException(ee);
        }
    }

    public List<LinkEnterEvent> writeListOfEvents() {
        return this.events;
    }

    public Map<Double, Id<Vehicle>> gettimevehiclemap(){
        return combimap;
    }

    public List<Double> getCombitimes(){
        return combitimes;
    }
}
