package org.matsim.prepare.gruppeB;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.PopulationFactory;
import org.matsim.core.population.routes.NetworkRoute;
import org.matsim.pt.transitSchedule.api.TransitRouteStop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkRouteBuilder {

    Map<String, NetworkRoute> NWRoutes = new HashMap<>();

    Map<String, NetworkRoute> getNWRoutes(){
        if(!NWRoutes.isEmpty()){
            return NWRoutes;
        }
        else {
            System.out.println("ERROR! Build NetworkRoutes first!");
            return null;
        }
    }

    Map<String, NetworkRoute> buildnew(Scenario scenario, Map<String, List<Id<Link>>> links){
        System.out.println("\tCreate NetworkRoutes with linkLists for both directions...");

        PopulationFactory pfactory = scenario.getPopulation().getFactory();
        Map<String, NetworkRoute> nwroutes = new HashMap<>(2);

        Id<Link> firstLinkWD = links.get("WD").get(0);
        Id<Link> lastLinkWD = links.get("WD").get(links.get("WD").size()-1);
        Id<Link> firstLinkDW = links.get("DW").get(0);
        Id<Link> lastLinkDW = links.get("DW").get(links.get("DW").size()-1);

        // routeWD contains turning link + 4 links
        NetworkRoute routeWD = pfactory.getRouteFactories().createRoute(NetworkRoute.class, firstLinkWD, lastLinkWD);
        routeWD.setStartLinkId(firstLinkWD);
        routeWD.setEndLinkId(lastLinkWD);
        routeWD.setLinkIds(firstLinkWD,links.get("WD").subList(1, links.get("WD").size()-2),lastLinkWD);

        // routeDW contains turning link + 4 links
        NetworkRoute routeDW = pfactory.getRouteFactories().createRoute(NetworkRoute.class, firstLinkDW, lastLinkDW);
        routeDW.setStartLinkId(firstLinkDW);
        routeDW.setEndLinkId(lastLinkDW);
        routeDW.setLinkIds(firstLinkDW,links.get("DW").subList(1, links.get("DW").size()-2),lastLinkDW);

        nwroutes.put("WD", routeWD);
        nwroutes.put("DW", routeDW);

        NWRoutes = nwroutes;

        return nwroutes;
    }
}
