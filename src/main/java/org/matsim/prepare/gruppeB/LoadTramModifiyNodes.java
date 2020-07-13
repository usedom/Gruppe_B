package org.matsim.prepare.gruppeB;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadTramModifiyNodes {

    Network network;

    public LoadTramModifiyNodes(Network network, int choice){
        this.network = network;
        switch(choice){
            case 4: System.out.println("\t\tLoad Nodes for Option 4!"); option4(); break;
            default: System.out.println("\t\tERROR! No valid route option!\nNo changes are made!"); optionDefault(); break;
        }
    }
    /** See List of nodes below */

    List<Node> inputnodes = new ArrayList<>();

    List<Node> getList(int choice) {
    System.out.println("\t\tGetting list of Nodes ... ");

    return inputnodes;
    }

    Map<Node, String> getMap(int choice, TransitSchedule tschedule) {
        if(choice==4) {
            System.out.println("\t\tGetting Map of Nodes ...");
            Map<Node, String> nodes = new HashMap<>();
            for (Node node : inputnodes) {
//          if(tschedule.getFacilities().get(Id.create(node.getId().toString().substring(3), TransitStopFacility.class)).getName()==""){
                if (node.getId().toString().contains("new")) {
                    nodes.put(node, "Berlin, Görlitzer Park");
                } else {
                    nodes.put(node, tschedule.getFacilities().get(Id.create(node.getId().toString().substring(3), TransitStopFacility.class)).getName());
                }
                // System.out.println(nodes);  // for troubleshooting
            }
            return nodes;
        }
        //System.out.println("NAME: "+tschedule.getFacilities().get(Id.create(warschauer_dep.substring(3), TransitStopFacility.class)).getName());
        /*nodes.put(network.getNodes().get(Id.createNodeId(warschauer_dep)),tschedule.getFacilities().get(Id.create(warschauer_dep.substring(3), TransitStopFacility.class)).getName());
        nodes.put(network.getNodes().get(Id.createNodeId(warschauer_arr)),tschedule.getFacilities().get(Id.create(warschauer_arr.substring(3), TransitStopFacility.class)).getName());
        nodes.put(network.getNodes().get(Id.createNodeId(falckensteinstr)),tschedule.getFacilities().get(Id.create(falckensteinstr.substring(3), TransitStopFacility.class)).getName());
        nodes.put(NetworkUtils.createAndAddNode(network, Id.createNodeId(görlipark), new Coord(4597874.123, 5819049.123)), "Berlin, Görlitzer Park");
        nodes.put(network.getNodes().get(Id.createNodeId(pfülgerstr)),tschedule.getFacilities().get(Id.create(pfülgerstr.substring(3), TransitStopFacility.class)).getName());
        nodes.put(network.getNodes().get(Id.createNodeId(hermannplatz)),tschedule.getFacilities().get(Id.create(hermannplatz.substring(3), TransitStopFacility.class)).getName());*/
        System.out.println("ERROR! No valid list found! Check option choice!");
        return null;
    }


    /** LIST OF NODES */

    /** Extension, Option 4 */
    // List of relevant nodes
    void option4() {
        String[] nodestrings = {"pt_070301008821", "pt_070301008819", "pt_070101001365", "pt_07010100newpark", "pt_070101003213", "pt_070101004244"}; // individual implementation
        System.out.println("\t\t\tThese are your nodes:");
        for(String onenode:nodestrings){

            if(!network.getNodes().containsKey(Id.createNodeId(onenode))){
                Node node = NetworkUtils.createAndAddNode(network, Id.createNodeId(onenode), new Coord(4597874.123, 5819049.123)); // individual implementation
                inputnodes.add(node);
                System.out.println("\t\t\t"+node);
            }
            else {
                Node node = network.getNodes().get(Id.createNodeId(onenode));
                inputnodes.add(node);
                System.out.println("\t\t\t"+node);
            }
        }
        System.out.println("\t\t\tThese are "+ inputnodes.size()+ " nodes!");
    }

/*String warschauer_dep = "pt_070301008821";
        String warschauer_arr = "pt_070301008819";
        String falckensteinstr = "pt_070101001365";
        String pfülgerstr = "pt_070101003213";
        String hermannplatz = "pt_070101004244";
        // New nodes
        String görlipark = "pt_07010100newpark";
        */
/*
    Id<Node> node1 = Id.createNodeId(warschauer_dep);
    Id<Node> node2 = Id.createNodeId(warschauer_arr);
    Id<Node> node3 = Id.createNodeId(falckensteinstr);
    Id<Node> node4 = Id.createNodeId(String.valueOf(NetworkUtils.createAndAddNode(network, Id.createNodeId(görlipark), new Coord(4597874.123, 5819049.123))));
    Id<Node> node5 = Id.createNodeId(pfülgerstr);
    Id<Node> node6 = Id.createNodeId(hermannplatz);*/

    /** Extension, DefaultOption (copy/paste and add via constructor to create options */
    // List of relevant nodes
    void optionDefault() {
        String node11 = "pt_111";
        String node22 = "pt_222";
        String node33 = "pt_333";
        String node44 = "pt_444";
        String node55 = "pt_555";
        // New nodes
        String node66 = "pt_666";

        Id<Node> node11Id = Id.createNodeId(node11);
        Id<Node> node22Id = Id.createNodeId(node22);
        Id<Node> node33Id = Id.createNodeId(node33);
        Id<Node> node44Id = Id.createNodeId(node44);
        Id<Node> node55Id = Id.createNodeId(node55);
        Id<Node> node66Id = Id.createNodeId(String.valueOf(NetworkUtils.createAndAddNode(network, Id.createNodeId(node66), new Coord(4597874.123, 5819049.123))));
    }
}

