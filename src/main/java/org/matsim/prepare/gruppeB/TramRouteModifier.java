package org.matsim.prepare.gruppeB;

import org.apache.commons.io.FileUtils;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.*;
import org.matsim.pt.utils.TransitScheduleValidator;
import org.matsim.vehicles.VehiclesFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TramRouteModifier {

    public static void main(String[] args) {

        /** Name of Tram Line */
        final String NAME = "M10";  // TO DO: Get this as parameter from RunTramModifier, but does not exist yet...
        final String ROUTE = "M10---17440_900";
        /** Choose Route option */
        final int OPTION = 4;       // TO DO: Get this as parameter from RunTramModifier, but does not exist yet...
        final String OPT = String.valueOf(OPTION);

        /** Set input files */
        // TODO: Get these ALL as parameters from RunTramModifier, but does not exist yet...
        // TODO: Maybe also get networkFile locally and give as input from RunTramModifier...
        String configFile = "scenarios/berlin-v5.5-1pct/input/berlin-v5.5-1pct.config_mod.xml";
        String outputNetwork = "scenarios/berlin-v5.5-1pct/input/tram_v2-berlin-matsim.xml.gz";
        String outputSchedule = "scenarios/berlin-v5.5-1pct/input/M10_WD-transitSchedule.xml.gz";

        File input = new File(outputNetwork);

        try{
            URL url = new URL("https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/berlin/berlin-v5.5-10pct/input/berlin-v5.5-network.xml.gz");
            FileUtils.copyURLToFile(url,input);
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(String.valueOf(input));

        /** Load necessary files */
        // Get at least config and scenario from RunTramModifier (when existing)
        Config config = ConfigUtils.loadConfig(configFile);
        Scenario scenario = ScenarioUtils.loadScenario(config);
        System.out.println("### Build new line "+NAME+" ! ###");
        TransitSchedule tschedule = scenario.getTransitSchedule();
        TransitScheduleFactory tsfactory = tschedule.getFactory();
        // tvfactory might be necessary when including adapted schedule (with Julia's help)
        VehiclesFactory tvfactory = scenario.getTransitVehicles().getFactory();

        /** (1) Load predefined Nodes as List and as Map (Map with stop names) */
        LoadTramModifiyNodes loadNodesAnd = new LoadTramModifiyNodes(network,OPTION);
        List<Node> nodeList = loadNodesAnd.getList(OPTION);
        Map<Node, String> nodeMap = loadNodesAnd.getMap(OPTION,tschedule);
        //System.out.println(nodeMap);
        //int size = nodeList.size();     // This size will be used quite often for iterations! Can be used for troubleshooting
        System.out.println("\t...Done!");

        /** HINT: As we'll talk about two directions, remember those indices (could be changed, if you like...)
         * WD: Warschauer Str.  ->  "Destination"
         * DW: Destination      ->  Warschauer Str.
         *
         * To get specific information (links, routes, ...), you'll have to give this index in the Map
         * e.g. getinfo.get("WD");  //
         * */
        /** (2) Create new pt-links and get linkId-Map WD/DW */
        Map<String, List<Id<Link>>> links = new TramNetworkBuilder().build(network,nodeList,outputNetwork);
        System.out.println("\t...Done!");

        // TODO: Delete this part
        /** (3) Create and set new NetworkRoutes and get Networkroute-Map WD/DW */
        //Map<String, NetworkRoute> nwroutes = new NetworkRouteBuilder().build(scenario,links);
        System.out.println("\t...Done!");

        /** (4) Get (and create non-existing) TransitRouteStops and get TransitRouteStop-Map WD/DW */
        // TODO: Create new TransitStops if their is no TransitStop with those Coord - name???
        // Here done manually with one stop
        Map<String,List<TransitRouteStop>> trstops = new TransitRouteStopBuilder().build(nodeList, nodeMap, links, tschedule);
        System.out.println("\t...Done!");

        /** (5) Try to extend line with ROUTE to ROUTE_EXT */
        System.out.println("\tGet informations from existing Route and add new...");

        /** (5).1 Get M10 line, routes and clone original networkroute */
        TransitLine m10 = tschedule.getTransitLines().get(Id.create(ROUTE, TransitLine.class));

        List<TransitRoute> necessaryroutesWD = new ArrayList<>();
        for(TransitRoute route:m10.getRoutes().values()){
            if(route.getStops().get(route.getStops().size()-1).getStopFacility().getName().equals(nodeMap.get(nodeList.get(1)))){
                necessaryroutesWD.add(route);
            }
        }

        // Beginning for-loop for all necessary routes (Dep/Arr at Warschauer Str.)
        for(TransitRoute m10index :necessaryroutesWD) {
            String index = m10index.getId().toString() + "_EXT";
            NetworkRoute m10route_ext = m10index.getRoute().clone();
            System.out.println(m10index);

            /** (5).2 Get original links and new links together -> NAME+ROUTE_linkList_ext */
            System.out.println("\t\t...links");
            List<Id<Link>> m10_19_linkList_ext = new ArrayList<>();
            m10_19_linkList_ext.add(m10route_ext.getStartLinkId());
            m10_19_linkList_ext.addAll(m10index.getRoute().getLinkIds());
            m10_19_linkList_ext.add(m10route_ext.getEndLinkId());
            m10_19_linkList_ext.addAll(links.get("WD").subList(1, links.get("WD").size()));
            //System.out.println(m10_19_linkList_ext);      // for troubleshooting: get new LinkList for new NetworkRoute

            /** (5).3 Set (5).2 into new NetworkRoute */
            System.out.println("\t\t...NetworkRoute(s)");
            int end = m10_19_linkList_ext.size();
            m10route_ext.setLinkIds(m10_19_linkList_ext.get(0), m10_19_linkList_ext.subList(1, end - 1), m10_19_linkList_ext.get(end - 1));
            //System.out.println(m10route_ext);             // for troubleshooting: get new NetworkRoute WITHOUT startLinkId/endLinkId

            /** (5).4 Get original TarnsitRouteStops and extend by List from (4) */
            // TODO: Edit variables (number of extended stops, departure time, ...) to be flexible with other routes! (with Julia's help)
            // TODO: Better way to edit departures? Do inside new class maybe better?
            System.out.println("\t\t...TransitRouteStops");
            List<TransitRouteStop> m10_19_stoplist_ext = new ArrayList<>();
            m10_19_stoplist_ext.addAll(m10index.getStops());

            double starttime = m10index.getStops().get(m10index.getStops().size() - 1).getDepartureOffset().seconds();

            for (int i = 0; i < 4; i++) {
                m10_19_stoplist_ext.add(tsfactory.createTransitRouteStop(trstops.get("WD").get(i).getStopFacility(), (starttime + ((i + 1) * 60.)), (starttime + ((i + 1) * 60.))));
            }
            //System.out.println(m10_19_stoplist_ext);          // for troubleshooting: get new TransitRouteStopLists

            /** (5).5 Get original Departures, Description -> delete original Route -> Set new extended Route with all infos */
            // TODO: Get new TransitVehicles if original number of vehicles not sufficient for new schedule (with Julia's help)
            System.out.println("\t\t...Departures");
            //System.out.println("\t\t...TransitVehicles");     //???
            Map<Id<Departure>, Departure> m10indexDepartures = m10index.getDepartures();
            //String m10indexDescription = m10index.getDescription();         //???
            //Attributes m10indexAttributes = m10index.getAttributes();      //???

            System.out.println("\tRemove original TransitRoute and...");
            m10.removeRoute(m10index);

            // TODO: "19" should be replaced by all routes which have their terminus at Warschauer Str.
            // TODO: What happens with the routes that are currently starting at Warschauer Str. (with Julia's help?)
            TransitRoute m10_19_ext = tsfactory.createTransitRoute(Id.create(index, TransitRoute.class), m10route_ext, m10_19_stoplist_ext, "tram");
            for (Departure dep : m10indexDepartures.values()) {
                m10_19_ext.addDeparture(dep);
            }

            /** (5).6 Add new extended Route to existing TransitLine and write new TransitSchedule */
            System.out.println("\t...add new route with ALL informations into existing TransitLine");
            m10.addRoute(m10_19_ext);
        }
        new TransitScheduleWriter(scenario.getTransitSchedule()).writeFile(outputSchedule);

        System.out.println("\tRunning Validator...");
        TransitScheduleValidator.ValidationResult validationResult = new TransitScheduleValidator.ValidationResult();
        TransitScheduleValidator.validateAll(tschedule, network);
        System.out.print("\t\t"); TransitScheduleValidator.printResult(validationResult);
        System.out.println("\t...Done!");

        System.out.println("\t...Done!");
        System.out.println("### DONE! ###");

//        ## Maybe interesting outputs to look at for troubleshooting ##
//        System.out.println(m10);
//        System.out.println(m10_19_ext);
//        System.out.println(m10indexDepartures);
//        System.out.println(m10_19_stoplist_ext);
//        System.out.println(m10_19_route_ext);
//        System.out.println(m10_19_linkList_ext);

    }
}
