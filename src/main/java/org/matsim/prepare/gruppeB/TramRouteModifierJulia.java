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
import org.matsim.vehicles.VehiclesFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TramRouteModifierJulia {
    /*In diesem TramRouteModifier sollen die refLinkIds und die dazugehörigen Arrival/Departure Zeiten angegeben werden*/
    //hier werden die links und nodes im network file hinzugefügt und die stopFacilities im transitSchedule
    public static void main(String[] args) {

        /** Name of Tram Line */
        final String NAME = "M10";  // TO DO: Get this as parameter from RunTramModifier, but does not exist yet...
        final String ROUTE = "M10---17440_900";
        /** Choose Route option */
        final int OPTION = 4;       // TO DO: Get this as parameter from RunTramModifier, but does not exist yet...
        final String OPT = String.valueOf(OPTION);

        /** Set input files */
        // TO DO: Get these ALL as parameters from RunTramModifier, but does not exist yet...
        // TO DO: Maybe also get networkFile locally and give as input from RunTramModifier...
        String configFile = "scenarios/berlin-v5.5-1pct/input/berlin-v5.5-1pct.config.xml";
        String outputNetwork = "scenarios/berlin-v5.5-1pct/input/tram_modified-cloned-berlin-matsim.xml.gz";
        String outputSchedule = "scenarios/berlin-v5.5-1pct/input/M10_modified-transitSchedule.xml.gz";

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
        System.out.println("\tCreate and add new pt-links for both directions...");
        Map<String, List<Id<Link>>> links = new TramNetworkBuilder().build(network,nodeList,outputNetwork);
        System.out.println("\t...Done!");

        /** (3) Create and set new NetworkRoutes and get Networkroute-Map WD/DW */
        System.out.println("\tCreate NetworkRoutes with linkLists for both directions...");
        //Map<String, NetworkRoute> nwroutes = new NetworkRouteBuilder().build(scenario,links);
        System.out.println("\t...Done!");

        /** (4) Get (and create non-existing) TransitRouteStops and get TransitRouteStop-Map WD/DW */
        // TO DO: Create new TransitStops if their is no TransitStop with those Coord - name???
        // Here done manually with one stop
        System.out.println("\tCreate/Get all TransitStops and put them into the right order...");
        Map<String,List<TransitRouteStop>> trstops = new TransitRouteStopBuilder().build(nodeList, nodeMap, links, tschedule);
        System.out.println("\t...Done!");

        /** (5) Try to extend line with ROUTE to ROUTE_EXT */
        System.out.println("\tGet informations from existing Route and add new...");

        /** (5).1 Get M10 line, route (19) and clone original networkroute */
        TransitLine m10_ori = tschedule.getTransitLines().get(Id.create(ROUTE, TransitLine.class));
        // TO DO: Maybe a starting point to iterate on all necessary routes! (Start while-loop here...)
        TransitRoute m10_19_ori = m10_ori.getRoutes().get(Id.create(ROUTE+"_19", TransitRoute.class));
        NetworkRoute m10_19_route_ext = m10_19_ori.getRoute().clone();

        /** (5).2 Get original links and new links together -> NAME+ROUTE_linkList_ext */
        System.out.println("\t\t...links");
        List<Id<Link>> m10_19_linkList_ext = new ArrayList<>();
        m10_19_linkList_ext.addAll(m10_19_ori.getRoute().getLinkIds());
        // FIX ERROR: last link is not in getLinkIds() ... why? (see printouts for info, ask Dominik)
        //System.out.println(m10_19_linkList_ext);
        m10_19_linkList_ext.add(Id.createLinkId("pt_38360"));
        m10_19_linkList_ext.addAll(links.get("WD"));
        //System.out.println(m10_19_linkList_ext);

        /** (5).3 Set (5).2 into new NetworkRoute */
        System.out.println("\t\t...NetworkRoute(s)");
        int end = m10_19_linkList_ext.size();
        m10_19_route_ext.setLinkIds(m10_19_linkList_ext.get(0),m10_19_linkList_ext.subList(1,end-1),m10_19_linkList_ext.get(end-1));

        /** (5).4 Get original TransitRouteStops and extend by List from (4) */
        // TO DO: Edit variables (number of extended stops, departure time, ...) to be flexible with other routes! (with Julia's help)
        // TO DO: Better way to edit departures? Do inside new class maybe better?
        System.out.println("\t\t...TransitRouteStops");
        List<TransitRouteStop> m10_19_stoplist_ext = new ArrayList<>();
        m10_19_stoplist_ext.addAll(m10_19_ori.getStops());


        double starttime = m10_19_ori.getStops().get(m10_19_ori.getStops().size()-1).getDepartureOffset().seconds();

        for(int i=0;i<4;i++) {
            m10_19_stoplist_ext.add(tsfactory.createTransitRouteStop(trstops.get("WD").get(i + 1).getStopFacility(), (starttime + (i * 60.)), (starttime + (i * 60.) + 10.)));
        }

        /** (5).5 Get original Departures, Description -> delete original Route -> Set new extended Route with all infos */
        // TO DO: Get new TransitVehicles if original number of vehicles not sufficient for new schedule (with Julia's help)
        System.out.println("\t\t...Departures");
        //System.out.println("\t\t...TransitVehicles");
        System.out.println("\tRemove original TransitRoute and...");
        Map<Id<Departure>, Departure> m10_19_oriDepartures = m10_19_ori.getDepartures();
        String m10_19_oriDescription = m10_19_ori.getDescription();

        //coding part Julia

        Id<TransitLine> transitLineM10 = Id.create("M10---17440_900", TransitLine.class);
        List<Id<TransitRoute>> trlineM10 = new ArrayList<>(scenario.getTransitSchedule().getTransitLines().get(transitLineM10).getRoutes().keySet());
        List<Id<TransitRoute>> tramM10routeid = new ArrayList<>();

        for (int i = 0; i < trlineM10.size(); i++){
            if (String.valueOf(trlineM10.get(i)).contains("M10")){
                tramM10routeid.add(trlineM10.get(i));
            }
        }

        System.out.println("Hier Anfang -------------------------");
        //Was muss modifiziert werden? (1) routes: Start link, Strecke, End Link; (2) route Profile:  ; (3) departures

        //Variablen deklarieren damit Schleife übersichtlich bleibt

        List<Id<Link>> newlinkidsDW = new ArrayList<>();
        newlinkidsDW.add(Id.createLinkId("pt_M10_4DW-1"));
        newlinkidsDW.add(Id.createLinkId("pt_M10_4DW-2"));
        newlinkidsDW.add(Id.createLinkId("pt_M10_4DW-3"));
        newlinkidsDW.add(Id.createLinkId("pt_M10_4DW-4"));

        List<Id<Link>> newlinkidsWD = new ArrayList<>();
        newlinkidsWD.add(Id.createLinkId("pt_M10_4WD-1"));
        newlinkidsWD.add(Id.createLinkId("pt_M10_4WD-2"));
        newlinkidsWD.add(Id.createLinkId("pt_M10_4WD-3"));

        for (Id<TransitRoute> id:tramM10routeid) {
            //Alte infos abspeichern
            TransitRoute oldRoute = scenario.getTransitSchedule().getTransitLines().get(transitLineM10).getRoutes().get(id);
            List<TransitRouteStop> oldstoplist = scenario.getTransitSchedule().getTransitLines().get(transitLineM10).getRoutes().get(id).getStops();
            Map<Id<Departure>, Departure> olddepartures = scenario.getTransitSchedule().getTransitLines().get(transitLineM10).getRoutes().get(id).getDepartures();
            String olddescription = scenario.getTransitSchedule().getTransitLines().get(transitLineM10).getRoutes().get(id).getDescription();
            String oldtransportMode = scenario.getTransitSchedule().getTransitLines().get(transitLineM10).getRoutes().get(id).getTransportMode();
            List<Id<Link>> oldlinkids = scenario.getTransitSchedule().getTransitLines().get(transitLineM10).getRoutes().get(id).getRoute().getLinkIds();
            Id<Link> oldstartlinkid = scenario.getTransitSchedule().getTransitLines().get(transitLineM10).getRoutes().get(id).getRoute().getStartLinkId();
            Id<Link> oldendlinkid = scenario.getTransitSchedule().getTransitLines().get(transitLineM10).getRoutes().get(id).getRoute().getEndLinkId();
            NetworkRoute oldroute = scenario.getTransitSchedule().getTransitLines().get(transitLineM10).getRoutes().get(id).getRoute();

            //neue Variablen deklarieren
            NetworkRoute newroute = oldroute.clone();
            List<TransitRouteStop> newstoplist = new ArrayList<>();

            //Route aus der TransitSchedule löschen
            scenario.getTransitSchedule().getTransitLines().get(transitLineM10).removeRoute(oldRoute);

            //neue Infos erstellen
            /**(1) routes: Start link, Strecke, End Link**/
            //Das wäre die Richtung von Hermannplatz über Warschauer nach Lüneburger
            if (oldstartlinkid.equals(Id.createLinkId("pt_38323"))) {
                newroute.setStartLinkId(Id.createLinkId("pt_M10_4DW-0"));
                newlinkidsDW.addAll(oldlinkids);
                newroute.setLinkIds(Id.createLinkId("pt_M10_4DW-0"), newlinkidsDW, oldendlinkid);
            }
            //Das wäre die Richtung von Lüneburger über Warschauer nach Hermannplatz
            if (oldendlinkid.equals(Id.createLinkId("pt_38360"))) {
                newroute.setEndLinkId(Id.createLinkId("pt_M10_4WD-4"));
                //List<Id<Link>> oldlinkIds = scenario.getTransitSchedule().getTransitLines().get(transitLineM10).getRoutes().get(id).getRoute().getLinkIds();
                List<Id<Link>> newlinkIds = new ArrayList<>();
                newlinkIds.addAll(oldlinkids);
                newlinkIds.add(Id.createLinkId("pt_38360"));
                newlinkIds.addAll(newlinkidsWD);
                newroute.setLinkIds(oldstartlinkid, newlinkIds, Id.createLinkId("pt_M10_4WD-4"));
            }
            //Bei den Zeiten überall 4 min als offset drauf!!!
            /**(2) route Profile: stop Ref Id, arrivaloffset, departureoffset, awaitDeparture**/
            //Das wäre die Richtung von Hermannplatz über Warschauer nach Lüneburger
            if (oldstoplist.get(0).getStopFacility().getId().equals(Id.create("070301008821", TransitStopFacility.class))) {
                for(int i=0;i<4;i++) {
                    newstoplist.add(tsfactory.createTransitRouteStop(trstops.get("DW").get(i).getStopFacility(), (60 * i), (60 * i)));
                    newstoplist.get(i).setAwaitDepartureTime(true);
                }
                for (int n=0; n<oldstoplist.size(); n++){
                    newstoplist.add(tsfactory.createTransitRouteStop(oldstoplist.get(n).getStopFacility(),oldstoplist.get(n).getDepartureOffset().seconds()+240, oldstoplist.get(n).getArrivalOffset().seconds()+240));
                    newstoplist.get(n+4).setAwaitDepartureTime(true);
                }
            }
            int nofstops = oldstoplist.size();
            //Das wäre die Richtung von Lüneburger über Warschauer nach Hermannplatz
            if (oldstoplist.get(nofstops-1).getStopFacility().getId().equals(Id.create("070301008819", TransitStopFacility.class))) {
                double doffset = oldstoplist.get(nofstops - 1).getDepartureOffset().seconds();
                double aoffset = oldstoplist.get(nofstops - 1).getArrivalOffset().seconds();
                newstoplist.addAll(oldstoplist);
                for(int i=0;i<4;i++) {
                    newstoplist.add(tsfactory.createTransitRouteStop(trstops.get("WD").get(i + 1).getStopFacility(), doffset+(60*(i+1)), aoffset+(60*(i+1))));
                    newstoplist.get(i).setAwaitDepartureTime(true);
                }
            }

            TransitRoute newtransitroute = tsfactory.createTransitRoute(id,newroute,newstoplist,oldtransportMode);

            /**(3) departures: departure ID, Departure Time, vehicle Ref Id, awaitDeparture**/
            //die Departure Time muss eigentlich nur auf der Strecke H->W->L angepasst werden
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
            scenario.getTransitSchedule().getTransitLines().get(transitLineM10).addRoute(newtransitroute);
            new TransitScheduleWriter(scenario.getTransitSchedule()).writeFile(outputSchedule);
        }

        System.out.println("Hier Ende -------------------------");

        /** (5).6 Add new extended Route to existing TransitLine and write new TransitSchedule */
        System.out.println("\t...Done!");
        System.out.println("### DONE! ###");
    }
}
