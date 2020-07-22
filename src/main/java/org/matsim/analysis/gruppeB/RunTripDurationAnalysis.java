package org.matsim.analysis.gruppeB;

import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;

import java.io.IOException;
import java.util.Set;

public class RunTripDurationAnalysis {

    public static void main(String[] args) throws IOException {
        String inputFile = "output_ori050/berlin-v5.5-1pct.output_events.xml.gz";
        // Set your working folder
        String inputfolder = "C:/Users/Julia/Desktop/git/Gruppe_B/outputs/personcount/";
        System.setProperty("user.dir",inputfolder);

        EventsManager eventsManager = EventsUtils.createEventsManager();

        String kma_ori_west_enter = System.getProperty("user.dir")+"Agents entering KMA on west Link 97508.txt";
        String kma_ori_east_enter = System.getProperty("user.dir")+"Agents entering KMA on east Link 54738.txt";
        String kma_ori_west_leave = System.getProperty("user.dir")+"Agents leaving KMA on west Link 126333.txt";
        String kma_ori_east_leave = System.getProperty("user.dir")+"Agents leaving KMA on east Link 99708.txt";

        String kma_mod_west_enter = System.getProperty("user.dir")+"agents entering KMA_mod on west link 97508.txt";
        String kma_mod_east_enter = System.getProperty("user.dir")+"agents entering KMA_mod on east link 54738.txt";
        String kma_mod_west_leave = System.getProperty("user.dir")  +"agents leaving KMA_mod on west link 126333.txt";
        String kma_mod_east_leave = System.getProperty("user.dir")+"agents leaving KMA_mod on east link 99708.txt";

        Tripduration tripduration = new Tripduration();

        System.out.println("Merge ALL - Ori:");
        Set<String> ori_merge_all = tripduration.merge_all(kma_ori_west_enter, kma_ori_east_enter, kma_ori_west_leave, kma_ori_east_leave);
        tripduration.printout_terminal(ori_merge_all);

        System.out.println("Merge ALL -  Mod:");
        Set<String> mod_merge_all = tripduration.merge_all(kma_mod_west_enter, kma_mod_east_enter, kma_mod_west_leave, kma_mod_east_leave);
        tripduration.printout_terminal(mod_merge_all);

        // Set output file txt:
        String output_txt = System.getProperty("user.dir")+"PeopleEnteringAndLeaving_mod.txt";
        tripduration.printout_txt(mod_merge_all, output_txt);
        //--------------------------------------------------------------------------------------------

        VehiclesAndTheirTime vehiclesAndTheirTime = new VehiclesAndTheirTime();
        eventsManager.addHandler(vehiclesAndTheirTime);

        MatsimEventsReader eventsReader = new MatsimEventsReader(eventsManager);
        eventsReader.readFile(inputFile);

        vehiclesAndTheirTime.print();

    }
}
