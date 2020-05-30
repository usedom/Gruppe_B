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

        int[] links = {54738,49528,132668,2942,50779,48093,68519,141526,86406,70094,112640,5198,152474,
                152091, 113232, 113237,126333,97508,96172,96171,57167, 58881, 52938,113580,57059,69132,57062,69223,94781,
                113244,30224,50381,89327,59654,99708};

        for (int i:links
        ) {
            //network.getLinks().get(Id.createLinkId(Integer.toString(i))).setCapacity(0);
            network.getLinks().get(Id.createLinkId(Integer.toString(i))).setNumberOfLanes(2);
        }


        String outputFile = "scenarios/berlin-v5.5-1pct/input/modified-cloned-berlin-matsim.xml.gz";
        new NetworkWriter(network).write(outputFile);
    }
}