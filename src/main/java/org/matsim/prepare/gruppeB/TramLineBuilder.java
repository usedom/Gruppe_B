package org.matsim.prepare.gruppeB;

import org.apache.commons.io.FileUtils;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.collections.CollectionUtils;
import org.matsim.pt.transitSchedule.api.*;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehiclesFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TramLineBuilder {

// The method uses as the input network on the vsp website and extends M10 line as proposed in variant 4
// The new network is named modified-cloned-berlin-matsim.xml.gz"
//public void modify() {
public static void main(String[] args) {

        String configFile = "scenarios/berlin-v5.5-1pct/input/berlin-v5.5-1pct.config.xml";

        String outputNetwork = "scenarios/berlin-v5.5-1pct/input/tram_modified-cloned-berlin-matsim.xml.gz";
        String outputSchedule = "scenarios/berlin-v5.5-1pct/input/M10new-transitSchedule.xml.gz";

        File input = new File(outputNetwork);

        try{
            URL url = new URL("https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/berlin/berlin-v5.5-10pct/input/berlin-v5.5-network.xml.gz");

            FileUtils.copyURLToFile(url,input);
        } catch (IOException e){
            e.printStackTrace();
        }

        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(String.valueOf(input));

        // List of relevant nodes (set this as input-thing)
        Node warschauer_arr = network.getNodes().get(Id.createNodeId("pt_070301008821"));
        Node warschauer_dep = network.getNodes().get(Id.createNodeId("pt_070301008819"));
        Node falckenstein = network.getNodes().get(Id.createNodeId("pt_070101001365"));
        Node pfilgerstr = network.getNodes().get(Id.createNodeId("pt_070101003213"));
        Node hermann = network.getNodes().get(Id.createNodeId("pt_070101004244"));
        // New nodes
        Node park = NetworkUtils.createNode(Id.createNodeId("pt_07010100park"));
        Coord park_coord = new Coord(4597874.123, 5819049.123);
        park.setCoord(park_coord);
        NetworkUtils.createAndAddNode(network,park.getId(),park_coord);

        /** Build variant 4 */



        List<Id<Link>> M10_var4_nachS = new ArrayList<>();
        List<Id<Link>> M10_var4_nachN = new ArrayList<>();


        for(int i=0; i<4; i++) {
            M10_var4_nachS.add(i, Id.createLinkId("pt_M10_4S-" + i));
        }
        for(int i=0; i<4; i++) {
                M10_var4_nachN.add(i, Id.createLinkId("pt_M10_4N-" + i));
        }

        NetworkUtils.createAndAddLink(network, M10_var4_nachS.get(0),warschauer_dep,falckenstein,10000,8.333333,500.0,1.0);
        NetworkUtils.createAndAddLink(network, M10_var4_nachS.get(1),falckenstein,park,10000,8.333333,500.0,1.0);
        NetworkUtils.createAndAddLink(network, M10_var4_nachS.get(2),park,pfilgerstr,10000,8.333333,500.0,1.0);
        NetworkUtils.createAndAddLink(network, M10_var4_nachS.get(3),pfilgerstr,hermann,10000,8.333333,500.0,1.0);
        NetworkUtils.createAndAddLink(network, M10_var4_nachN.get(0),hermann,pfilgerstr,10000,8.333333,500.0,1.0);
        NetworkUtils.createAndAddLink(network, M10_var4_nachN.get(1),pfilgerstr,park,10000,8.333333,500.0,1.0);
        NetworkUtils.createAndAddLink(network, M10_var4_nachN.get(2),park,falckenstein,10000,8.333333,500.0,1.0);
        NetworkUtils.createAndAddLink(network, M10_var4_nachN.get(3),falckenstein,warschauer_arr,10000,8.333333,500.0,1.0);

        /*
        Set<String> pt = new HashSet<>();
        pt.add("pt");
        for(Id<Link> id:M10_var4) {
            network.getLinks().get(id).setAllowedModes(pt);
        }

         */

        for(Link link:network.getLinks().values()){
                for(int i=0; i<4; i++) {
                        if (M10_var4_nachN.get(i) == link.getId()) {
                                link.setAllowedModes(CollectionUtils.stringToSet("pt"));
                        }
                        if (M10_var4_nachS.get(i) == link.getId()) {
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



        Config config = ConfigUtils.loadConfig(configFile);
        Scenario scenario = ScenarioUtils.loadScenario(config);

        PopulationFactory popf = scenario.getPopulation().getFactory();
        TransitSchedule schedule = scenario.getTransitSchedule();
        TransitScheduleFactory tsfactory = scenario.getTransitSchedule().getFactory();
        VehiclesFactory transvf = scenario.getTransitVehicles().getFactory();



        NetworkRoute routeS = popf.getRouteFactories().createRoute(NetworkRoute.class, M10_var4_nachS.get(0), M10_var4_nachS.get(3));
        routeS.setLinkIds(M10_var4_nachS.get(0), M10_var4_nachS.subList(1,3), M10_var4_nachS.get(3));

        NetworkRoute routeN = popf.getRouteFactories().createRoute(NetworkRoute.class, M10_var4_nachS.get(0), M10_var4_nachS.get(3));
        routeN.setLinkIds(M10_var4_nachN.get(0), M10_var4_nachN.subList(1,3), M10_var4_nachN.get(3));

        /** New TransitRouteStop, create List of Route Stops */

        Id<TransitStopFacility> parkstopfacility = Id.create( "M10_park_07010100", TransitStopFacility.class );
        TransitStopFacility parkstop = tsfactory.createTransitStopFacility(parkstopfacility, park_coord,false);
        parkstop.setLinkId(M10_var4_nachS.get(2));
        parkstop.setName("Berlin, Görlitzer Park");
        schedule.addStopFacility(parkstop);

        TransitRouteStop stop0 = tsfactory.createTransitRouteStop(schedule.getFacilities().get(Id.create("070101005203", TransitStopFacility.class)),0.,20.) ;     // Warschauer Str.
        TransitRouteStop stop1 = tsfactory.createTransitRouteStop(schedule.getFacilities().get(Id.create("070101001365", TransitStopFacility.class)),(1*60+30),(1*60+40)) ;      // Falckensteinstr.
        TransitRouteStop stop2 = tsfactory.createTransitRouteStop(parkstop,(2*60+10),(2*60+20));
        TransitRouteStop stop3 = tsfactory.createTransitRouteStop(schedule.getFacilities().get(Id.create("070101003213", TransitStopFacility.class)),(3*60),(3*60+10)) ;     // Pflügerstr.
        TransitRouteStop stop4 = tsfactory.createTransitRouteStop(schedule.getFacilities().get(Id.create("070101004244", TransitStopFacility.class)),(4*60),(4*60+20)) ;      // Hermannplatz/Sonnenallee

        List<TransitRouteStop> stopsM10S = new ArrayList<>();
        List<TransitRouteStop> stopsM10N = new ArrayList<>();

        stopsM10S.add(stop0);stopsM10S.add(stop1);stopsM10S.add(stop2);stopsM10S.add(stop3);stopsM10S.add(stop4);
        stopsM10N.add(stop4);stopsM10N.add(stop3);stopsM10N.add(stop2);stopsM10N.add(stop1);stopsM10N.add(stop0);

        TransitRoute m10S = tsfactory.createTransitRoute(Id.create("M10_4S", TransitRoute.class), routeS, stopsM10S, "tram");
        TransitRoute m10N = tsfactory.createTransitRoute(Id.create("M10_4N", TransitRoute.class), routeN, stopsM10S, "tram");

        // Id<VehicleType> tramTypeID = Id.create( "tram", VehicleType.class );

        //VehicleType tram_vtype = transvf.createVehicleType(tramTypeID);

        for(int i=0; i<(12*9);i++){

                String string = "m10S_"+i;
                //String string = "m10N_"+i;
                //*
                // ok?
                VehicleType tram_vtype = scenario.getTransitVehicles().getVehicleTypes().get(Id.create("Tram_veh_type", VehicleType.class));
                // Id<org.matsim.vehicles.VehicleType> tramtype_id = tramtype.getId();
                scenario.getTransitVehicles().addVehicle(transvf.createVehicle(Id.createVehicleId(string), tram_vtype));
                //*/
                Departure dep_S = tsfactory.createDeparture(Id.create(string, Departure.class), 10*3600.+i*300);
                dep_S.setVehicleId(Id.createVehicleId(string));
                m10S.addDeparture(dep_S);
                Departure dep_N = tsfactory.createDeparture(Id.create(string, Departure.class), 10*3600.+(i+1)*300);
                dep_N.setVehicleId(Id.createVehicleId(string));
                m10N.addDeparture(dep_N);
        }

         //*/
        TransitLine m10line = tsfactory.createTransitLine(Id.create("M10", TransitLine.class));
        m10line.addRoute(m10S);
        m10line.addRoute(m10N);

        schedule.addTransitLine(m10line);


        /** Try to extend line with 900_19 to 900_19_EXT
         * 1. get M10 line, route (19) and original networkroute into extended networkroute
         * 2. get passing route (LinkId) from original into new extended list
         * 3. Fix error
         * 4. ... (to be continued)
         * */

        TransitLine m10_ori = schedule.getTransitLines().get(Id.create("M10---17440_900", TransitLine.class));
        TransitRoute m10_19_ori = m10_ori.getRoutes().get(Id.create("M10---17440_900_19", TransitRoute.class));
        NetworkRoute m10_19_route_ext = m10_19_ori.getRoute().clone();

        //System.out.println(m10_19_ori.getRoute().getLinkIds().size());
        //System.out.println(m10_19_ori.getRoute().getLinkIds().get(23));

        List<Id<Link>> m10_19_linkList_ext = new ArrayList<>();
        m10_19_linkList_ext.addAll(m10_19_ori.getRoute().getLinkIds());

        //System.out.println(m10_19_linkList_ext);
        
        // FIX ERROR: last link is not in getLinkIds() ... why? (see printouts for info)
        m10_19_linkList_ext.add(Id.createLinkId("pt_38360"));

        m10_19_linkList_ext.addAll(M10_var4_nachS);

        int end = m10_19_linkList_ext.size();
        System.out.println(m10_19_linkList_ext);

        m10_19_route_ext.setLinkIds(m10_19_linkList_ext.get(0),m10_19_linkList_ext.subList(1,end-1),m10_19_linkList_ext.get(end-1));

        List<TransitRouteStop> m10_19_stoplist_ext = new ArrayList<>();
        m10_19_stoplist_ext.addAll(m10_19_ori.getStops());
        /*
        for(TransitRouteStop stop:m10_19_ori.getStops()){
                m10_19_stoplist_ext.add(stop);
        }
        */
        double starttime = m10_19_ori.getStops().get(m10_19_ori.getStops().size()-1).getDepartureOffset().seconds();

        // Edit variables to be flexible with other routes! TO DO
        //m10_19_stoplist_ext.addAll(stopsM10S.subList(1,4));
        //System.out.println(m10_19_stoplist_ext);

        //better way to edit departures? TO DO (problem: to iterate over stopX)
        m10_19_stoplist_ext.add(tsfactory.createTransitRouteStop(stop1.getStopFacility(),(starttime+(0*60.)),(starttime+(0*60.)+10.)));
        m10_19_stoplist_ext.add(tsfactory.createTransitRouteStop(stop2.getStopFacility(),(starttime+(1*60.)),(starttime+(1*60.)+10.)));
        m10_19_stoplist_ext.add(tsfactory.createTransitRouteStop(stop3.getStopFacility(),(starttime+(2*60.)),(starttime+(2*60.)+10.)));
        m10_19_stoplist_ext.add(tsfactory.createTransitRouteStop(stop4.getStopFacility(),(starttime+(3*60.)),(starttime+(3*60.)+10.)));

        /*
        m10_19_stoplist_ext.set(m10_19_ori.getStops().size()+0, tsfactory.createTransitRouteStop(stop1.getStopFacility(),(starttime+(0*60.)),(starttime+(0*60.)+10.)));
        m10_19_stoplist_ext.set(m10_19_ori.getStops().size()+1, tsfactory.createTransitRouteStop(stop2.getStopFacility(),(starttime+(1*60.)),(starttime+(1*60.)+10.)));
        m10_19_stoplist_ext.set(m10_19_ori.getStops().size()+2, tsfactory.createTransitRouteStop(stop3.getStopFacility(),(starttime+(2*60.)),(starttime+(2*60.)+10.)));
        m10_19_stoplist_ext.set(m10_19_ori.getStops().size()+3, tsfactory.createTransitRouteStop(stop4.getStopFacility(),(starttime+(3*60.)),(starttime+(3*60.)+10.)));
         */

        Map<Id<Departure>, Departure> m10_19_oriDepartures = m10_19_ori.getDepartures();
        String m10_19_oriDescription = m10_19_ori.getDescription();
        //Attributes m10_19_oriAttributes = m10_19_ori.getAttributes();
        m10_ori.removeRoute(m10_19_ori);

        TransitRoute m10_19_ext = tsfactory.createTransitRoute(Id.create("M10---17440_900_19_EXT", TransitRoute.class),m10_19_route_ext,m10_19_stoplist_ext,"tram");
        m10_19_ext.setDescription(m10_19_oriDescription);
        for(Departure dep:m10_19_oriDepartures.values()){
                m10_19_ext.addDeparture(dep);
        }

        m10_ori.addRoute(m10_19_ext);


        /** */

        new TransitScheduleWriter(scenario.getTransitSchedule()).writeFile(outputSchedule);

        new NetworkWriter(network).write(outputNetwork);

    }

}