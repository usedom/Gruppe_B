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
                        10000, 8.333333, 500., 1.).setAllowedModes(CollectionUtils.stringToSet("pt"));
                // linkDW(4) connects Falckensteinstr. (...65, node 2) with Warschauer Str (...21, node 0)
                NetworkUtils.createAndAddLink(network, m10links_DW.get(lsize - i - 1), nodeList.get(i + 2), nodeList.get(i),
                        10000, 8.333333, 500., 1.).setAllowedModes(CollectionUtils.stringToSet("pt"));
                continue;
            }
            else{
                NetworkUtils.createAndAddLink(network, m10links_WD.get(i), nodeList.get(i), nodeList.get(i + 1),
                        10000, 8.333333, 500., 1.).setAllowedModes(CollectionUtils.stringToSet("pt"));
            }
            if(i!=4) {
                NetworkUtils.createAndAddLink(network, m10links_DW.get(lsize - i - 1), nodeList.get(i + 2), nodeList.get(i + 1),
                        10000, 8.333333, 500., 1.).setAllowedModes(CollectionUtils.stringToSet("pt"));
            }
            // linkDW(0) must get from Hermannplatz (node 5) to Hermannplatz (node 5)
            else{
                NetworkUtils.createAndAddLink(network, m10links_DW.get(lsize - i - 1), nodeList.get(5), nodeList.get(5),
                        10000, 8.333333, 500., 1.).setAllowedModes(CollectionUtils.stringToSet("pt"));
            }
        }

        new NetworkWriter(network).write(outputNetwork);

        NWLinks = links;

        return links;
    }


}
