package org.matsim.prepare.gruppeB;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.pt.transitSchedule.api.*;
import org.matsim.pt.utils.TransitScheduleValidator;
import org.matsim.vehicles.Vehicle;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehiclesFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RunTramModifier {

//    Scenario scenario=null;
//    int choice = 0; // no changes
//
//    public RunTramModifier(Scenario scenario, int routeOption){
//        this.scenario = scenario;
//        this.choice = routeOption;
//    }

    public void buildnew(Scenario scenario, String inputNetwork, String outputNetwork, TransitSchedule tschedule, String outputSchedule, String name, int choice) {

        /** Name of Tram Line */
        final String NAME = name;  // TO DO: Get this as parameter from RunTramModifier, but does not exist yet...
        /** Choose Route option */
        final int OPTION = choice;       // TO DO: Get this as parameter from RunTramModifier, but does not exist yet...
        final String OPT = String.valueOf(OPTION);

        /** Set input files */
        // TO DO: Get these ALL as parameters from RunTramModifier, but does not exist yet...
        // TO DO: Maybe also get networkFile locally and give as input from RunTramModifier...
        //String configFile = "scenarios/berlin-v5.5-1pct/input/berlin-v5.5-1pct.config.xml";
        //String outputNetwork = "scenarios/berlin-v5.5-1pct/input/tram_modified-cloned-berlin-matsim.xml.gz";
        //String outputSchedule = "scenarios/berlin-v5.5-1pct/input/M10new-transitSchedule.xml.gz";

        /*File input = new File(outputNetwork);

        try{
            URL url = new URL("https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/berlin/berlin-v5.5-10pct/input/berlin-v5.5-network.xml.gz");
            FileUtils.copyURLToFile(url,input);
        }
        catch (IOException e){
            e.printStackTrace();
        }*/

        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(inputNetwork);

        /** Load necessary files */
        // Get at least config and scenario from RunTramModifier (when existing)
        //Config config = ConfigUtils.loadConfig(configFile);
        //Scenario scenario = ScenarioUtils.loadScenario(config);
        System.out.println("### Build new line "+NAME+" ! ###");
        //TransitSchedule tschedule = scenario.getTransitSchedule();
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
    Map<String, List<Id<Link>>> links = new TramNetworkBuilder_2().build(network,nodeList,outputNetwork);
    System.out.println("\t...Done!");

        /** (3) Create and set new NetworkRoutes and get Networkroute-Map WD/DW */
        System.out.println("\tCreate NetworkRoutes with linkLists for both directions...");
        Map<String, NetworkRoute> nwroutes = new NetworkRouteBuilder().build(scenario,links);
        System.out.println("\t...Done!");

        /** (4) Get (and create non-existing) TransitRouteStops and get TransitRouteStop-Map WD/DW */
        // TO DO: Create new TransitStops if their is no TransitStop with those Coord - name???
        // Here done manually with one stop
        System.out.println("\tCreate/Get all TransitStops and put them into the right order...");
        Map<String,List<TransitRouteStop>> trstops = new TransitRouteStopBuilder_old().build(nodeList, nodeMap, links, tschedule);
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

        String vehiclename = NAME + "_" + i;

        VehicleType tram_vtype = scenario.getTransitVehicles().getVehicleTypes().get(Id.create("Tram_veh_type", VehicleType.class));

        Id<Vehicle> m10vehicle = Id.createVehicleId(vehiclename);

        scenario.getTransitVehicles().addVehicle(tvfactory.createVehicle(m10vehicle, tram_vtype));

        Departure dep_WD = tsfactory.createDeparture(Id.create(vehiclename, Departure.class), 10*3600.+(i*(size-1)*60)+5.);  // Departures: 10:00:05, 10:05:05, 10:10:05, ...
        dep_WD.setVehicleId(m10vehicle);                                                                                        // Arrivals would be 10:04:00, 10:09:00, ...
        m10WD.getRoute().setTravelCost(1.);
        m10WD.getRoute().setVehicleId(m10vehicle);
        m10WD.addDeparture(dep_WD);

        Departure dep_DW = tsfactory.createDeparture(Id.create(vehiclename, Departure.class), 10*3600.+((i+1)*(size-1)*60)+5.);     // Departures: 10:05:05, 10:10:05, 10:15:05, ...
        m10DW.getRoute().setTravelCost(1.);                                                                                            // Arrivals would be 10:09:00, 10:14:00, ...
        m10DW.getRoute().setVehicleId(m10vehicle);
        dep_DW.setVehicleId(Id.createVehicleId(vehiclename));
        m10DW.addDeparture(dep_DW);
    }
    System.out.println("\t...Done!");

        /** (7) Create new TransitLine and add new TransitRoutes to it*/
        System.out.println("\tCreate new TramLine and add TransitRoutes...");
        if(tschedule.getTransitLines().containsKey(Id.create(name+"_new", TransitLine.class))){
            tschedule.removeTransitLine(tschedule.getTransitLines().get(Id.create(name+"_new", TransitLine.class)));
        }
        if(tschedule.getTransitLines().containsKey(Id.create(name, TransitLine.class))){
            tschedule.removeTransitLine(tschedule.getTransitLines().get(Id.create(name, TransitLine.class)));
        }
        TransitLine m10line = tsfactory.createTransitLine(Id.create(name+"_new", TransitLine.class));
        m10line.addRoute(m10WD);
        m10line.addRoute(m10DW);
        System.out.println("\t...Done!");

        /** (8) Add ALL information of new line to TransitSchedule and write new TransitScheduleWriter */
        tschedule.addTransitLine(m10line);
        new TransitScheduleWriter(scenario.getTransitSchedule()).writeFile(outputSchedule);
        System.out.println("### DONE! ###");
    }

    public void extend(Scenario scenario, String inputNetwork, String outputNetwork, TransitSchedule tschedule, String outputSchedule, String name, int choice){
        /** Name of Tram Line (getting from RunBerlinTramScenario) */
        final String NAME = name;
        final String LINE = name+"---17440_900";
        /** Choose Route option (getting from RunBerlinTramScenario) */
        final int OPTION = choice;
        final String OPT = String.valueOf(OPTION);

        /** Set input files */

        Network network = NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(String.valueOf(inputNetwork));

        /** Load necessary files */
        System.out.println("### Extend routes of line "+NAME+" ! ###");
        TransitScheduleFactory tsfactory = tschedule.getFactory();
        // tvfactory might be necessary when including adapted schedule (with Julia's help)
        // VehiclesFactory tvfactory = scenario.getTransitVehicles().getFactory();

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

        /** (3) Get (and create non-existing) TransitRouteStops and get TransitRouteStop-Map WD/DW */
        // Idea: Create new TransitStops if their is no TransitStop with those Coord - name???
        Map<String,List<TransitRouteStop>> trstops = new TransitRouteStopBuilder().build(nodeList, nodeMap, links, tschedule);
        System.out.println("\t...Done!");

        //coding part Julia
        /** (4) Get line "LINE" and its specific routes  */
        Id<TransitLine> transitLineM10 = Id.create(LINE, TransitLine.class);
        List<Id<TransitRoute>> trlineM10 = new ArrayList<>(tschedule.getTransitLines().get(transitLineM10).getRoutes().keySet());
        List<Id<TransitRoute>> tramM10routeid = new ArrayList<>();

        for (int i = 0; i < trlineM10.size(); i++){
            if (String.valueOf(trlineM10.get(i)).contains(NAME)){
                tramM10routeid.add(trlineM10.get(i));
            }
        }

        /** (5) Save informations from existing route and add new informations from above **/
        //Was muss modifiziert werden? (5.1) routes: Start link, Strecke, End Link; (5.2) route Profile:  ; (5.3) departures
        //Variablen deklarieren damit Schleife übersichtlich bleibt

        List<Id<Link>> newlinkidsDW = new ArrayList<>();            // todo: über schon existierende links.get("..").get(x) lösen? ab hier existieren nun 2 doppelte link-listen
        newlinkidsDW.add(links.get("DW").get(1));
        newlinkidsDW.add(links.get("DW").get(2));
        newlinkidsDW.add(links.get("DW").get(3));
        newlinkidsDW.add(links.get("DW").get(4));

        List<Id<Link>> newlinkidsWD = new ArrayList<>();
        newlinkidsWD.add(links.get("WD").get(1));
        newlinkidsWD.add(links.get("WD").get(2));
        newlinkidsWD.add(links.get("WD").get(3));

        for (Id<TransitRoute> id:tramM10routeid) {
            //Alte infos abspeichern
            TransitRoute oldRoute = tschedule.getTransitLines().get(transitLineM10).getRoutes().get(id);
            List<TransitRouteStop> oldstoplist = tschedule.getTransitLines().get(transitLineM10).getRoutes().get(id).getStops();
            Map<Id<Departure>, Departure> olddepartures = tschedule.getTransitLines().get(transitLineM10).getRoutes().get(id).getDepartures();
            String olddescription = tschedule.getTransitLines().get(transitLineM10).getRoutes().get(id).getDescription();               // todo: wird das gebraucht? vielleicht später
            String oldtransportMode = tschedule.getTransitLines().get(transitLineM10).getRoutes().get(id).getTransportMode();
            List<Id<Link>> oldlinkids = tschedule.getTransitLines().get(transitLineM10).getRoutes().get(id).getRoute().getLinkIds();
            Id<Link> oldstartlinkid = tschedule.getTransitLines().get(transitLineM10).getRoutes().get(id).getRoute().getStartLinkId();
            Id<Link> oldendlinkid = tschedule.getTransitLines().get(transitLineM10).getRoutes().get(id).getRoute().getEndLinkId();
            NetworkRoute oldroute = tschedule.getTransitLines().get(transitLineM10).getRoutes().get(id).getRoute();

            //neue Variablen deklarieren
            List<Id<Link>> newlinkIds = new ArrayList<>();
            NetworkRoute newroute = oldroute.clone();
            List<TransitRouteStop> newstoplist = new ArrayList<>();

            //Route aus der TransitSchedule löschen
            tschedule.getTransitLines().get(transitLineM10).removeRoute(oldRoute);

            //neue Infos erstellen
            /**(5.1) routes: Start link, Strecke, End Link**/                               // todo: über schon existierende NetworkRouteBuilder lösen? Übergabeparameter anpassen
            //Das wäre die Richtung von Hermannplatz über Warschauer nach Lüneburger
            if (oldstartlinkid.equals(Id.createLinkId("pt_38323"))) {
                newroute.setStartLinkId(links.get("DW").get(0));
                newroute.setEndLinkId(oldendlinkid);
                newlinkIds.addAll(newlinkidsDW);
                newlinkIds.addAll(oldlinkids);
                newroute.setLinkIds(links.get("DW").get(0), newlinkIds, oldendlinkid);
            }
            //Das wäre die Richtung von Lüneburger über Warschauer nach Hermannplatz
            if (oldendlinkid.equals(Id.createLinkId("pt_38360"))) {
                newroute.setStartLinkId(oldstartlinkid);
                newroute.setEndLinkId(links.get("WD").get(4));
                newlinkIds.addAll(oldlinkids);
                newlinkIds.add(Id.createLinkId("pt_38360"));        // todo: = oldendlinkid?
                newlinkIds.addAll(newlinkidsWD);
                newroute.setLinkIds(oldstartlinkid, newlinkIds, links.get("WD").get(4));
            }
            //Bei den Zeiten überall 4 min als offset drauf!!!
            /**(5.2) route Profile: stop Ref Id, arrivaloffset, departureoffset, awaitDeparture**/
            //Das wäre die Richtung von Hermannplatz über Warschauer nach Lüneburger
            if (oldstoplist.get(0).getStopFacility().getId().equals(Id.create("070301008821", TransitStopFacility.class))) {
                for(int i=0;i<4;i++) {
                    newstoplist.add(tsfactory.createTransitRouteStop(trstops.get("DW").get(i).getStopFacility(), (60 * i), (60 * i)));
                    newstoplist.get(i).setAwaitDepartureTime(true);
                }
                for (int n=0; n<oldstoplist.size(); n++){
                    if (n==0){
                        newstoplist.add(tsfactory.createTransitRouteStop(trstops.get("DW").get(4).getStopFacility(), oldstoplist.get(n).getDepartureOffset().seconds() + 240, oldstoplist.get(n).getArrivalOffset().seconds() + 240));
                        newstoplist.get(n + 4).setAwaitDepartureTime(true);
                    }
                    if (n>0) {
                        newstoplist.add(tsfactory.createTransitRouteStop(oldstoplist.get(n).getStopFacility(), oldstoplist.get(n).getDepartureOffset().seconds() + 240, oldstoplist.get(n).getArrivalOffset().seconds() + 240));
                        newstoplist.get(n + 4).setAwaitDepartureTime(true);
                    }
                }
            }
            int nofstops = oldstoplist.size();
            //Das wäre die Richtung von Lüneburger über Warschauer nach Hermannplatz
            if (oldstoplist.get(nofstops-1).getStopFacility().getId().equals(Id.create("070301008819", TransitStopFacility.class))) {
                double doffset = oldstoplist.get(nofstops - 1).getDepartureOffset().seconds();
                double aoffset = oldstoplist.get(nofstops - 1).getArrivalOffset().seconds();
                newstoplist.addAll(oldstoplist);
                for(int i=0;i<4;i++) {
                    newstoplist.add(tsfactory.createTransitRouteStop(trstops.get("WD").get(i).getStopFacility(), doffset+(60*(i+1)), aoffset+(60*(i+1))));
                    newstoplist.get(i).setAwaitDepartureTime(true);
                }
            }
            if (!oldstoplist.get(0).getStopFacility().getId().equals(Id.create("070301008821", TransitStopFacility.class)) && !oldstoplist.get(nofstops-1).getStopFacility().getId().equals(Id.create("070301008819", TransitStopFacility.class))){
                //  for (int i=0;i<4;i++)
                newstoplist.addAll(oldstoplist);
                //newstoplist.get().setAwaitDepartureTime(true);
            }

            TransitRoute newtransitroute = tsfactory.createTransitRoute(id,newroute,newstoplist,oldtransportMode);

            /**(5.3) departures: departure ID, Departure Time, vehicle Ref Id, awaitDeparture**/
            //die Departure Time muss eigentlich nur auf der Strecke H->W->L angepasst werden
            System.out.println("\tCreate new Departures to new TransitRoute and rewrite TransitSchedule...");
            for (Id<Departure> did:olddepartures.keySet()) {
                if (oldstartlinkid.equals(Id.createLinkId("pt_38323"))) {
                    newtransitroute.addDeparture(tsfactory.createDeparture(did, olddepartures.get(did).getDepartureTime()-240));
                    newtransitroute.getDepartures().get(did).setVehicleId(olddepartures.get(did).getVehicleId());
                } else {
                    newtransitroute.addDeparture(tsfactory.createDeparture(did, olddepartures.get(did).getDepartureTime()));
                    newtransitroute.getDepartures().get(did).setVehicleId(olddepartures.get(did).getVehicleId());
                }
            }
            //erstelle eine neue transitroute und schreib sie in TransitSchedule
            tschedule.getTransitLines().get(transitLineM10).addRoute(newtransitroute);
        }

        new TransitScheduleWriter(tschedule).writeFile(outputSchedule);

        /** (6) Add new extended Route to existing TransitLine, run Validator and write new TransitSchedule */
        System.out.println("\t...Done!");
        System.out.println("\tRunning TransitScheduleValidator...");
        TransitScheduleValidator.ValidationResult validationResult = new TransitScheduleValidator.ValidationResult();
        TransitScheduleValidator.validateAll(tschedule, network);
        System.out.print("\t\t"); TransitScheduleValidator.printResult(validationResult);
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
