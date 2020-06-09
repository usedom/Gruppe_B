package org.matsim.analysis.gruppeB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MergeKMAHandler {

    public static void main(String[] args) throws IOException {

        // Set your working folder
        String inputfolder = "C:/Users/djp/Desktop/task 3/";
        System.setProperty("user.dir",inputfolder);

        String kma_ori_west_enter = System.getProperty("user.dir")+"Agents entering KMA on west Link 97508.txt";
        String kma_ori_east_enter = System.getProperty("user.dir")+"Agents entering KMA on east Link 54738.txt";
        String kma_ori_west_leave = System.getProperty("user.dir")+"Agents leaving KMA on west Link 126333.txt";
        String kma_ori_east_leave = System.getProperty("user.dir")+"Agents leaving KMA on east Link 99708.txt";

        String kma_mod_west_enter = System.getProperty("user.dir")+"agents entering KMA_mod on west link 97508.txt";
        String kma_mod_east_enter = System.getProperty("user.dir")+"agents entering KMA_mod on east link 54738.txt";
        String kma_mod_west_leave = System.getProperty("user.dir")+"agents leaving KMA_mod on west link 126333.txt";
        String kma_mod_east_leave = System.getProperty("user.dir")+"agents leaving KMA_mod on east link 99708.txt";

        HandleKMAPersonLists mergeKM = new HandleKMAPersonLists();

        System.out.println("Merge ALL - Ori:");
        Set<String> ori_merge_all = mergeKM.merge_all(kma_ori_west_enter, kma_ori_east_enter, kma_ori_west_leave, kma_ori_east_leave);

        System.out.println("Merge ALL - Mod:");
        Set<String> mod_merge_all = mergeKM.merge_all(kma_mod_west_enter, kma_mod_east_enter, kma_mod_west_leave, kma_mod_east_leave);

        // Set output merge file txt, print Set:
        String output_merge = System.getProperty("user.dir")+"merge_all.txt";
        mergeKM.printout_set_txt(mod_merge_all, output_merge);

        // Comparison of KMA vs KMA_mod
        // IDs that do not use KMA after modification
        // IDs that use KMA as new route after modification
        // IDs that use KMA before and after modification
        // -> output in terminal
        mergeKM.compareIDappearance(ori_merge_all, mod_merge_all);

        // Get compareIDapperance as List< ** > for further use
        List<List<String>> compareIDapperance = mergeKM.compareIDappearance_list(ori_merge_all, mod_merge_all);

        // Set output compare file txt, print List you need < ** >:
        // ** = <List 0> IDs that do not use KMA after modification
        String output_compare_0 = System.getProperty("user.dir")+"onlykma.txt";
        mergeKM.printout_list_txt(compareIDapperance.get(0), output_compare_0);

        // ** = <List 1> IDs that use KMA as new route after modification
        String output_compare_1 = System.getProperty("user.dir")+"onlykma_mod.txt";
        mergeKM.printout_list_txt(compareIDapperance.get(1), output_compare_1);

        // ** = <List 2> IDs that use KMA before and after modification
        String output_compare_2 = System.getProperty("user.dir")+"bothkma.txt";
        mergeKM.printout_list_txt(compareIDapperance.get(2), output_compare_2);

    }
}
