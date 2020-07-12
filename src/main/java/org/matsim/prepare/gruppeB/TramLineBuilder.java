package org.matsim.prepare.gruppeB;

import org.apache.commons.io.FileUtils;
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
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.*;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehiclesFactory;
import scala.util.parsing.combinator.testing.Str;

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

    /** Name of Tram Line */
    final String NAME = "M10";  // TO DO: Get this as parameter from RunTramModifier, but does not exist yet...
    /** Choose Route option */
    final int OPTION = 4;       // TO DO: Get this as parameter from RunTramModifier, but does not exist yet...
    final String OPT = String.valueOf(OPTION);

    /** Set input files */
    // TO DO: Get these ALL as parameters from RunTramModifier, but does not exist yet...
    // TO DO: Maybe also get networkFile locally and give as input from RunTramModifier...
    String configFile = "scenarios/berlin-v5.5-1pct/input/berlin-v5.5-1pct.config.xml";
    String outputNetwork = "scenarios/berlin-v5.5-1pct/input/tram_modified-cloned-berlin-matsim.xml.gz";
    String outputSchedule = "scenarios/berlin-v5.5-1pct/input/M10new-transitSchedule.xml.gz";

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
    System.out.println("\tLoad given nodes (and create non-existing) and its (new) stop names...");
    LoadTramModifiyNodes loadNodesAnd = new LoadTramModifiyNodes(network,OPTION);
    List<Node> nodeList = loadNodesAnd.getList(OPTION);
    Map<Node, String> nodeMap = loadNodesAnd.getMap(OPTION,tschedule);
    //System.out.println(nodeMap);
    int size = nodeList.size();     // This size will be used quite often for iterations!
    System.out.println("\t...Done!");

    /** HINT: As we'll talk about two directions, remember those indices (could be changed, if you like...)
     * WD: Warschauer Str.  ->  "Destination"
     * DW: Destination      ->  Warschauer Str.
     *
     * To get specific information (links, routes, ...), you'll have to give this index in the Map
     * e.g. getinfo.get("WD");  //
     * */
    /** (2) Create new pt-links and get linkId-Map WD/DW */
    System.out.println("\tCreate and add new pt-links for both directions...");
    Map<String, List<Id<Link>>> links = new TramNetworkBuilder().build(network,nodeList,outputNetwork);
    System.out.println("\t...Done!");

    /** (3) Create and set new NetworkRoutes and get Networkroute-Map WD/DW */
    System.out.println("\tCreate NetworkRoutes with linkLists for both directions...");
    Map<String, NetworkRoute> nwroutes = new NetworkRouteBuilder().build(scenario,links);
    System.out.println("\t...Done!");

    /** (4) Get (and create non-existing) TransitRouteStops and get TransitRouteStop-Map WD/DW */
    // TO DO: Create new TransitStops if their is no TransitStop with those Coord - name???
    // Here done manually with one stop
    System.out.println("\tCreate/Get all TransitStops and put them into the right order...");
    Map<String,List<TransitRouteStop>> trstops = new TransitRouteStopBuilder().build(nodeList, nodeMap, links, tschedule);
    System.out.println("\t...Done!");

    /** (5) Define new TransitRoutes with informations from (3), (4), given names */
    System.out.println("\tCreate new TransitRoutes and put them into the right order...");
    TransitRoute m10WD = tsfactory.createTransitRoute(Id.create(NAME + "_" + OPT + "WD", TransitRoute.class), nwroutes.get("WD"), trstops.get("WD"), "tram");
    TransitRoute m10DW = tsfactory.createTransitRoute(Id.create(NAME + "_" + OPT + "DW", TransitRoute.class), nwroutes.get("DW"), trstops.get("DW"), "tram");
    System.out.println("\t...Done!");

    /** (6) Create new TramVehicles and new Departures for the TransitRoutes */
    // TO DO: Could bring this into extra class, for TramRouteModifier with adapted departure times (with Julia's help)
    System.out.println("\tCreate new TramVehicles and give them new Departures to TransitRoute...");
    for(int i=0; i<(12*9);i++){

        String m10vehicle = NAME + "_" + i;

        VehicleType tram_vtype = scenario.getTransitVehicles().getVehicleTypes().get(Id.create("Tram_veh_type", VehicleType.class));

        scenario.getTransitVehicles().addVehicle(tvfactory.createVehicle(Id.createVehicleId(m10vehicle), tram_vtype));

        Departure dep_WD = tsfactory.createDeparture(Id.create(m10vehicle, Departure.class), 10*3600.+i*(size-1)*60);
        dep_WD.setVehicleId(Id.createVehicleId(m10vehicle));
        m10WD.addDeparture(dep_WD);

        Departure dep_DW = tsfactory.createDeparture(Id.create(m10vehicle, Departure.class), 10*3600.+(i+1)*(size-1)*60);
        dep_DW.setVehicleId(Id.createVehicleId(m10vehicle));
        m10DW.addDeparture(dep_DW);
    }
    System.out.println("\t...Done!");

    /** (7) Create new TransitLine and add new TransitRoutes to it*/
    System.out.println("\tCreate new TramLine and add TransitRoutes...");
    TransitLine m10line = tsfactory.createTransitLine(Id.create("M10", TransitLine.class));
    m10line.addRoute(m10WD);
    m10line.addRoute(m10DW);
    System.out.println("\t...Done!");

    /** (8) Add ALL information of new line to TransitSchedule and write new TransitScheduleWriter */
    tschedule.addTransitLine(m10line);
    new TransitScheduleWriter(scenario.getTransitSchedule()).writeFile(outputSchedule);
    System.out.println("### DONE! ###");
    }

}

