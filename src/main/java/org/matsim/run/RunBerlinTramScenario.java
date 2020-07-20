/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2017 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.run;

import ch.sbb.matsim.routing.pt.raptor.RaptorIntermodalAccessEgress;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorModule;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.matsim.analysis.RunPersonTripAnalysis;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.drt.routing.DrtRoute;
import org.matsim.contrib.drt.routing.DrtRouteFactory;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigGroup;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.QSimConfigGroup.TrafficDynamics;
import org.matsim.core.config.groups.VspExperimentalConfigGroup;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.controler.OutputDirectoryLogging;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.gbl.MatsimRandom;
import org.matsim.core.population.routes.RouteFactories;
import org.matsim.core.router.AnalysisMainModeIdentifier;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.prepare.gruppeB.RunTramModifier;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt.utils.TransitScheduleValidator;
import org.matsim.run.drt.OpenBerlinIntermodalPtDrtRouterModeIdentifier;
import org.matsim.run.drt.RunDrtOpenBerlinScenario;
import org.matsim.run.singleTripStrategies.ChangeSingleTripModeAndRoute;
import org.matsim.run.singleTripStrategies.RandomSingleTripReRoute;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import static org.matsim.core.config.groups.ControlerConfigGroup.RoutingAlgorithmType.FastAStarLandmarks;

/**
 * @author ikaddoura
 */

public final class RunBerlinTramScenario {

    private static final Logger log = Logger.getLogger(RunBerlinScenario.class );

    public static void main(String[] args) {

        // Input files
        String configFile = "scenarios/berlin-v5.5-1pct/input/berlin-v5.5-1pct.config.xml";
        String inputNetwork = "scenarios/berlin-v5.5-1pct/input/berlin-matsim-v5.5-network.xml.gz";

        // Output files
        String outputNetwork = "scenarios/berlin-v5.5-1pct/input/berlin-tram_v3-network.xml.gz";
        String outputSchedule = "scenarios/berlin-v5.5-1pct/input/M10_WD-transitSchedule.xml.gz";

        for (String arg : args) {
            log.info( arg );
        }

        if ( args.length==0 ) {
            args = new String[] {configFile}  ;
        }


        /** Name of Tram Line */
        final String NAME = "M10";
        /** Choose Route option */
        final int OPTION = 4;
        final String OPT = String.valueOf(OPTION);

        /** Set input files */
        // TO DO: Get these ALL as parameters from RunTramModifier, but does not exist yet...
        // TO DO: Maybe also get networkFile locally and give as input from RunTramModifier...
/*

        File input = new File(outputNetwork);
        try{
            URL url = new URL("https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/berlin/berlin-v5.5-10pct/input/berlin-v5.5-network.xml.gz");
            FileUtils.copyURLToFile(url,input);
        }
        catch (IOException e){
            e.printStackTrace();
        }*/

        /** Load necessary files */
        Config config = prepareConfig( args ) ;
        config.controler().setOverwriteFileSetting(OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists);
        // Last iteration to xy
        config.controler().setLastIteration(2);
        // Set output folder
        config.controler().setOutputDirectory("./outputs/output_tram002ext");

        // Set readable Path for config (see "Set new inputs")
        String newnetwork = "./berlin-tram_v3-network.xml.gz";
        String newSchedule = "./M10_WD-transitSchedule.xml.gz";

        Scenario scenario = prepareScenario( config ) ;
        TransitSchedule tschedule = scenario.getTransitSchedule();

        // Run own TramModify (buildnew for new Line (actually unused and not working) | extend to extend existing lines)
        // new RunTramModifier().buildnew(scenario, inputNetwork, outputNetwork, tschedule, outputSchedule, NAME, OPTION);
        new RunTramModifier().extend(scenario, inputNetwork, outputNetwork, tschedule, outputSchedule, NAME, OPTION);

        // Set new inputs
        config.network().setInputFile(newnetwork);
        config.transit().setTransitScheduleFile(newSchedule);

        // Prepare scenario again and prepare Controler
        scenario = prepareScenario(config);
        Controler controler = prepareControler( scenario ) ;

        controler.run() ;
    }


    public static Controler prepareControler( Scenario scenario ) {
        // note that for something like signals, and presumably drt, one needs the controler object

        Gbl.assertNotNull(scenario);

        final Controler controler = new Controler( scenario );

        if (controler.getConfig().transit().isUseTransit()) {
            // use the sbb pt raptor router
            controler.addOverridingModule( new AbstractModule() {
                @Override
                public void install() {
                    install( new SwissRailRaptorModule() );
                }
            } );
        } else {
            log.warn("Public transit will be teleported and not simulated in the mobsim! "
                    + "This will have a significant effect on pt-related parameters (travel times, modal split, and so on). "
                    + "Should only be used for testing or car-focused studies with a fixed modal split.  ");
        }



        // use the (congested) car travel time for the teleported ride mode
        controler.addOverridingModule( new AbstractModule() {
            @Override
            public void install() {
                addTravelTimeBinding( TransportMode.ride ).to( networkTravelTime() );
                addTravelDisutilityFactoryBinding( TransportMode.ride ).to( carTravelDisutilityFactoryKey() );
                bind(AnalysisMainModeIdentifier.class).to(OpenBerlinIntermodalPtDrtRouterModeIdentifier.class);

                addPlanStrategyBinding("RandomSingleTripReRoute").toProvider(RandomSingleTripReRoute.class);
                addPlanStrategyBinding("ChangeSingleTripModeAndRoute").toProvider(ChangeSingleTripModeAndRoute.class);

                bind(RaptorIntermodalAccessEgress.class).to(BerlinRaptorIntermodalAccessEgress.class);
            }
        } );


        return controler;
    }

    public static Scenario prepareScenario( Config config ) {
        Gbl.assertNotNull( config );

        // note that the path for this is different when run from GUI (path of original config) vs.
        // when run from command line/IDE (java root).  :-(    See comment in method.  kai, jul'18
        // yy Does this comment still apply?  kai, jul'19

        /*
         * We need to set the DrtRouteFactory before loading the scenario. Otherwise DrtRoutes in input plans are loaded
         * as GenericRouteImpls and will later cause exceptions in DrtRequestCreator. So we do this here, although this
         * class is also used for runs without drt.
         */
        final Scenario scenario = ScenarioUtils.createScenario( config );

        RouteFactories routeFactories = scenario.getPopulation().getFactory().getRouteFactories();
        routeFactories.setRouteFactory(DrtRoute.class, new DrtRouteFactory());

        ScenarioUtils.loadScenario(scenario);

        BerlinExperimentalConfigGroup berlinCfg = ConfigUtils.addOrGetModule(config, BerlinExperimentalConfigGroup.class);
        if (berlinCfg.getPopulationDownsampleFactor() != 1.0) {
            downsample(scenario.getPopulation().getPersons(), berlinCfg.getPopulationDownsampleFactor());
        }

        return scenario;
    }

    public static Config prepareConfig( String [] args, ConfigGroup... customModules ){
        return prepareConfig( RunDrtOpenBerlinScenario.AdditionalInformation.none, args, customModules ) ;
    }
    public static Config prepareConfig( RunDrtOpenBerlinScenario.AdditionalInformation additionalInformation, String [] args,
                                        ConfigGroup... customModules ) {
        OutputDirectoryLogging.catchLogEntries();

        String[] typedArgs = Arrays.copyOfRange( args, 1, args.length );

        ConfigGroup[] customModulesToAdd = null ;
        if ( additionalInformation== RunDrtOpenBerlinScenario.AdditionalInformation.acceptUnknownParamsBerlinConfig ) {
            customModulesToAdd = new ConfigGroup[]{ new BerlinExperimentalConfigGroup(true) };
        } else {
            customModulesToAdd = new ConfigGroup[]{ new BerlinExperimentalConfigGroup(false) };
        }
        ConfigGroup[] customModulesAll = new ConfigGroup[customModules.length + customModulesToAdd.length];

        int counter = 0;
        for (ConfigGroup customModule : customModules) {
            customModulesAll[counter] = customModule;
            counter++;
        }

        for (ConfigGroup customModule : customModulesToAdd) {
            customModulesAll[counter] = customModule;
            counter++;
        }

        final Config config = ConfigUtils.loadConfig( args[ 0 ], customModulesAll );

        config.controler().setRoutingAlgorithmType( FastAStarLandmarks );

        config.subtourModeChoice().setProbaForRandomSingleTripMode( 0.5 );

        config.plansCalcRoute().setRoutingRandomness( 3. );
        config.plansCalcRoute().removeModeRoutingParams(TransportMode.ride);
        config.plansCalcRoute().removeModeRoutingParams(TransportMode.pt);
        config.plansCalcRoute().removeModeRoutingParams(TransportMode.bike);
        config.plansCalcRoute().removeModeRoutingParams("undefined");

        config.qsim().setInsertingWaitingVehiclesBeforeDrivingVehicles( true );



        // vsp defaults
        config.vspExperimental().setVspDefaultsCheckingLevel( VspExperimentalConfigGroup.VspDefaultsCheckingLevel.info );
        config.plansCalcRoute().setInsertingAccessEgressWalk( true );
        config.qsim().setUsingTravelTimeCheckInTeleportation( true );
        config.qsim().setTrafficDynamics( TrafficDynamics.kinematicWaves );

        // activities:
        for ( long ii = 600 ; ii <= 97200; ii+=600 ) {
            config.planCalcScore().addActivityParams( new ActivityParams( "home_" + ii + ".0" ).setTypicalDuration( ii ) );
            config.planCalcScore().addActivityParams( new ActivityParams( "work_" + ii + ".0" ).setTypicalDuration( ii ).setOpeningTime(6. * 3600. ).setClosingTime(20. * 3600. ) );
            config.planCalcScore().addActivityParams( new ActivityParams( "leisure_" + ii + ".0" ).setTypicalDuration( ii ).setOpeningTime(9. * 3600. ).setClosingTime(27. * 3600. ) );
            config.planCalcScore().addActivityParams( new ActivityParams( "shopping_" + ii + ".0" ).setTypicalDuration( ii ).setOpeningTime(8. * 3600. ).setClosingTime(20. * 3600. ) );
            config.planCalcScore().addActivityParams( new ActivityParams( "other_" + ii + ".0" ).setTypicalDuration( ii ) );
        }
        config.planCalcScore().addActivityParams( new ActivityParams( "freight" ).setTypicalDuration( 12.*3600. ) );

        ConfigUtils.applyCommandline( config, typedArgs ) ;

        return config ;
    }

    public static void runAnalysis(Controler controler) {
        Config config = controler.getConfig();

        String modesString = "";
        for (String mode: config.planCalcScore().getAllModes()) {
            modesString = modesString + mode + ",";
        }
        // remove last ","
        if (modesString.length() < 2) {
            log.error("no valid mode found");
            modesString = null;
        } else {
            modesString = modesString.substring(0, modesString.length() - 1);
        }

        String[] args = new String[] {
                config.controler().getOutputDirectory(),
                config.controler().getRunId(),
                "null", // TODO: reference run, hard to automate
                "null", // TODO: reference run, hard to automate
                config.global().getCoordinateSystem(),
                "https://svn.vsp.tu-berlin.de/repos/public-svn/matsim/scenarios/countries/de/berlin/projects/avoev/shp-files/shp-bezirke/bezirke_berlin.shp",
                TransformationFactory.DHDN_GK4,
                "SCHLUESSEL",
                "home",
                "100", // TODO: scaling factor, should be 10 for 10pct scenario and 100 for 1pct scenario
                "null", // visualizationScriptInputDirectory
                modesString
        };

        try {
            RunPersonTripAnalysis.main(args);
        } catch (IOException e) {
            log.error(e.getStackTrace());
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void downsample( final Map<Id<Person>, ? extends Person> map, final double sample ) {
        final Random rnd = MatsimRandom.getLocalInstance();
        log.warn( "Population downsampled from " + map.size() + " agents." ) ;
        map.values().removeIf( person -> rnd.nextDouble() > sample ) ;
        log.warn( "Population downsampled to " + map.size() + " agents." ) ;
    }

}

