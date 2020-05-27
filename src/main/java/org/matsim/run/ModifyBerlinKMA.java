package org.matsim.run;

import org.apache.commons.io.FileUtils;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModifyBerlinKMA {

    // change Path to String + avoid "toString in NetworkReader
    public static void main(String[] args) {
        //Path inputnetwork = Paths.get(args[0]);
        //Path outputnetwork = Paths.get(args[1]);

        /* TO-DO:
        - redefine output location to 1pct-config-folder
        - reset networkinput file in 1pct-config
        - integrate code tu (Experiment)RunMatsim file
        - try to modify reading list of modified links
        - validate result in via...
         */

        System.out.println(System.getProperty("user.dir"));

        File outputfile = new File("cloned-berlin-matsim.xml.gz");
        try{
            URL url = new URL("https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/berlin/berlin-v5.5-10pct/input/berlin-v5.5-network.xml.gz");

            FileUtils.copyURLToFile(url,outputfile);
        } catch (IOException e){
            e.printStackTrace();
        }
        String inputFile = "cloned-berlin-matsim.xml.gz";
        String outputFile = "modified-cloned-berlin-matsim.xml.gz";

        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(inputFile);

        network.getLinks().get(Id.createLinkId("54738")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("57458")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("49528")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("132668")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("2942")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("50779")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("48093")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("68519")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("141526")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("86406")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("70094")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("112640")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("5198")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("152474")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("152091")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("113237")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("126333")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("97508")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("96172")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("96171")).setNumberOfLanes(2);
        network.getLinks().get(Id.createLinkId("57167")).setNumberOfLanes(2);

        new NetworkWriter(network).write(outputFile);
    }
}

    /* Restliche Links
        52938
        113580
        57059
        69132
        57062
        69223
        94781
        113244
        30224
        113232
        50381
        58881
        89327
        59654
        99708

     */
