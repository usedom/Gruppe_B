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

    Map<String, NetworkRoute> build(Scenario scenario, Map<String, List<Id<Link>>> links){

        PopulationFactory pfactory = scenario.getPopulation().getFactory();
        Map<String, NetworkRoute> nwroutes = new HashMap<>(2);

        // routeWD contains 4 links
        NetworkRoute routeWD = pfactory.getRouteFactories().createRoute(NetworkRoute.class, links.get("WD").get(0), links.get("WD").get(links.get("WD").size()-1));
        routeWD.setLinkIds(links.get("WD").get(0), links.get("WD").subList(1, links.get("WD").size()-1), links.get("WD").get(links.get("WD").size()-1));

        // routeDW contains turning link + 4 links
        NetworkRoute routeDW = pfactory.getRouteFactories().createRoute(NetworkRoute.class, links.get("DW").get(0), links.get("DW").get(links.get("DW").size()-1));
        routeDW.setLinkIds(links.get("DW").get(0), links.get("DW").subList(1, links.get("DW").size()-1), links.get("DW").get(links.get("DW").size()-1));

        nwroutes.put("WD", routeWD);
        nwroutes.put("DW", routeDW);

        NWRoutes = nwroutes;

        return nwroutes;
    }
}
