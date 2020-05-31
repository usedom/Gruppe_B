package org.matsim.analysis.gruppeB;

import org.apache.commons.io.FileUtils;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GetDataNetworkLinks {

    public static void main(String[] args) {

        // Uncomment line below to printout your root folder
        // System.out.println(System.getProperty("user.dir"));

        // Set URL or local Path as inputFile (from root folder, usually "Gruppe_B")
        String inputFile = "https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/berlin/berlin-v5.5-10pct/input/berlin-v5.5-network.xml.gz";

        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(inputFile);

        int[] links = {54738,49528,132668,2942,50779,48093,68519,141526,86406,70094,112640,5198,152474,
                152091, 113232, 113237,126333,97508,96172,96171,57167, 58881, 52938,113580,57059,69132,57062,69223,94781,
                113244,30224,50381,89327,59654,99708};

        BufferedWriter writer = null;

        try {
            // Set name of output file *csv, *txt, ... again from root folder
            File file = new File("../../../pivot matsim/getDataFromNetwork/getDataKMA_ori.csv");
            if(!file.exists()){
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file);
            writer = new BufferedWriter(fileWriter);
            for (int i:links) {

                String networkdata = "ID:;"+i+";FromNode: "+network.getLinks().get(Id.createLinkId(Integer.toString(i))).getFromNode()+
                        ";ToNode: "+network.getLinks().get(Id.createLinkId(Integer.toString(i))).getToNode()+
                        ";Lenght: "+network.getLinks().get(Id.createLinkId(Integer.toString(i))).getLength()+
                        ";#lanes: "+network.getLinks().get(Id.createLinkId(Integer.toString(i))).getNumberOfLanes()+
                        ";FSpeed: "+network.getLinks().get(Id.createLinkId(Integer.toString(i))).getFreespeed()+
                        ";Capaci: "+network.getLinks().get(Id.createLinkId(Integer.toString(i))).getCapacity()+
                        ";\n";
                writer.write(networkdata);
            }

        }catch (IOException e){
            e.printStackTrace();
        }

        try {
            if(writer!=null) {
                writer.close();
                System.out.println("Done!");
            }
        } catch (IOException ioException) {
            System.out.println("Error!");
        }

    }
}

