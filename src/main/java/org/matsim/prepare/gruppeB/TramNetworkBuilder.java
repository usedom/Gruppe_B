package org.matsim.prepare.gruppeB;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.utils.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TramNetworkBuilder {

    Map<String, List<Id<Link>>> NWLinks = new HashMap<>();

    Map<String, List<Id<Link>>> getNWLinks(){
        if(!NWLinks.isEmpty()){
            return NWLinks;
        }
        else {
            System.out.println("ERROR! Build TramNetwork first!");
            return null;
        }
    }

   Map<String, List<Id<Link>>> build(Network network, List<Node> nodeList, String outputNetwork) {
        System.out.println("\tCreate and add new pt-links for both directions...");
        int nsize = nodeList.size();
        List<Id<Link>> m10links_WD = new ArrayList<>();
        List<Id<Link>> m10links_DW = new ArrayList<>();
        Map<String, List<Id<Link>>> links = new HashMap<>(2);
        links.put("WD",m10links_WD);
        links.put("DW", m10links_DW);

        for (int i = 0; i < nsize - 1; i++) {
            m10links_WD.add(i, Id.createLinkId("pt_M10_4WD-" + i));
            m10links_DW.add(i, Id.createLinkId("pt_M10_4DW-" + i));
        }


        int lsize = nsize-1;

        for (int i = 0; i < lsize; i++) {
            if(i==0) {
                // linkWD(0) connects Warschauer Str (...19, node 1) with Warschauer Str (...19, node 1)
                NetworkUtils.createAndAddLink(network, m10links_WD.get(i), nodeList.get(i + 1), nodeList.get(i + 1),
                       100, 8.333333, 100000., 1.).setAllowedModes(CollectionUtils.stringToSet("pt"));
                // linkDW(4) connects Falckensteinstr. (...65, node 2) with Warschauer Str (...21, node 0)
                NetworkUtils.createAndAddLink(network, m10links_DW.get(lsize - i - 1), nodeList.get(i + 2), nodeList.get(i),
                       getDistance(nodeList.get(i+2), nodeList.get(i)), 8.333333, 100000., 1.).setAllowedModes(CollectionUtils.stringToSet("pt"));
                continue;
            }
            else{
                NetworkUtils.createAndAddLink(network, m10links_WD.get(i), nodeList.get(i), nodeList.get(i + 1),
                       getDistance(nodeList.get(i),nodeList.get(i+1)), 8.333333, 100000., 1.).setAllowedModes(CollectionUtils.stringToSet("pt"));
            }
            if(i!=4) {
                NetworkUtils.createAndAddLink(network, m10links_DW.get(lsize - i - 1), nodeList.get(i + 2), nodeList.get(i + 1),
                       getDistance(nodeList.get(i + 2), nodeList.get(i + 1)), 8.333333, 100000., 1.).setAllowedModes(CollectionUtils.stringToSet("pt"));
            }
            // linkDW(0) must get from Hermannplatz (node 5) to Hermannplatz (node 5)
            else{
                NetworkUtils.createAndAddLink(network, m10links_DW.get(lsize - i - 1), nodeList.get(5), nodeList.get(5),
                        100, 8.333333, 100000., 1.).setAllowedModes(CollectionUtils.stringToSet("pt"));
            }
        }

        new NetworkWriter(network).write(outputNetwork);

        NWLinks = links;

        return links;
   }


   private double getDistance(Node start, Node end) {
        Coord a = start.getCoord();
        Coord b = end.getCoord();
        double x_dist = Math.abs(a.getX() - b.getX());
        double y_dist = Math.abs(a.getY() - b.getY());
   //           if(a.getX()>b.getX()){
   //               x_dist = a.getX()-b.getX();
   //           }
   //           else{
   //               x_dist = b.getX()-a.getX();
   //           }
   //           if(a.getY()>b.getY()){
   //               y_dist = a.getY()-b.getY();
   //           }
   //           else{
   //               y_dist = b.getY()-a.getY();
   //           }
       // sqrt-distance + 10% extra to be safe
       System.out.println("x: " + x_dist);
       System.out.println("y: " + y_dist);
       double dist = Math.sqrt((x_dist * x_dist) + (y_dist * y_dist)) * 1.1;
       System.out.println(dist);

       return dist;
   }

}
