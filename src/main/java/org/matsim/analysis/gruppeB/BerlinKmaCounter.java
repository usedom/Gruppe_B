package org.matsim.analysis.gruppeB;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.events.LinkEnterEvent;
import org.matsim.api.core.v01.events.handler.LinkEnterEventHandler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BerlinKmaCounter implements LinkEnterEventHandler {

    // in Datei schreiben
    private BufferedWriter bufferedWriter;

    public BerlinKmaCounter(String outputfile){

        try {
            FileWriter fileWriter = new FileWriter(outputfile);
                bufferedWriter = new BufferedWriter(fileWriter);
            } catch(IOException ee){
                throw new RuntimeException(ee);
            }
        }


    // Rest
    private List<LinkEnterEvent> events = new ArrayList<>();
    private double[] eastLinkKMA = new double[30];
    private double[] westLinkKMA = new double[30];

    private int getSlot(double time){
        return (int) time/3600;
    }

    @Override
    public void handleEvent(LinkEnterEvent enterKMA){

        if(enterKMA.getLinkId().equals(Id.createLinkId("97508")) || enterKMA.getLinkId().equals(Id.createLinkId("54738"))){
            int slot = getSlot(enterKMA.getTime());
            if(enterKMA.getLinkId().equals(Id.createLinkId("97508"))){
                this.eastLinkKMA[slot]++;
            }
            else if (enterKMA.getLinkId().equals(Id.createLinkId("54738"))){
                this.westLinkKMA[slot]++;
            }
            events.add(enterKMA);
        }
    }


    public void printResult() {
        try {
            bufferedWriter.write("Hour\tFrom East\tFrom West\tTotal\n" + events.size());
            System.out.println("Hour\tFrom East\tFrom West\tTotal\n");
            for (int i = 0; i < 24; i++) {
                double east_volume = this.eastLinkKMA[i];
                double west_volume = this.westLinkKMA[i];
                bufferedWriter.write("\n  " + i + "\t" + east_volume + "\t" + west_volume + "\t" + (east_volume+west_volume) + "\n\n");
                //System.out.println("volume on link 6 from " + i + " to " + (i+1) + "o clock = " + volume);
                System.out.println(i + "\t\t" + east_volume + "\t\t" + west_volume + "\t\t" + (east_volume+west_volume) + "\n");
            }
            bufferedWriter.close();
        } catch (IOException ee){
            throw new RuntimeException(ee);
        }
    }

    public List<LinkEnterEvent> writeListOfEvents() {
        return this.events;

    }


}
