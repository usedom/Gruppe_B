package org.matsim.prepare.gruppeB;

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
        int size=nodeList.size();
        List<Id<Link>> m10links_WD = new ArrayList<>();
        List<Id<Link>> m10links_DW = new ArrayList<>();
        Map<String, List<Id<Link>>> links = new HashMap<>(2);
        links.put("WD",m10links_WD);
        links.put("DW", m10links_DW);

        //System.out.println("Create and add new links to the network...");
        for (int i = 1; i < size - 1; i++) {
            m10links_WD.add(i - 1, Id.createLinkId("pt_M10_4WD-" + i));
        }
        // Need one link more at the beginning for the turning point ( = pt_...-0)
        for (int i = 0; i < size - 1; i++) {
            m10links_DW.add(i, Id.createLinkId("pt_M10_4DW-" + i));
        }

        // methods to modify: length (or calculate directly?), freespeed, capacity (? ... more or less to play around..)

        for (int i = 0; i < m10links_WD.size(); i++) {
            NetworkUtils.createAndAddLink(network, m10links_WD.get(i), nodeList.get(i + 1), nodeList.get(i + 2),
                    10000, 8.333333, 500., 1.).setAllowedModes(CollectionUtils.stringToSet("pt"));
        }

        NetworkUtils.createAndAddLink(network, m10links_DW.get(0), nodeList.get(5), nodeList.get(5),
                100, 8.333333, 50.0, 1.0).setAllowedModes(CollectionUtils.stringToSet("pt"));

        for (int i = m10links_DW.size() - 1; i > 0; i--) {
            if (i != 1) {
                NetworkUtils.createAndAddLink(network, m10links_DW.get(m10links_DW.size() - i), nodeList.get(i + 1), nodeList.get(i),
                        10000, 8.333333, 500., 1.).setAllowedModes(CollectionUtils.stringToSet("pt"));
            }
            // fix for last link: Have to get nodeList.get(0) instead of .get(1) !
            else {
                NetworkUtils.createAndAddLink(network, m10links_DW.get(m10links_DW.size() - i), nodeList.get(i + 1), nodeList.get(0),
                        10000, 8.333333, 500., 1.).setAllowedModes(CollectionUtils.stringToSet("pt"));
            }
        }

        new NetworkWriter(network).write(outputNetwork);

        NWLinks = links;

        return links;
    }


}
