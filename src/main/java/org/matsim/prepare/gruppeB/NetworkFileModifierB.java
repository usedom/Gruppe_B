package org.matsim.prepare.gruppeB;

import org.apache.commons.io.FileUtils;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class NetworkFileModifierB {

// The method uses as the input network on the vsp website and changes the number of lanes on the Karl-Marx-Allee
    //The new network is named modified-cloned-berlin-matsim.xml.gz"
    public void modify() {
        File inputFile = new File("scenarios/berlin-v5.5-1pct/input/berlin-matsim-v5.5-network.xml.gz");
        try{
            URL url = new URL("https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/berlin/berlin-v5.5-10pct/input/berlin-v5.5-network.xml.gz");

            FileUtils.copyURLToFile(url,inputFile);
        } catch (IOException e){
            e.printStackTrace();
        }



        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(String.valueOf(inputFile));

        // stays unmodified at 3 lanes, east entry of KMA
        // int[] links_3_eastentry_unmodified = {54738,49528,132668};

        // stays unmodified at 3 lanes, west exit of KMA
        // int[] links_3_eastentry_unmodified = {152091};

        // stays unmodified at 3 lanes, new turning lanes
        // int[] links_3_turn_unmodified = {141526,69223};

        // modify to 3->2 lanes
        int[] links_3_b = {2942,50779,48093,68519,70094,112640,5198,12474,113232,97508,96172,
                96171,57167,58881,52938,113580,57062,94781,113244,30224,50381,89327,152474};

        // stays unmodified at 4 lanes, east end of KMA
        // int[] links_4 = {99708};

        // modify 4->3 lanes
        int[] links_4_b = {59654};

        //modify to 4->2 lanes
        int[] links_4_c = {86406,57059,69132};

        // stays unmodified at 5 lanes, west end of KMA
        // int[] links_5 = {126333};

        // modify to 5->4 lanes
        int[] links_5_b = {113237};

        for (int i:links_3_b
        ) {
            network.getLinks().get(Id.createLinkId(Integer.toString(i))).setNumberOfLanes(2);
        }

        for (int i:links_4_b
        ) {
            network.getLinks().get(Id.createLinkId(Integer.toString(i))).setNumberOfLanes(3);
        }

        for (int i:links_4_c
        ) {
            network.getLinks().get(Id.createLinkId(Integer.toString(i))).setNumberOfLanes(2);
        }

        for (int i:links_5_b
        ) {
            network.getLinks().get(Id.createLinkId(Integer.toString(i))).setNumberOfLanes(4);
        }

        String outputFile = "scenarios/berlin-v5.5-1pct/input/modified-cloned-berlin-matsim.xml.gz";
        new NetworkWriter(network).write(outputFile);
    }
}