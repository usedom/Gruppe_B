package org.matsim.prepare.gruppeB;

import org.apache.commons.io.FileUtils;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.network.LinkFactory;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.collections.CollectionUtils;
import org.matsim.pt.transitSchedule.DepartureImpl;
import org.matsim.pt.transitSchedule.TransitLineImpl;
import org.matsim.pt.transitSchedule.TransitScheduleFactoryImpl;
import org.matsim.pt.transitSchedule.TransitStopFacilityImpl;
import org.matsim.pt.transitSchedule.api.*;
import scala.collection.mutable.LinkedHashSet$;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;


public class TramLineBuilder {


// The method uses as the input network on the vsp website and extends M10 line as proposed in variant 4
// The new network is named modified-cloned-berlin-matsim.xml.gz"
//public void modify() {
public static void main(String[] args) {

        File inputFile = new File("scenarios/berlin-v5.5-1pct/input/berlin-matsim-v5.5-network.xml.gz");
        try{
            URL url = new URL("https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/berlin/berlin-v5.5-10pct/input/berlin-v5.5-network.xml.gz");

            FileUtils.copyURLToFile(url,inputFile);
        } catch (IOException e){
            e.printStackTrace();
        }



        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(String.valueOf(inputFile));

        // List of relevant nodes (set this as input-thing)
        Node warschauer_dep = network.getNodes().get(Id.createNodeId("pt_070301008821"));
        Node warschauer_arr = network.getNodes().get(Id.createNodeId("pt_070301008819"));
        Node falckenstein = network.getNodes().get(Id.createNodeId("pt_070101001365"));
        Node pfilgerstr = network.getNodes().get(Id.createNodeId("pt_070101003213"));
        Node hermann = network.getNodes().get(Id.createNodeId("pt_070101004244"));
        // New nodes
        Node park = NetworkUtils.createNode(Id.createNodeId("pt_07010100newpark"));
        Coord park_coord = new Coord(4597874.123, 5819049.123);
        park.setCoord(park_coord);
        NetworkUtils.createAndAddNode(network,park.getId(),park_coord);

        /** Build variant 4 */

        List<Id<Link>> M10_var4 = new ArrayList<>();


        for(int i=0; i<8; i++) {
            M10_var4.add(i, Id.createLinkId("pt_M10_4-" + i));
        }

        NetworkUtils.createAndAddLink(network, M10_var4.get(0),warschauer_dep,falckenstein,5000,8.333333,500.0,1.0);
        NetworkUtils.createAndAddLink(network, M10_var4.get(1),falckenstein,park,10000,8.333333,500.0,1.0);
        NetworkUtils.createAndAddLink(network, M10_var4.get(2),park,pfilgerstr,10000,8.333333,500.0,1.0);
        NetworkUtils.createAndAddLink(network, M10_var4.get(3),pfilgerstr,hermann,10000,8.333333,500.0,1.0);
        NetworkUtils.createAndAddLink(network, M10_var4.get(4),hermann,pfilgerstr,10000,8.333333,500.0,1.0);
        NetworkUtils.createAndAddLink(network, M10_var4.get(5),pfilgerstr,park,10000,8.333333,500.0,1.0);
        NetworkUtils.createAndAddLink(network, M10_var4.get(6),park,falckenstein,10000,8.333333,500.0,1.0);
        NetworkUtils.createAndAddLink(network, M10_var4.get(7),falckenstein,warschauer_arr,10000,8.333333,500.0,1.0);

        /*
        Set<String> pt = new HashSet<>();
        pt.add("pt");
        for(Id<Link> id:M10_var4) {
            network.getLinks().get(id).setAllowedModes(pt);
        }

         */

        for(Link link:network.getLinks().values()){
                for(int i=0; i<8; i++) {
                        if (M10_var4.get(i) == link.getId()) {
                                link.setAllowedModes(CollectionUtils.stringToSet("pt"));
                        }
                }
        }

/*
        for(Id<Link> id:M10_var4){
                Link link = network.getLinks().get(id);
                link.setAllowedModes(CollectionUtils.stringToSet("pt"));
        }

 */

        String outputFile = "scenarios/berlin-v5.5-1pct/input/newnetwork/tram_modified-cloned-berlin-matsim.xml.gz";
        new NetworkWriter(network).write(outputFile);

        Config config = ConfigUtils.loadConfig("scenarios/berlin-v5.5-1pct/input/berlin-v5.5-1pct.config.xml");
        Scenario scenario = ScenarioUtils.loadScenario(config);

        //config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        //config.controler().setLastIteration(1);

        TransitScheduleReader transitScheduleReader = new TransitScheduleReader(scenario);
        //transitScheduleReader.readFile("C:/Users/djp/Desktop/TUB/MATSim/matsim-2020/git/Gruppe_B/output_ori050/berlin-v5.5-1pct.output_transitSchedule.xml.gz");

        TransitScheduleFactory factory = scenario.getTransitSchedule().getFactory();
        TransitStopFacility parkstop = factory.createTransitStopFacility(Id.create("TransitStopFacility", TransitStopFacility.class),park_coord,false);
        parkstop.setName("Berlin, Im Görlitzer Park");
        parkstop.setLinkId(M10_var4.get(1));
        //TransitStopFacilityImpl stopFacility = new TransitStopFacilityImpl(scenario.getTransitSchedule());


        Map<Id<TransitLine>, TransitLine> map = scenario.getTransitSchedule().getTransitLines();
        Id<TransitLine> transitLineId = Id.create("TransitLine", TransitLine.class);
        // new TransitLineImpl(transitLineId).setName("M10---17440_900");

        System.out.println(map.get(Id.create("TransitLine", TransitLine.class)));


        TransitLine m10;
        List<TransitRoute> m10affectedroutes = new ArrayList<>();
        Map<Id<TransitRoute>, TransitRoute> m10affected_leaving = new HashMap<>();
        Map<Id<TransitRoute>, TransitRoute> m10affected_arriving = new HashMap<>();
        if(map.get("M10---17440_900").getName().toString()!=null){
                m10 = map.get("M10---17440_900");
                for(Id<TransitRoute> id:m10.getRoutes().keySet()){

                        for(TransitRouteStop stop:m10.getRoutes().get("abc").getStops()){
                                if(stop.getStopFacility().getAttributes().getAttribute("refId").equals("070301008821")){
                                        m10affected_leaving.put(id,m10.getRoutes().get(id));
                                }
                        }
                }
        }
        System.out.println(m10affected_leaving);

        /*
        Id<TransitLine> transitLineId = Id.create("Blue Line", TransitLine.class);
        Id<TransitRoute> transitRouteId = Id.create("1to3", TransitRoute.class);
        TransitRoute route = map.get(transitLineId).getRoutes().get(transitRouteId);
        Map<Id<Departure>, Departure> departures = route.getDepartures();
        ArrayList<Departure> toRemove = new ArrayList<>();
        for (Map.Entry<Id<Departure>, Departure> entry : departures.entrySet()) {
                if (entry.getValue().getDepartureTime() >= 7 * 3600 && entry.getValue().getDepartureTime() <= 22 * 3600) {
                        toRemove.add(entry.getValue());
                }
        }
        for (Departure departure : toRemove) {
                route.removeDeparture(departure);
        }

        

        TransitScheduleFactoryImpl transitScheduleFactory = new TransitScheduleFactoryImpl();
        /*TransitScheduleReaderV2 transitScheduleReaderV2 = new TransitScheduleReaderV2(); */

        //TransitScheduleWriter transitScheduleWriter = new TransitScheduleWriter(); //TransitSchedule.api
        //transitScheduleWriter.writeFile("scenarios/pt-tutorial/transitschedule.xml");

        //transitScheduleFactory.createDeparture();

    }
}